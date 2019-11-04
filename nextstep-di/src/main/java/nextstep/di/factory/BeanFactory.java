package nextstep.di.factory;

import com.google.common.collect.Maps;
import nextstep.annotation.Bean;
import nextstep.annotation.Configuration;
import nextstep.di.factory.exception.BeanFactoryInitializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstantiateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();
    private Map<Type, Object> beans2 = Maps.newHashMap();
    //    private Set<Executable> executables;
    private Map<Type, Executable> executables;

    public BeanFactory(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        executables = preInstantiateBeans.stream()
                .filter(cls -> cls.isAnnotationPresent(Configuration.class))
                .flatMap(cls -> Arrays.stream(cls.getMethods()).filter(method -> method.isAnnotationPresent(Bean.class)))
                .collect(Collectors.toMap(exe -> exe.getAnnotatedReturnType().getType(),
                        exe -> exe));

        preInstantiateBeans.stream()
                .map(this::getConstructor)
                .forEach(ctor -> executables.put(ctor.getDeclaringClass(), ctor));

        executables.keySet().forEach(this::getOrInstantiate2);
        //
        preInstantiateBeans
                .forEach(this::getOrInstantiate);
    }

    private Object getOrInstantiate2(Type type) {
        if (beans2.containsKey(type)) {
            return beans2.get(type);
        }

        Object instance = tryInstantiateBean2(type);
        beans2.put(type, instance);
        return instance;
    }

    private Object tryInstantiateBean2(Type type) {
        try {
            return instantiateBean2(type);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Error while instantiate bean", e);
            throw new BeanFactoryInitializeException(e);
        }
    }

    private Object instantiateBean2(Type type) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Executable exe = executables.get(type);
        Object[] params = resolveParameters(exe);
        if (exe instanceof Constructor) {
            return ((Constructor) exe).newInstance(params);
        }
        return ((Method) exe).invoke(beans2.get(exe.getDeclaringClass()), params);
    }

    private Object[] resolveParameters(Executable executable) {
        return Arrays.stream(executable.getParameterTypes())
                .map(param -> BeanFactoryUtils.findConcreteClass(param, preInstantiateBeans))
                .map(this::getOrInstantiate2)
                .toArray();
    }

    private Object getOrInstantiate(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        Object instance = tryInstantiateBean(clazz);
        beans.put(clazz, instance);
        return instance;
    }

    private Object tryInstantiateBean(Class<?> clazz) {
        try {
            return instantiateBean(clazz);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Error while instantiate bean", e);
            throw new BeanFactoryInitializeException(e);
        }
    }

    private Object instantiateBean(Class<?> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> ctor = getConstructor(clazz);
        Object[] params = resolveConstructorParameters(ctor);

        return ctor.newInstance(params);
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        try {
            Constructor<?> ctor = BeanFactoryUtils.getInjectedConstructor(clazz);
            if (Objects.isNull(ctor)) {
                return clazz.getConstructor();
            }
            return ctor;
        } catch (NoSuchMethodException e) {
            logger.error("Error while getting constructor", e);
            throw new BeanFactoryInitializeException(e);
        }
    }

    private Object[] resolveConstructorParameters(Constructor<?> ctor) {
        return Arrays.stream(ctor.getParameterTypes())
                .map(param -> BeanFactoryUtils.findConcreteClass(param, preInstantiateBeans))
                .map(this::getOrInstantiate)
                .toArray();
    }
}
