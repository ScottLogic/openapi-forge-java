package com.openapi.forge;

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
    String packageName = this.getClass().getPackageName();
    if (javaType.contains(packageName)) {
      // If we are using a custom type, strip out the package name
      String[] parts = javaType.split("\\.");
      return parts[parts.length - 1];
    }
    throw new UnsupportedOperationException("Converting " + javaType);
  }
}
