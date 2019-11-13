package aop;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloTargetProxyTest {

    @Test
    void name() {
        // given
        HelloTarget helloTarget = HelloProxy.create();
        String name = "John";

        // when
        String hello = helloTarget.sayHello(name);
        String hi = helloTarget.sayHi(name);
        String thx = helloTarget.sayThankYou(name);

        // then
        assertThat(hello.toUpperCase()).isEqualTo(hello);
        assertThat(hi.toUpperCase()).isEqualTo(hi);
        assertThat(thx.toUpperCase()).isEqualTo(thx);
    }
}
