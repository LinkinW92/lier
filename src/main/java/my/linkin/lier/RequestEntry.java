package my.linkin.lier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.StringJoiner;

/**
 * A request entry corresponding to a controller in a web service.
 * We mark down the class and method for the entry. eg: we mark a request
 * entry as: method(ClassName.java:282)
 *
 * @author linkin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestEntry {
    private static final StringJoiner joiner = new StringJoiner("#");
    private Class<?> clz;
    private Method method;

    public String getIdentifier() {
        return joiner.add(clz.getCanonicalName()).add(method.getName()).toString();
    }
}
