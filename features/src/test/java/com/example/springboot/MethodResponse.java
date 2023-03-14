package com.example.springboot;

import okhttp3.Headers;
import okhttp3.Request;

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
    return resultOfMethodCall;
  }

  public String getUrlRequested() {
    return request.url().toString();
  }

  public Headers getHeaders() {
    return request.headers();
  }

  public String getRequestMethod() {
    return request.method();
  }

  public ClassLoader getClassLoader() {
    return classLoader;
  }
}
