package com.spinteg.lv.ExceptionHandling;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ValidationUtil {

    public static boolean checkRequiredFields(Object dto) {
        Field[] fields = dto.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(dto);
                if (value == null || value.toString().trim().isEmpty()) {
                    throw new CustomDataMissingException("Field " + field.getName() + " is required");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
