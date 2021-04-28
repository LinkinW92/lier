package my.linkin.lier;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Trace the stack from controller to a third client, we only need to
 * keep the relation from end to end, not all stacks.
 *
 * @author linkin
 */
@Data
public class ClientStackTracer {

    private RequestEntry controllerEntry;
    /**
     * key -> the simple class name of controller
     * value -> request entry to a third client call
     */
    private final ConcurrentMap<String, List<RequestEntry>> tracer;
    private boolean isEmpty = true;

    private static volatile ClientStackTracer INSTANCE = null;

    public static void initial(RequestEntry controllerEntry) {
        getInstance().controllerEntry = controllerEntry;
    }

    public static ClientStackTracer getInstance() {
        if (null == INSTANCE) {
            synchronized (ClientStackTracer.class) {
                if (null == INSTANCE) {
                    INSTANCE = new ClientStackTracer();
                }
            }
        }
        return INSTANCE;
    }

    private ClientStackTracer() {
        this.tracer = new ConcurrentHashMap<>(16);
    }

    public static void trace(String controller, RequestEntry entry) {
        ConcurrentMap<String, List<RequestEntry>> tracer = getInstance().getTracer();
        if (tracer.containsKey(controller)) {
            tracer.get(controller).add(entry);
            return;
        }
        List<RequestEntry> entries = new ArrayList<>();
        entries.add(entry);
        tracer.put(controller, entries);
        getInstance().isEmpty = false;
    }

    public static boolean isEmpty() {
        return getInstance().isEmpty;
    }
}
