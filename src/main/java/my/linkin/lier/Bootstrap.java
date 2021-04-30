package my.linkin.lier;


import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;

/**
 * @author linkin
 */
public class Bootstrap {

    public static void main(String[] args) {
        VirtualMachine vm = null;
        try {
//            List<VirtualMachineDescriptor> list = VirtualMachine.list();
//            for (VirtualMachineDescriptor vmd : list) {
//                if (vmd.displayName().endsWith("Main")) {
//                    VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
//                    virtualMachine.loadAgent("/usr/local/study-code/lier/target/lier-1.0-SNAPSHOT.jar");
//                    System.out.println("ok");
//                    virtualMachine.detach();
//                }
//            }
            vm = VirtualMachine.attach("49237");
            System.out.println("ok");
            vm.loadAgent("/usr/local/study-code/lier/target/lier.jar", "-url /mss/v1/ranking/prepare");
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
