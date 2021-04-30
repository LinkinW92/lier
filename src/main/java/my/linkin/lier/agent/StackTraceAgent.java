package my.linkin.lier.agent;

import lombok.extern.slf4j.Slf4j;
import my.linkin.lier.ArgsHelper;
import my.linkin.lier.Preconditions;
import my.linkin.lier.RequestEntry;
import my.linkin.lier.StackTraceContext;
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
import java.lang.reflect.Method;

/**
 * lier agent
 *
 * @author linkin
 */
@Slf4j
public class StackTraceAgent {

    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        Class[] classes = inst.getAllLoadedClasses();
        ArgsHelper argsHelper = ArgsHelper.of(agentArgs);
        StackTraceContext.initial(selectEntry(argsHelper.getUrl(), classes), classes);

        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) ->
                builder.method(ElementMatchers.<MethodDescription>any())
                        .intercept(MethodDelegation.to(StackTraceInterceptor.class));

        new AgentBuilder.Default()
                .type(ElementMatchers.nameContains("Client"))
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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> StackTraceContext.getInstance().shutdown()));
    }


    /**
     * Select a request entry for given url
     *
     * @param url
     * @param classes
     * @return
     */
    private static RequestEntry selectEntry(String url, Class[] classes) throws Exception {
        Class<?> c = null;
        Method met = null;
        for (Class<?> clz : classes) {
            if (null == clz) {
                continue;
            }
            try {
                if (!clz.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
            } catch (Exception e) {
                log.warn("Failed to get annotation for class:{}", clz);
                continue;
            }
            RequestMapping mapping = clz.getAnnotation(RequestMapping.class);
            String[] candidate = mapping.path().length == 0 ? mapping.value() : mapping.path();
            if (null == candidate || candidate.length == 0 || !url.contains(candidate[0])) {
                continue;
            }
            String path = candidate[0];

            for (Method m : clz.getDeclaredMethods()) {
                String[] subPath = null;
                if (m.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping rm = m.getAnnotation(RequestMapping.class);
                    subPath = rm.path().length == 0 ? rm.value() : rm.path();
                }
                if (m.isAnnotationPresent(GetMapping.class)) {
                    GetMapping rm = m.getAnnotation(GetMapping.class);
                    subPath = rm.path().length == 0 ? rm.value() : rm.path();
                }
                if (m.isAnnotationPresent(PostMapping.class)) {
                    PostMapping rm = m.getAnnotation(PostMapping.class);
                    subPath = rm.path().length == 0 ? rm.value() : rm.path();
                }
                if (m.isAnnotationPresent(DeleteMapping.class)) {
                    DeleteMapping rm = m.getAnnotation(DeleteMapping.class);
                    subPath = rm.path().length == 0 ? rm.value() : rm.path();
                }
                if (m.isAnnotationPresent(PutMapping.class)) {
                    PutMapping rm = m.getAnnotation(PutMapping.class);
                    subPath = rm.path().length == 0 ? rm.value() : rm.path();
                }
                if (m.isAnnotationPresent(PatchMapping.class)) {
                    PatchMapping rm = m.getAnnotation(PatchMapping.class);
                    subPath = rm.path().length == 0 ? rm.value() : rm.path();
                }
                if (subPath != null && subPath.length > 0) {
                    if (url.equals(path.concat(subPath[0]))) {
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
        log.info("Matched result, clz:{}, method:{}", c.getCanonicalName(), met.getName());
        return new RequestEntry(c, met);
    }
}
