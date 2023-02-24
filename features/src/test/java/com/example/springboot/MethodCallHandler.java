package com.example.springboot;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
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

      Class<?> configurationClass =
          Class.forName(packageName + ".Configuration", true, this.getClass().getClassLoader());
      Class<?> apiClientClass =
          Class.forName(packageName + ".ApiClient", true, this.getClass().getClassLoader());

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
