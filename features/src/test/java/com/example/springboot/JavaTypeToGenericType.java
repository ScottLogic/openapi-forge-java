package com.example.springboot;

public class JavaTypeToGenericType {
  public String convert(String javaType) {
    // Want to convert values like java.lang.Integer
    if (javaType.contains("Integer")
        || javaType.contains("Long")
        || javaType.contains("Double")
        || javaType.contains("Float")) {
      return "number";
    }
    if (javaType.contains("String")) {
      return "string";
    }
    throw new UnsupportedOperationException("Converting " + javaType);
  }
}
