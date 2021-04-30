package my.linkin.lier;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Trace the stack from controller to a third client, we only need to
 * keep the relation from end to end, not all stacks.
 *
 * @author linkin
 */
@Data
@Slf4j
public class StackTraceContext {
    private final StringJoiner joiner = new StringJoiner("\n");
    private final String tmp = "/tmp/stack_trace";

    private Class[] loadedClasses;

    private RequestEntry controllerEntry;
    /**
     * key -> the simple name consists of clz name and method name, eg: clz#method
     * value -> request entry to a third client call
     */
    private final ConcurrentMap<String, RequestEntry> tracer;
    private boolean isEmpty = true;

    private static volatile StackTraceContext INSTANCE = null;

    public static void initial(RequestEntry controllerEntry, Class[] loadedClasses) {
        getInstance().controllerEntry = controllerEntry;
        getInstance().loadedClasses = loadedClasses;
    }

    public static StackTraceContext getInstance() {
        if (null == INSTANCE) {
            synchronized (StackTraceContext.class) {
                if (null == INSTANCE) {
                    INSTANCE = new StackTraceContext();
                }
            }
        }
        return INSTANCE;
    }

    private StackTraceContext() {
        this.tracer = new ConcurrentHashMap<>(16);
    }

    public static void trace(RequestEntry entry) {
        log.info("Trace entry:{}", entry);
        String key = entry.getIdentifier();
        ConcurrentMap<String, RequestEntry> tracer = getInstance().getTracer();
        if (tracer.containsKey(key)) {
            return;
        }
        tracer.putIfAbsent(key, entry);
        getInstance().isEmpty = false;
    }

    public static boolean isEmpty() {
        return getInstance().isEmpty;
    }

    public RequestEntry getTopController() {
        return INSTANCE.controllerEntry;
    }

    /**
     * When shutdown, save the end-to-end relation to tmp file
     */
    public void shutdown() {
        if (isEmpty) {
            return;
        }
        final Set<String> identifiers = tracer.keySet();
        int i = 0;
        joiner.add("# make a test");
        for (String identifier : identifiers) {
            joiner.add(i + "\t" + identifier);
        }
        File file = new File(tmp);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.warn("Failed to create tmp file...");
                return;
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp))) {
            bw.write(joiner.toString());
        } catch (IOException ie) {
            // ignore this
        }

    }
}
