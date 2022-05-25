package com.l1fe1.springdatajpa;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A helper class to get generics information about a class.
 */
public class GenericsUtils {

    private GenericsUtils() {
    }

    /**
     * Get the first generics information for the specified class.
     *
     * @param clazz
     * @return
     */
    public static Class getGenericClass(Class clazz) {
        return getGenericClass(clazz, 0);
    }

    /**
     * Get generics information for the specified location of the specified class.
     *
     * @param clazz
     * @param index
     * @param <T>
     * @return
     */
    public static <T> Class<T> getGenericClass(Class clazz, int index) {
        return getGenericClass(clazz, index, null);
    }

    /**
     * Get the first generics information for a parent of a specified class.
     *
     * @param clazz
     * @param parentClass
     * @param <T>
     * @return
     */
    public static <T> Class<T> getGenericClass(Class clazz, Class parentClass) {
        return getGenericClass(clazz, 0, parentClass);
    }

    /**
     * Get generics information about the specified location of a parent class of a specified class.
     *
     * @param clazz
     * @param index
     * @param parentClass
     * @param <T>
     * @return
     */
    public static <T> Class<T> getGenericClass(Class clazz, int index, Class parentClass) {
        Type genType = clazz.getGenericSuperclass();

        if (genType instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

            if ((params != null) && (params.length >= (index + 1))) {
                if (parentClass != null && params.length > (index + 1)) {
                    Class<T> preClazz = (Class<T>) params[index];
                    if (isSub(preClazz, parentClass)) {
                        return preClazz;
                    } else {
                        index++;
                        return getGenericClass(clazz, index, parentClass);
                    }
                } else if (params[index] instanceof Class) {
                    return (Class<T>) params[index];
                }
            }
        } else if (!clazz.equals(Object.class)) {
            return getGenericClass(clazz.getSuperclass(), index, parentClass);
        }
        return null;
    }

    /**
     * Used to determine whether a class is a subclass of a class.
     *
     * @param subClass
     * @param supClass
     * @return
     */
    private static boolean isSub(Class subClass, Class supClass) {
        do {
            if (subClass.isAssignableFrom(supClass)) {
                return true;
            }
            Class[] classes = subClass.getInterfaces();
            if (classes != null) {
                for (int i = 0; i < classes.length; i++) {
                    if (isSub(classes[i], supClass))
                        return true;
                }
            }
            subClass = subClass.getSuperclass();
        } while (subClass != null && !(subClass.getClass().equals(Object.class)));
        return false;
    }

}