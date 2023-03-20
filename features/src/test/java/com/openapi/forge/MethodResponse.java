package com.openapi.forge;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;
import okhttp3.Headers;
import okhttp3.Request;
import okio.Buffer;

public class MethodResponse {
  private final Object resultOfMethodCall;
  private final Request request;
  private final ClassLoader classLoader;

  MethodResponse(Object resultOfMethodCall, Request request, ClassLoader classLoader) {
    this.resultOfMethodCall = resultOfMethodCall;
    this.request = request;
    this.classLoader = classLoader;
  }

  public Object getResultOfMethodCall() {
    Class<?> clazz = resultOfMethodCall.getClass();
    if (clazz.getSimpleName().equals("HttpResponse")) {
      try {
        Field data = clazz.getDeclaredField("data");
        data.setAccessible(true);
        return data.get(resultOfMethodCall);
      } catch (NoSuchFieldException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    return resultOfMethodCall;
  }

  public String getUrlRequested() {
    return request.url().toString();
  }

  public Headers getRequestHeaders() {
    return request.headers();
  }

  public String getRequestMethod() {
    return request.method();
  }

  public String getRequestBodyAsString() {
    return bodyToString(request);
  }

  public ClassLoader getClassLoader() {
    return classLoader;
  }

  public Headers getResponseHeaders() {
    return null; // TODO: How to get these?
//    return resultOfMethodCall .headers();
  }

  // https://stackoverflow.com/a/29033727/
  private String bodyToString(final Request request) {
    try {
      final Request copy = request.newBuilder().build();
      final Buffer buffer = new Buffer();
      Objects.requireNonNull(copy.body()).writeTo(buffer);
      return buffer.readUtf8();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
