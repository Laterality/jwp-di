package aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class HelloProxy implements MethodInterceptor {

    public static final String METHOD_NAME_PREFIX = "say";

    public static HelloTarget create() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new HelloProxy());
        return (HelloTarget) enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String result = (String) methodProxy.invokeSuper(o, objects);
        if (method.getName().startsWith(METHOD_NAME_PREFIX)) {
            return result.toUpperCase();
        }
        return result;
    }
}
