package my.linkin.lier.agent;

import my.linkin.lier.*;
import my.linkin.lier.interceptor.StackTraceInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.springframework.web.bind.annotation.*;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;

/**
 * lier agent
 *
 * @author linkin
 */
public class StackTraceAgent {


    public static void agentmain(String agentArgs, Instrumentation inst) throws UnmodifiableClassException {
        inst.addTransformer(new MockClassFileTransformer(), true);
        Class[] classes = inst.getAllLoadedClasses();
        ArgsHelper argsHelper = ArgsHelper.of(agentArgs);
        ClientStackTracer.initial(selectEntry(argsHelper.getUrl(), classes));
        for (Class c : classes) {
            if (c.getSimpleName().contains("client") && c.getSimpleName().contains("Client")) {
                inst.retransformClasses(c);
            }
        }

        AgentBuilder.Transformer transformer = new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                                    TypeDescription typeDescription,
                                                    ClassLoader classLoader,
                                                    JavaModule javaModule) {
                return builder.method(ElementMatchers.<MethodDescription>any())
                        .intercept(MethodDelegation.to(StackTraceInterceptor.class));
            }
        };

        new AgentBuilder.Default()
                .type(ElementMatchers.nameContains("client"))
                .transform(transformer)
                .with(new AgentBuilder.Listener() {

                    @Override
                    public void onDiscovery(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {
                        
                    }

                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b, DynamicType dynamicType) {

                    }

                    @Override
                    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b) {

                    }

                    @Override
                    public void onError(String s, ClassLoader classLoader, JavaModule javaModule, boolean b, Throwable throwable) {

                    }

                    @Override
                    public void onComplete(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {

                    }
                })
                .installOn(inst);

    }


    /**
     * Select a request entry for given url
     *
     * @param url
     * @param classes
     * @return
     */
    private static RequestEntry selectEntry(String url, Class[] classes) {
        Class<?> c = null;
        Method met = null;
        for (Class<?> clz : classes) {
            if (!clz.isAnnotationPresent(RequestMapping.class)) {
                continue;
            }
            RequestMapping mapping = clz.getAnnotation(RequestMapping.class);
            if (!url.contains(mapping.path()[0])) {
                continue;
            }
            String path = mapping.path()[0];

            for (Method m : clz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping rm = m.getAnnotation(RequestMapping.class);
                    if (url.equals(path.concat(rm.path()[0]))) {
                        met = m;
                        break;
                    }
                }
                if (m.isAnnotationPresent(GetMapping.class)) {
                    GetMapping rm = m.getAnnotation(GetMapping.class);
                    if (url.equals(path.concat(rm.path()[0]))) {
                        met = m;
                        break;
                    }
                }
                if (m.isAnnotationPresent(PostMapping.class)) {
                    PostMapping rm = m.getAnnotation(PostMapping.class);
                    if (url.equals(path.concat(rm.path()[0]))) {
                        met = m;
                        break;
                    }
                }
                if (m.isAnnotationPresent(DeleteMapping.class)) {
                    DeleteMapping rm = m.getAnnotation(DeleteMapping.class);
                    if (url.equals(path.concat(rm.path()[0]))) {
                        met = m;
                        break;
                    }
                }
                if (m.isAnnotationPresent(PutMapping.class)) {
                    PutMapping rm = m.getAnnotation(PutMapping.class);
                    if (url.equals(path.concat(rm.path()[0]))) {
                        met = m;
                        break;
                    }
                }
                if (m.isAnnotationPresent(PatchMapping.class)) {
                    PatchMapping rm = m.getAnnotation(PatchMapping.class);
                    if (url.equals(path.concat(rm.path()[0]))) {
                        met = m;
                        break;
                    }
                }
            }
            if (null != met) {
                c = clz;
                break;
            }

        }
        Preconditions.checkArgument(null != c && null != met, "No match request entry found");
        return new RequestEntry(c, met);
    }
}
