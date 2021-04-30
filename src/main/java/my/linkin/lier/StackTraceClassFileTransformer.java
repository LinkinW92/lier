package my.linkin.lier;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Transform a client to trace the call stack from controller to current client itself.
 * {@link StackTraceContext#trace(String, RequestEntry)}
 *
 * @author linkin
 */
public class StackTraceClassFileTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!(className.contains("client") && className.contains("Client"))) {
            return classfileBuffer;
        }
        return new byte[0];
    }
}
