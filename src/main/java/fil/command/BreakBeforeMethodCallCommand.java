package fil.command;

import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;

/**
 * configure the execution to stop at the very beginning of the execution of the method methodName.
 */
public class BreakBeforeMethodCallCommand extends BreakCommand {

    public BreakBeforeMethodCallCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    protected Location getLocation(String[] args) {
        String className = args[0];
        String methodName = args[1];
        for (ReferenceType refType : vm.classesByName(className)) {
            for (Method method : refType.methodsByName(methodName)) {
                return method.location();
            }
        }
        throw new RuntimeException("Couldn't place a breakpoint at " + className + " for method " + methodName);
    }

}
