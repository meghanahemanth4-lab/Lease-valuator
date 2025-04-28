package com.spinteg.lv.exception;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ValidationUtil {

    public static boolean checkRequiredFields(Object dto, String... notRequiredFields) {
        Field[] fields = dto.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(dto);
                if (value == null || value.toString().trim().isEmpty()) {
                    if (notRequiredFields != null && Arrays.asList(notRequiredFields).contains(field.getName())) {
                        continue;
                    }
                    throw new CustomDataMissingException("Field " + field.getName() + " is required");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
