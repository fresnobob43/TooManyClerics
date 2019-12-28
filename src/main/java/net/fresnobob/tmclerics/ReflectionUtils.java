package net.fresnobob.tmclerics;

//import net.minecraft.init.Blocks;

import java.lang.reflect.Field;

/**
 * @author fresnobob43
 * @since 0.0.1
 */
class ReflectionUtils {

    static <T> T getFieldValue(String fieldName, Class onClass, Object onObject, Class<T> expectedType) {
        try {
            final Field f = onClass.getDeclaredField(fieldName);
            f.setAccessible(true);
            return (T) f.get(onObject);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static void setFieldValue(String fieldName, Class onClass, Object onObject, Object newValue) {
        try {
            final Field f = onClass.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(onObject, newValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
