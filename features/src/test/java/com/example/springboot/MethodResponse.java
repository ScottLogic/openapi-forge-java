package com.example.springboot;

import okhttp3.Headers;
import okhttp3.Request;

public class MethodResponse {
  private final Object resultOfMethodCall;
  private final Request request;

  MethodResponse(Object resultOfMethodCall, Request request) {
    this.resultOfMethodCall = resultOfMethodCall;
    this.request = request;
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
}
