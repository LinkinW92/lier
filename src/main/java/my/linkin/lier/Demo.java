package my.linkin.lier;


import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;

public class Demo {

    public static void main(String[] args) {
        VirtualMachine vm = null;
        try {
            vm = VirtualMachine.attach("9002");
            vm.loadAgent("");
        } catch (AttachNotSupportedException ase) {

        } catch (IOException ioe) {

        } catch (AgentLoadException | AgentInitializationException aes) {

        } finally {
            if (null != vm) {
                try {
                    vm.detach();
                } catch (IOException ex) {

                }
            }
        }
    }
}
