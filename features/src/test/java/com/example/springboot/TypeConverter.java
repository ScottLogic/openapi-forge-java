package com.example.springboot;

import java.util.Arrays;

public class TypeConverter {
  // Cannot store primitives in an array of Objects. This requires generated code to only use boxed
  // values.
  Object[] convertBoxedTypes(String[] values, Class<?>[] targetTypes) {
    System.err.println(Arrays.toString(values));
    System.err.println(Arrays.toString(targetTypes));

    assert values.length == targetTypes.length;
    Object[] convertedValues = new Object[values.length];
    for (int i = 0; i < values.length; i++) {
      Class<?> type = targetTypes[i];
      // Please change this if you think of a better way!
      if (type == Integer.class) {
        convertedValues[i] = Integer.valueOf(values[i]);
      } else if (type == Long.class) {
        convertedValues[i] = Long.valueOf(values[i]);
      } else if (type == Double.class) {
        convertedValues[i] = Double.valueOf(values[i]);
      } else if (type == Float.class) {
        convertedValues[i] = Float.valueOf(values[i]);
      } else if (type == String.class) {
        convertedValues[i] = values[i];
      } else {
        throw new UnsupportedOperationException(
            "Trying to convert value " + values[i] + " from String to " + type);
      }
    }
    System.err.println(Arrays.toString(convertedValues));
    return convertedValues;
  }
}
