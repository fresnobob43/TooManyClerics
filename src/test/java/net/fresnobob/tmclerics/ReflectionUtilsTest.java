package net.fresnobob.tmclerics;

import org.junit.jupiter.api.Test;

import static net.fresnobob.tmclerics.ReflectionUtils.getFieldValue;
import static net.fresnobob.tmclerics.ReflectionUtils.setFieldValue;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author fresnobob43
 * @since 0.0.1
 */
public class ReflectionUtilsTest {

    @Test
    public void testGetFieldValue() throws Exception {
        class SomeClass {
            final String someField = "someValue";
        }
        final SomeClass someObject = new SomeClass();
        assertEquals("someValue", getFieldValue("someField", SomeClass.class, someObject, String.class));
        try {
            getFieldValue("noSuchField", SomeClass.class, someObject, String.class);
            fail("did not get expected exception on non-existent field");
        } catch (NoSuchFieldException expected) {
        }

    }

    @Test
    public void testSetFieldValue() throws Exception {
        class SomeClass {
            String someField = "someValue";
            final String someFinalField = "neverChanges";
        }
        final SomeClass someObject = new SomeClass();
        setFieldValue("someField", SomeClass.class, someObject, "someOtherValue");
        assertEquals("someOtherValue", someObject.someField);
        setFieldValue("someField", SomeClass.class, someObject, null);
        assertNull(someObject.someField);
        try {
            setFieldValue("noSuchField", SomeClass.class, someObject, "doesntMatter");
            fail("did not get expected exception on non-existent field");
        } catch (NoSuchFieldException expected) {
        }
        try {
            setFieldValue("someFinalField", SomeClass.class, someObject, "doesntMatter");
            fail("did not get expected exception on final field");
        } catch (IllegalAccessException expected) {
        }
        try {
            setFieldValue("noSuchField", SomeClass.class, someObject, 123456);
            fail("did not get expected exception on type mismatch");
        } catch (NoSuchFieldException expected) {
        }

    }

}
