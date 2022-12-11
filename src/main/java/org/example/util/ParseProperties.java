package org.example.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Properties;
import org.example.model.Property;

public class ParseProperties {

    public static <T> T loadFromProperties(Class<T> cls, Path propertiesPath) throws Exception {
        Properties properties = readPropertiesFromFile(propertiesPath);
        T t = cls.getConstructor().newInstance();
        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Annotation[] annotations = field.getDeclaredAnnotations();

            for (Annotation annotation : annotations) {
                if (annotation.annotationType().getName().equals(Property.class.getName())) {
                    setFieldByProperty(properties, (Property) annotation, t, field);
                }
            }
        }
        return t;
    }

    private static <T> void setFieldByProperty(Properties properties, Property property, T t,
                                             Field field)
            throws IllegalAccessException {
        String s = properties.getProperty(property.name());
        if (String.class.equals(field.getType())) {
            field.set(t, s);
        } else if (int.class.equals(field.getType())) {
            field.set(t, Integer.parseInt(s));
        } else if (Instant.class.equals(field.getType())) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(property.format());
                Instant instant = format.parse(s).toInstant();
                field.set(t, instant);
            } catch (Exception e) {
                throw new RuntimeException("Wrong format", e);
            }
        }
    }

    private static Properties readPropertiesFromFile(Path path) {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(String.valueOf(path))) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Can`t open file ", e);
        }
        return properties;
    }
}
