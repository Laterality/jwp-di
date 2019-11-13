package aop;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloTargetProxyTest {

    @Test
    void proxy() {
        // given
        HelloTarget helloTarget = HelloProxy.create((target, method, args) -> true);
        String name = "John";

        // when
        String hello = helloTarget.sayHello(name);
        String hi = helloTarget.sayHi(name);
        String thx = helloTarget.sayThankYou(name);
        String pong = helloTarget.pingpong(name);

        // then
        assertThat(hello.toUpperCase()).isEqualTo(hello);
        assertThat(hi.toUpperCase()).isEqualTo(hi);
        assertThat(thx.toUpperCase()).isEqualTo(thx);
    }

    @Test
    void proxy_matcher() {
        // given
        HelloTarget helloTarget = HelloProxy.create((target, method, args) -> method.getName().startsWith("say"));
        String name = "John";

        // when
        String hello = helloTarget.sayHello(name);
        String hi = helloTarget.sayHi(name);
        String thx = helloTarget.sayThankYou(name);
        String pong = helloTarget.pingpong(name);

        // then
        assertThat(hello.toUpperCase()).isEqualTo(hello);
        assertThat(hi.toUpperCase()).isEqualTo(hi);
        assertThat(thx.toUpperCase()).isEqualTo(thx);
        assertThat(pong).isEqualTo("Pong John");
    }
}
