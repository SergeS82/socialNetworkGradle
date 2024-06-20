package com.example.springexample.dto.lib;

import java.lang.reflect.Field;
import java.util.Map;

public interface Dto {
    String getId();
    default void fillFromMap(Map<String, Object> map) throws IllegalAccessException {
        //Constructor<T> constructor = clazz.getConstructor();
        //T pojoObject = constructor.newInstance();
        for (Map.Entry<String, Object> m : map.entrySet()) {
            String propertyName = m.getKey();
            Object value = m.getValue();
            Field field;
            try {
                field = getClass().getDeclaredField(propertyName);
            } catch (NoSuchFieldException e) {
                continue;
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                if (field.getType().getSimpleName().equals("Character")) {
                    field.set(this, value.toString().charAt(0));
                } else {
                    field.set(this, value);
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalAccessException("Не соотвествует тип данных. "+e.getMessage());
            }
        }
    }

}
