package com.example.springboot;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import okhttp3.*;
import org.mockito.ArgumentCaptor;
import org.springframework.util.StringUtils;

public class MethodCallHandler {
  private final String packageName = this.getClass().getPackageName();
  private final TypeConverter typeConverter;
  private ClassLoader classLoader;

  MethodCallHandler(TypeConverter typeConverter) {
    this.typeConverter = typeConverter;
  }

  protected MethodResponse callMethod(String methodName, List<String> parameters)
      throws RuntimeException {
    // Putting null without quotes here is incorrect! We're just trying to keep the JSON
    // serialiser/deserialiser
    // happy with our mocks.
    return callMethod(methodName, parameters, "null", 0);
  }

  protected MethodResponse callMethod(String methodName, List<String> parameters, String response)
      throws RuntimeException {
    return callMethod(methodName, parameters, response, 0);
  }

  protected MethodResponse callMethod(
      String methodName, List<String> parameters, String response, int serverIndex)
      throws RuntimeException {
    try {
      Response mockResponse = mock(Response.class);
      ResponseBody mockResponseBody = mock(ResponseBody.class);
      when(mockResponse.body()).thenReturn(mockResponseBody);
      when(mockResponseBody.string()).thenReturn(response);
      Call mockCall = mock(Call.class);
      OkHttpClient mockHttp = mock(OkHttpClient.class);
      ArgumentCaptor<Request> argumentCaptor = ArgumentCaptor.forClass(Request.class);
      when(mockHttp.newCall(argumentCaptor.capture())).thenReturn(mockCall);
      when(mockCall.execute()).thenReturn(mockResponse);

      // Compile source file.
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      File srcMain = new File("src/main/java/" + packageName.replaceAll("\\.", "/"));
      File[] filesInSrcMain = srcMain.listFiles();
      if (filesInSrcMain != null) {
        String[] filePaths = new String[filesInSrcMain.length];
        for (int i = 0; i < filesInSrcMain.length; i++) {
          filePaths[i] = filesInSrcMain[i].getPath();
          System.err.println("compiling " + filePaths[i]);
        }
        compiler.run(null, null, null, filePaths);
      }

      // Load and instantiate compiled class.
      File root = new File("src/main/java/");
      //      System.err.println(root.getCanonicalPath());
      URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {root.toURI().toURL()});
      this.classLoader = classLoader;

      Class<?> apiClientClass = Class.forName(packageName + ".ApiClient", true, classLoader);
      Object apiClient = createApiClient(apiClientClass, mockHttp, serverIndex);

      Method[] allMethods = apiClientClass.getDeclaredMethods();
      Method methodWithParameters =
          Arrays.stream(allMethods)
              .filter(m -> m.getName().equals(methodName))
              .findFirst()
              .orElseThrow();

      Object[] convertedParameters =
          typeConverter.convertBoxedTypes(
              parameters.toArray(new String[0]), methodWithParameters.getParameterTypes());
      Object objectResponse =
          methodWithParameters.invoke(
              apiClient, convertedParameters); // ONLY WORKS WITH BOXED VALUES
      return new MethodResponse(objectResponse, argumentCaptor.getValue());
    } catch (IOException
        | ClassNotFoundException
        | NoSuchMethodException
        | InvocationTargetException
        | InstantiationException
        | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public String getPropertyOnObject(
      String propName, Object latestResponse, String latestResponseType) {
    try {
      Class<?> propClass = Class.forName(packageName + "." + latestResponseType, true, classLoader);
      Method getProp = propClass.getDeclaredMethod("get" + StringUtils.capitalize(propName));
      getProp.setAccessible(true); // Otherwise causes IllegalAccessException.
      return getProp.invoke(latestResponse).toString();
    } catch (ClassNotFoundException
        | NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private Object createApiClient(Class<?> apiClientClass, OkHttpClient mockHttp, int serverIndex)
      throws NoSuchMethodException, InvocationTargetException, InstantiationException,
          IllegalAccessException, ClassNotFoundException {
    Class<?> configurationClass = Class.forName(packageName + ".Configuration", true, classLoader);

    Object configuration = configurationClass.getDeclaredConstructor().newInstance();
    setBasePath(configurationClass, configuration);

    Method setSelectedServerIndex =
        configurationClass.getDeclaredMethod("setSelectedServerIndex", int.class);
    setSelectedServerIndex.invoke(configuration, serverIndex);

    return apiClientClass
        .getDeclaredConstructor(OkHttpClient.class, configurationClass)
        .newInstance(mockHttp, configuration);
  }

  private void setBasePath(Class<?> configurationClass, Object configuration)
      throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    Method getServers = configurationClass.getDeclaredMethod("getServers");
    List<String> servers = (List<String>) getServers.invoke(configuration);
    Method setBasePath = configurationClass.getDeclaredMethod("setBasePath", String.class);
    if (servers.size() == 0) {
      // The default case is where the servers property in the schema is undefined. OpenAPI
      // allows this if the schema file is hosted on the same server to request from and in the
      // correct relative location.
      // https://spec.openapis.org/oas/v3.1.0#fixed-fields
      // It doesn't matter what the URL is for the purpose of these tests:
      setBasePath.invoke(configuration, "https://example.com/");
      Method setServers = configurationClass.getDeclaredMethod("setServers", List.class);
      setServers.invoke(configuration, List.of("api/v3"));
    } else {
      setBasePath.invoke(configuration, "");
    }
  }
}
