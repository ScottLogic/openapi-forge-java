package com.openapi.forge;

public class JavaTypeToGenericType {

  public String convert(Class<?> javaType) {
    if (
      javaType.equals(Integer.class) ||
      javaType.equals(int.class) ||
      javaType.equals(Long.class) ||
      javaType.equals(long.class) ||
      javaType.equals(Double.class) ||
      javaType.equals(double.class) ||
      javaType.equals(Float.class) ||
      javaType.equals(float.class)
    ) {
      return "number";
    }
    if (javaType.equals(String.class)) {
      return "string";
    }
    if (javaType.getPackageName().equals(this.getClass().getPackageName())) {
      // If we are using a custom type, strip out the package name
      String[] parts = javaType.getTypeName().split("\\.");
      return parts[parts.length - 1];
    }
    throw new UnsupportedOperationException("Converting " + javaType);
  }
}
