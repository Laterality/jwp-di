package aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Objects;

public class HelloProxy implements MethodInterceptor {

    private final MethodMatcher matcher;

    public static HelloTarget create(MethodMatcher matcher) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new HelloProxy(matcher));
        return (HelloTarget) enhancer.create();
    }

    public HelloProxy(MethodMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String result = (String) methodProxy.invokeSuper(o, objects);
        if (Objects.nonNull(matcher) && matcher.matches(o, method, objects)) {
            return result.toUpperCase();
        }
        return result;
    }
}
