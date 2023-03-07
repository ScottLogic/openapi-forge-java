package com.example.springboot;

public class MethodResponse {
  private final Object resultOfMethodCall;
  private final String urlRequested;
  private final String cookieHeader;

  MethodResponse(Object resultOfMethodCall, String urlRequested, String cookieHeader) {
    this.resultOfMethodCall = resultOfMethodCall;
    this.urlRequested = urlRequested;
    this.cookieHeader = cookieHeader;
  }

  public Object getResultOfMethodCall() {
    return resultOfMethodCall;
  }

  public String getUrlRequested() {
    return urlRequested;
  }

  public String getCookieHeader() {
    return cookieHeader;
  }
}
