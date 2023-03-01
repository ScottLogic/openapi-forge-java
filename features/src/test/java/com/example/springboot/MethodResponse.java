package com.example.springboot;

public class MethodResponse {
  private Object resultOfMethodCall;
  private String urlRequested;

  MethodResponse(Object resultOfMethodCall, String urlRequested) {
    this.resultOfMethodCall = resultOfMethodCall;
    this.urlRequested = urlRequested;
  }

  public Object getResultOfMethodCall() {
    return resultOfMethodCall;
  }

  public String getUrlRequested() {
    return urlRequested;
  }
}
