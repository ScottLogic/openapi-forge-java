package com.example.springboot;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
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
  private final String basePath = "https://example.com/";
  private final String server0 = "api/v3";
  private final TypeConverter typeConverter;
  private ClassLoader classLoader;

  MethodCallHandler(TypeConverter typeConverter) {
    this.typeConverter = typeConverter;
  }

  protected MethodResponse callMethod(String methodName, List<String> parameters, String response) {
    return callMethod(methodName, parameters, response, 0);
  }

  protected MethodResponse callMethod(
      String methodName, List<String> parameters, String response, int serverIndex) {
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

      //      /////////////
      //      Runtime runtime = Runtime.getRuntime();
      //      String[] javacCommand = new String[] {"javac", "src/main/java/**/*.java"};
      //      // TODO: Can we get the names of all files generated to be returned with the exit
      // code?
      //      Process process = runtime.exec(javacCommand);
      //      try {
      //        process.waitFor();
      //      } catch (InterruptedException e) {
      //        throw new RuntimeException(e);
      //      }
      //      //////////////

      //      File configurationFile =
      //          new File("src/main/java/" + packageName.replaceAll("\\.", "/") +
      // "/Configuration.java");
      //      File apiClientFile =
      //          new File("src/main/java/" + packageName.replaceAll("\\.", "/") +
      // "/ApiClient.java");
      //      File apiModelFile =
      //          new File("src/main/java/" + packageName.replaceAll("\\.", "/") +
      // "/ApiModel.java");
      //      File applicationFile =
      //          new File("src/main/java/" + packageName.replaceAll("\\.", "/") +
      // "/Application.java");
      //      File iApiClientFile =
      //          new File("src/main/java/" + packageName.replaceAll("\\.", "/") +
      // "/IApiClient.java");
      //      File allFiles = new File("src/main/java/" + packageName.replaceAll("\\.", "/") +
      // "/*.java");
      //
      //      System.err.println(configurationFile.getCanonicalPath());
      //      compiler.run(
      //          null,
      //          null,
      //          null,
      //          configurationFile.getPath(),
      //          apiClientFile.getPath(),
      //          apiModelFile.getPath(),
      //          iApiClientFile.getPath(),
      //          applicationFile.getPath());
      //      compiler.run(null, null, null, apiClientFile.getPath());
      //      compiler.run(null, null, null, apiModelFile.getPath());
      //      compiler.run(null, null, null, allFiles.getPath());

      // Load and instantiate compiled class.
      File root = new File("src/main/java/");
      System.err.println(root.getCanonicalPath());
      URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {root.toURI().toURL()});
      this.classLoader = classLoader;

      Class<?> configurationClass =
          Class.forName(packageName + ".Configuration", true, classLoader);
      Class<?> apiClientClass = Class.forName(packageName + ".ApiClient", true, classLoader);
      //      Class.forName(packageName + ".ObjectResponse", true, classLoader);

      Object configuration = configurationClass.getDeclaredConstructor().newInstance();
      Method setBasePath = configurationClass.getDeclaredMethod("setBasePath", String.class);
      setBasePath.invoke(configuration, basePath);
      Method setSelectedServerIndex =
          configurationClass.getDeclaredMethod("setSelectedServerIndex", int.class);
      setSelectedServerIndex.invoke(configuration, 0);
      Method setServers = configurationClass.getDeclaredMethod("setServers", List.class);
      setServers.invoke(configuration, List.of(server0));

      Object apiClient =
          apiClientClass
              .getDeclaredConstructor(OkHttpClient.class, configurationClass)
              .newInstance(mockHttp, configuration);

      //      System.err.println("Parameters:");
      //      for (var param : parameters.toArray()) {
      //        System.err.println(param);
      //      }
      //      List<Class> classList = parameters.stream().map(param -> (Class)
      // param.getClass()).toList();
      //      Class[] classes = classList.toArray(new Class[0]);
      //      Object[] castParameters = new Object[parameters.size()];
      //      for (var param : parameters.toArray()) {
      //        System.err.println(param.getClass());
      //      }

      // Doesn't account for params:
      //      Method method = apiClientClass.getDeclaredMethod(methodName, classes);

      Method[] allMethods = apiClientClass.getDeclaredMethods();
      Method methodWithParameters =
          Arrays.stream(allMethods)
              .filter(m -> m.getName().equals(methodName))
              .findFirst()
              .orElseThrow();
      //      Class<?>[] parameterTypes = methodWithParameters.getParameterTypes();
      //      for (int i = 0; i < parameters.size(); i++) {
      //        castParameters[i] = parameterTypes[i].cast(parameters.toArray()[i]);
      //        System.err.println(castParameters[i]);
      //        System.err.println(castParameters[i].getClass());
      //      }
      //      Object[]
      //      for

      ///// TODO: Get method with given name and cast my string parameters to the types expected. We
      // assume that there
      ///// will only be one of each method name (no overloaded methods).

      ////
      //      Class<?>[] parameterClasses = method.getParameterTypes();
      //      Map<Class, >

      ////
      //      Object objectResponse = methodWithParameters.invoke(apiClient);

      //      System.err.println(methodWithParameters.getName());
      //      Object objectResponse = methodWithParameters.invoke(apiClient, "cat", 2); // TODO
      // don't hardcode!
      Object[] convertedParameters =
          typeConverter.convertBoxedTypes(
              parameters.toArray(new String[0]), methodWithParameters.getParameterTypes());
      Object objectResponse =
          methodWithParameters.invoke(
              apiClient, convertedParameters); // ONLY WORKS WITH BOXED VALUES
      return new MethodResponse(objectResponse, argumentCaptor.getValue().url().toString());

    } catch (Exception e) {
      e.printStackTrace();
      // TODO Throw here.
    }
    return null;
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
}
