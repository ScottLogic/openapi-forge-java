package com.example.springboot;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.util.StringUtils;

public class MethodCallHandler {
  private final String packageName = this.getClass().getPackageName();

  protected Object callMethod(String methodName, List<?> parameters, String response) {
    return callMethod(methodName, parameters, response, 0);
  }

  protected Object callMethod(
      String methodName, List<?> parameters, String response, int serverIndex) {
    try {
      Response mockResponse = mock(Response.class);
      ResponseBody mockResponseBody = mock(ResponseBody.class);
      when(mockResponse.body()).thenReturn(mockResponseBody);
      when(mockResponseBody.string()).thenReturn(response);
      Call mockCall = mock(Call.class);
      OkHttpClient mockHttp = mock(OkHttpClient.class);
      when(mockHttp.newCall(any())).thenReturn(mockCall);
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

      Class<?> configurationClass =
          Class.forName(packageName + ".Configuration", true, classLoader);
      Class<?> apiClientClass = Class.forName(packageName + ".ApiClient", true, classLoader);

      Object configuration = configurationClass.getDeclaredConstructor().newInstance();
      Method setBasePath = configurationClass.getDeclaredMethod("setBasePath", String.class);
      setBasePath.invoke(configuration, "https://todoBasePath");
      Method setSelectedServerIndex =
          configurationClass.getDeclaredMethod("setSelectedServerIndex", int.class);
      setSelectedServerIndex.invoke(configuration, 0);
      Method setServers = configurationClass.getDeclaredMethod("setServers", List.class);
      setServers.invoke(configuration, List.of("todoServer0"));

      Object apiClient =
          apiClientClass
              .getDeclaredConstructor(OkHttpClient.class, configurationClass)
              .newInstance(mockHttp, configuration);

      Method method = apiClientClass.getDeclaredMethod(methodName);
      ////
      //      Class<?>[] parameterClasses = method.getParameterTypes();
      //      Map<Class, >

      ////
      Object objectResponse = method.invoke(apiClient);
      //      Object objectResponse = method.invoke(apiClient, parameters.toArray());
      return objectResponse;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public String getPropertyOnObject(
      String propName, Object latestResponse, String latestResponseType) {
    try {
      Class<?> propClass = Class.forName(packageName + "." + latestResponseType);
      Method getProp = propClass.getDeclaredMethod("get" + StringUtils.capitalize(propName));
      return getProp.invoke(latestResponse).toString();
    } catch (ClassNotFoundException
        | NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
