package com.openapi.forge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;

public class TypeConverter {

  // Cannot store primitives in an array of Objects. This requires generated code to only use boxed
  // values.
  Object[] convertBoxedTypes(
    String[] valuesWithoutOptionalParameters,
    Class<?>[] targetTypes
  ) {
    String[] values = fillOutWithNull(
      valuesWithoutOptionalParameters,
      targetTypes.length
    );
    Object[] convertedValues = new Object[values.length];
    for (int i = 0; i < values.length; i++) {
      convertedValues[i] = convertValue(values[i], targetTypes[i]);
    }
    return convertedValues;
  }

  public Object convertValue(String value, Class<?> type) {
    if (value == null) {
      return null;
    } else if (type == Integer.class) {
      return Integer.valueOf(value);
    } else if (type == Long.class) {
      return Long.valueOf(value);
    } else if (type == Double.class) {
      return Double.valueOf(value);
    } else if (type == Float.class) {
      return Float.valueOf(value);
    } else if (type == String.class) {
      return value;
    } else if (type.toString().contains(this.getClass().getPackageName())) {
      // This is a custom type, defined in the schema.
      // We need to deserialise the String to the Object of the correct class.
      ObjectMapper deserMapper = new ObjectMapper();
      try {
        return deserMapper.readValue(value, type);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    } else if (type.toString().contains("java.util.List")) {
      // Assuming a list of strings.
      String[] strings = value.split(",");
      return Arrays.asList(strings);
    } else {
      throw new UnsupportedOperationException(
        "Trying to convert value " + value + " from String to " + type
      );
    }
  }

  private String[] fillOutWithNull(
    String[] valuesWithoutOptionalParameters,
    int targetLength
  ) {
    // By default, parameters are optional. We can fill out the parameters with null to support
    // that.
    // https://swagger.io/specification/#parameter-object
    String[] values = new String[targetLength];
    System.arraycopy(
      valuesWithoutOptionalParameters,
      0,
      values,
      0,
      valuesWithoutOptionalParameters.length
    );
    return values;
  }
}
