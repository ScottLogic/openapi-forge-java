package com.example.springboot;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
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
      OkHttpClient mockHttp = mock(OkHttpClient.class);
      ArgumentCaptor<Request> requestArgumentCaptor = ArgumentCaptor.forClass(Request.class);

      createOkHttpMocks(response, mockHttp, requestArgumentCaptor);
      compileFilesInPackage();
      ClassLoader classLoader = createClassLoaderForPackage();

      Class<?> apiClientClass = Class.forName(packageName + ".ApiClient", true, classLoader);
      Object apiClient = createApiClient(apiClientClass, mockHttp, serverIndex, classLoader);

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
      return new MethodResponse(objectResponse, requestArgumentCaptor.getValue(), classLoader);
    } catch (IOException
        | NoSuchMethodException
        | InstantiationException
        | IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(
          "There may be a compile error in the generated code."
              + " Try generating using the JSON schema from the test. "
              + e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(
          "There is a problem with calling this method.\r\n\t"
              + "Check that the generated method "
              + methodName
              + " can cope with these parameters: "
              + parameters
              + ". \r\n\t"
              + e);
    }
  }

  private ClassLoader createClassLoaderForPackage() throws MalformedURLException {
    File root = new File("src/main/java/");
    return URLClassLoader.newInstance(new URL[] {root.toURI().toURL()});
  }

  private void createOkHttpMocks(
      String response, OkHttpClient mockHttp, ArgumentCaptor<Request> requestArgumentCaptor)
      throws IOException {
    Response mockResponse = mock(Response.class);
    ResponseBody mockResponseBody = mock(ResponseBody.class);
    when(mockResponse.body()).thenReturn(mockResponseBody);
    when(mockResponseBody.string()).thenReturn(response);
    Call mockCall = mock(Call.class);
    when(mockHttp.newCall(requestArgumentCaptor.capture())).thenReturn(mockCall);
    when(mockCall.execute()).thenReturn(mockResponse);
  }

  public String getPropertyOnObject(
      String propName,
      Object resultOfMethodCall,
      String latestResponseType,
      ClassLoader classLoader) {
    try {
      Class<?> propClass =
          Class.forName(packageName + "." + latestResponseType, false, classLoader);
      Method getProp = propClass.getDeclaredMethod("get" + StringUtils.capitalize(propName));
      getProp.setAccessible(true); // Otherwise causes IllegalAccessException.
      return getProp.invoke(resultOfMethodCall).toString();
    } catch (ClassNotFoundException
        | NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean doesClassExist(String className)
      throws ClassNotFoundException, MalformedURLException {
    compileFilesInPackage();
    Class.forName(packageName + "." + className, false, createClassLoaderForPackage());
    return true;
  }

  public boolean classHasProperty(String className, String propertyName)
      throws NoSuchFieldException, ClassNotFoundException, MalformedURLException {
    compileFilesInPackage();
    ClassLoader classLoader = createClassLoaderForPackage();
    Class<?> clazz = Class.forName(packageName + "." + className, false, classLoader);
    return clazz.getField(propertyName).getName().equals(propertyName);
  }

  public String getTypeOfClassProperty(String className, String propertyName)
      throws NoSuchFieldException, ClassNotFoundException, MalformedURLException {
    compileFilesInPackage();
    ClassLoader classLoader = createClassLoaderForPackage();
    Class<?> clazz = Class.forName(packageName + "." + className, false, classLoader);
    return clazz.getField(propertyName).getGenericType().getTypeName();
  }

  private void compileFilesInPackage() {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    File srcMain = new File("src/main/java/" + packageName.replaceAll("\\.", "/"));
    File[] files = srcMain.listFiles();
    if (files == null) {
      System.err.println("Failed to read files in src/main/java/");
      return;
    }
    File[] filesInSrcMain =
        Arrays.stream(files)
            .filter(file -> file.getPath().endsWith(".java")) // To avoid re-compiling .class files.
            .toArray(File[]::new);
    String[] filePaths = new String[filesInSrcMain.length];
    for (int i = 0; i < filesInSrcMain.length; i++) {
      filePaths[i] = filesInSrcMain[i].getPath();
      System.out.println("compiling " + filePaths[i]);
    }
    compiler.run(null, null, null, filePaths);
  }

  private Object createApiClient(
      Class<?> apiClientClass, OkHttpClient mockHttp, int serverIndex, ClassLoader classLoader)
      throws NoSuchMethodException, InvocationTargetException, InstantiationException,
          IllegalAccessException, ClassNotFoundException, MalformedURLException {
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
      setBasePath.invoke(configuration, "https://doesnotmatter.com/");
      Method setServers = configurationClass.getDeclaredMethod("setServers", List.class);
      setServers.invoke(configuration, List.of("somewhere/around"));
    } else {
      setBasePath.invoke(configuration, "");
    }
  }
}