package fil.command;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;

/**
 * install a breakpoint at line lineNumber of file fileName.
 * This breakpoint is removed after being reached.
 */
public class BreakOnceCommand extends BreakCommand {

    public BreakOnceCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    protected void customizeBreakpoint(BreakpointRequest bReq, String[] args) {
        bReq.putProperty("once", true);
    }

}
