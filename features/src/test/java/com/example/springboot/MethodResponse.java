package com.example.springboot;

import okhttp3.Headers;

public class MethodResponse {
  private final Object resultOfMethodCall;
  private final String urlRequested;
  private final Headers headers;

  MethodResponse(Object resultOfMethodCall, String urlRequested, Headers headers) {
    this.resultOfMethodCall = resultOfMethodCall;
    this.urlRequested = urlRequested;
    this.headers = headers;
  }

  public Object getResultOfMethodCall() {
    return resultOfMethodCall;
  }

  public String getUrlRequested() {
    return urlRequested;
  }

  public Headers getHeaders() {
    return headers;
  }
}
