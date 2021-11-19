package org.shaq.graphql.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflactionHelper {

    private final static String GENERATED_FIELD_NAME_MARK = "$";

    public List<Field> getAllFieldsOfClass(Class clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field currentField : clazz.getDeclaredFields()) {
            if (currentField.getName().contains(GENERATED_FIELD_NAME_MARK)) {
                continue;
            }

            fields.add(currentField);
        }

        return fields;
    }

}
