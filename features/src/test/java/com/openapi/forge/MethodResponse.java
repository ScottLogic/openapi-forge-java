package com.openapi.forge;

import java.io.IOException;
import java.util.Objects;
import okhttp3.Headers;
import okhttp3.Request;
import okio.Buffer;

public class MethodResponse {
  private final Object responseData;
  private final Headers responseHeaders;
  private final Request request;
  private final ClassLoader classLoader;

  MethodResponse(
      Request request, Object responseData, Headers responseHeaders, ClassLoader classLoader) {
    this.responseData = responseData;
    this.responseHeaders = responseHeaders;
    this.request = request;
    this.classLoader = classLoader;
  }

  public Object getResultOfMethodCall() {
    return responseData;
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
    return responseHeaders;
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
