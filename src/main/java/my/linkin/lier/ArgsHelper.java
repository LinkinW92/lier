package my.linkin.lier;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * A helper for agent args
 *
 * @author linkin
 */
@Data
public class ArgsHelper {
    private String args;
    private final String URL = "url";
    private Map<String, String> parameters;

    public static ArgsHelper of(String args) {
        Preconditions.checkNotNull(args);
        Map<String, String> parameters = new HashMap<>(8);
        String[] splits = args.split("-");
        for (String split : splits) {
            String[] kv = split.trim().split("\\s");
            if (1 == kv.length) {
                parameters.put(kv[0], null);
            }
            if (2 == kv.length) {
                parameters.put(kv[0], kv[1]);
            }
        }

        ArgsHelper helper = new ArgsHelper();
        helper.setParameters(parameters);
        helper.setArgs(args);
        return helper;
    }

    public String getUrl() {
        return Preconditions.checkNotNull(parameters.get(URL),
                "No url specified for controller");
    }
}
