package gregtech.api.util;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by matthew on 3/31/17.
 */
public final class Reflection {

    static {
        thisClass = Reflection.class.getCanonicalName();
        methods = new HashMap<Class<?>, String>() {
            {
                for (Method m : Reflection.class.getDeclaredMethods()) {
                    put(Reflection.class, m.getName());
                }
                for (Class c : Reflection.class.getDeclaredClasses()) {
                    for (Method m : c.getDeclaredMethods()) {
                        put(c, m.getName());
                    }
                }
            }
        };
    }

    private static final HashMap<Class<?>, String> methods;

    public static String thisClass;

    private static final boolean isReflectionMember(final StackTraceElement e) {
        Boolean matchClass = false;
        if (e.getClassName() != null) {
            for (Class c : Reflection.methods.keySet()) {
                matchClass = e.getClassName().contains(Reflection.thisClass) ||
                        c.getCanonicalName() == e.getClassName();
            }
        }
        return matchClass;
    }

    private static final int countInternalCalls() {
        int internal = 0;
        for (StackTraceElement s : Thread.currentThread().getStackTrace()) {
            if (s.getMethodName().contains("getStackTrace") ||
                    isReflectionMember(s)) ++internal;
            else break;
        }
        return internal;
    }

    /**Returns a {@link StackTraceElement} representing the Class and Method,
     * in {@link String} form, that invoked a method on the stack, a specified
     * number of calls back.
     *
     * @param traceBack How many calls to traverse backwards in the stack.
     *                  Cannot be negative.
     * @return {@link StackTraceElement}
     */
    public static StackTraceElement getInvokerElement(final int traceBack) {
        return Thread.currentThread().getStackTrace()[traceBack];
    }

    /**Not useful outside of the {@link Reflection} class. Delegates to
     * {@link Reflection#getInvokerElement(int)}, substituting the number of
     * internal calls.
     *
     * @return {@link StackTraceElement}
     */
    public static StackTraceElement getInvokerElement() {
        return getInvokerElement(countInternalCalls());
    }

    public static Class<?> getCallerClass() {
        StackTraceElement s = getInvokerElement();
        Class<?> invoker = null;
        try {
            invoker = Class.forName(s.getClassName());
        } catch (ClassNotFoundException e) {
            System.err.println("Could not find class: '" + s.getClassName() + "'!");
//            e.printStackTrace();
        }
        return invoker;
    }
}
