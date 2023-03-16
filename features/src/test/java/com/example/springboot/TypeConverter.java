package com.example.springboot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TypeConverter {
  // Cannot store primitives in an array of Objects. This requires generated code to only use boxed
  // values.
  Object[] convertBoxedTypes(String[] valuesWithoutOptionalParameters, Class<?>[] targetTypes) {
    String[] values = fillOutWithNull(valuesWithoutOptionalParameters, targetTypes.length);
    Object[] convertedValues = new Object[values.length];
    for (int i = 0; i < values.length; i++) {
      Class<?> type = targetTypes[i];
      // Please change this if you think of a better way!
      if (values[i] == null) {
        convertedValues[i] = null;
      } else if (type == Integer.class) {
        convertedValues[i] = Integer.valueOf(values[i]);
      } else if (type == Long.class) {
        convertedValues[i] = Long.valueOf(values[i]);
      } else if (type == Double.class) {
        convertedValues[i] = Double.valueOf(values[i]);
      } else if (type == Float.class) {
        convertedValues[i] = Float.valueOf(values[i]);
      } else if (type == String.class) {
        convertedValues[i] = values[i];
      } else if (type.toString().contains(this.getClass().getPackageName())) {
        // This is a custom type, defined in the schema.
        // We need to deserialise the String to the Object of the correct class.
        ObjectMapper deserMapper = new ObjectMapper();
        try {
          convertedValues[i] = deserMapper.readValue(values[i], type);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      } else {
        throw new UnsupportedOperationException(
            "Trying to convert value " + values[i] + " from String to " + type);
      }
    }
    return convertedValues;
  }

  private String[] fillOutWithNull(String[] valuesWithoutOptionalParameters, int length) {
    // By default, parameters are optional. We can fill out the parameters with null to support
    // that.
    // https://swagger.io/specification/#parameter-object
    String[] values = new String[length];
    for (int i = 0; i < length; i++) {
      if (i < valuesWithoutOptionalParameters.length) {
        values[i] = valuesWithoutOptionalParameters[i];
      } else {
        values[i] = null;
      }
    }
    return values;
  }
}
