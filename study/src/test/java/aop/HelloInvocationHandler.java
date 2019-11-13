package aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

public class HelloInvocationHandler implements InvocationHandler {

    private final Object target;
    private final MethodMatcher matcher;

    public HelloInvocationHandler(Object target, MethodMatcher matcher) {
        this.target = target;
        this.matcher = matcher;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String result = (String) method.invoke(target, args);
        if (Objects.nonNull(matcher) && matcher.matches(target, method, args)) {
            return result.toUpperCase();
        }
        return result;
    }
}
