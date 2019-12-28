package net.fresnobob.tmclerics;

//import net.minecraft.init.Blocks;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author fresnobob43
 * @since 0.0.1
 */
class ReflectionUtils {

    static <T> T getFieldValue(String fieldName, Class onClass, Object onObject, Class<T> expectedType)
            throws ReflectiveOperationException {
        final Field f = onClass.getDeclaredField(fieldName);
        f.setAccessible(true);
        return (T) f.get(onObject);
    }

    static void setFieldValue(String fieldName, Class onClass, Object onObject, Object newValue)
            throws ReflectiveOperationException {
        final Field f = onClass.getDeclaredField(fieldName);
        if (Modifier.isFinal(f.getModifiers())) {
            // guard against stupid mistakes
            throw new IllegalAccessException(onClass + "." + fieldName + " is final");
        }
        f.setAccessible(true);
        f.set(onObject, newValue);
    }
}
