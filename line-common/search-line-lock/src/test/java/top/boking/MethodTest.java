package top.boking;

import org.springframework.core.StandardReflectionParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodTest {
    public static void main(String[] args) {
        StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();
        MethodTest methodTest = new MethodTest();
        Method[] methods = methodTest.getClass().getMethods();
        for (Method method : methods) {
            String[] parameterNames = discoverer.getParameterNames(method);
        }

    }

    public void test(String a, String b, Integer c, int d) {

    }
}
