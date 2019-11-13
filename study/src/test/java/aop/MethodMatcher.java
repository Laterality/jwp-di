package aop;

import java.lang.reflect.Method;

@FunctionalInterface
public interface MethodMatcher {
    boolean matches(Object target, Method method, Object[] args);
}
