package gov.nih.nci.caarray.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class representing the getter/setter pair for a property.
 * @author dkokotov
 */
public final class PropertyAccessor {
    private final Method getter; // NOPMD
    private final Method setter; // NOPMD

    /**
     * @param getter the getter method for the property
     * @param setter the setter method for the property
     */
    public PropertyAccessor(Method getter, Method setter) {
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * @return the type of the property of this accessor
     */
    public Class<?> getType() {
        return getter.getReturnType();
    }

    /**
     * Set the value of the property of this accessor on the given object to the given value.
     * @param target the target object
     * @param val the value to set the property to
     * @throws IllegalAccessException if there is an error invoking the setter
     * @throws InvocationTargetException if there is an error invoking the setter
     */
    public void set(Object target, Object val) throws IllegalAccessException, InvocationTargetException {
        setter.invoke(target, val);
    }

    /**
     * Get the value of the property of this accessor from the given object.
     * @param target the object from which to get the value of the property
     * @return the property value
     * @throws IllegalAccessException if there is an error invoking the setter
     * @throws InvocationTargetException if there is an error invoking the setter
     */
    public Object get(Object target) throws IllegalAccessException, InvocationTargetException {
        return getter.invoke(target);
    }

    /**
     * @return the getter method for this property
     */
    public Method getter() { 
        return getter;
    }

    /**
     * @return the setter method for this property
     */
    public Method setter() { 
        return setter;
    }
}
