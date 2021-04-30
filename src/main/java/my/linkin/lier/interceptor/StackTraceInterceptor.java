package my.linkin.lier.interceptor;

import lombok.extern.slf4j.Slf4j;
import my.linkin.lier.RequestEntry;
import my.linkin.lier.StackTraceContext;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;


/**
 * Find out the end-to-end relation between controller and client.
 * This method intercept all third clients.
 *
 * @author linkin
 */
@Slf4j
public class StackTraceInterceptor {

    private static final int MIN_STACK_SIZE = 3;

    @RuntimeType
    @SuppressWarnings("unused")
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        log.info("Trigger intercept..., method:{}", method.getName());
        try {
            RequestEntry controllerEntry = StackTraceContext.getInstance().getControllerEntry();
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            if (null == elements || elements.length < MIN_STACK_SIZE) {
                log.warn("Unsatisfied stack trace...");
            } else {
                StackTraceElement end = elements[1], from = null;
                for (int i = 2; i < elements.length; i++) {
                    if (elements[i].getClassName().contains(controllerEntry.getClz().getSimpleName())) {
                        from = elements[i];
                        break;
                    }
                }
                // `from` maybe null if the request entry is not from a controller
                // Here, we should make sure all third clients are in the same package and everyone
                // has unique full outfitted name
                if (null != from) {
                    Class<?> candidate = null;
                    for (Class clz : StackTraceContext.getInstance().getLoadedClasses()) {
                        if (clz.getSimpleName().equals(end.getClassName())) {
                            candidate = clz;
                            break;
                        }
                    }
                    if (null != candidate) {
                        StackTraceContext.trace(new RequestEntry(candidate, method));
                    }
                }
            }
        } catch (Exception e) {

        }
        return callable.call();
    }
}
