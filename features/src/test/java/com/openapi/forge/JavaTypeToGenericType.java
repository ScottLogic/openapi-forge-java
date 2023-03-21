package com.openapi.forge;

public class JavaTypeToGenericType {
  public String convert(String javaType) {
    // Want to convert values like java.lang.Integer
    String typeLowerCase = javaType.toLowerCase();
    if (typeLowerCase.contains("int")
        || typeLowerCase.contains("long")
        || typeLowerCase.contains("double")
        || typeLowerCase.contains("float")) {
      return "number";
    }
    if (typeLowerCase.contains("string")) {
      return "string";
    }
    String packageName = this.getClass().getPackageName().toLowerCase();
    if (typeLowerCase.contains(packageName)) {
      // If we are using a custom type, strip out the package name
      String[] parts = javaType.split("\\.");
      return parts[parts.length - 1];
    }
    throw new UnsupportedOperationException("Converting " + javaType);
  }
}
