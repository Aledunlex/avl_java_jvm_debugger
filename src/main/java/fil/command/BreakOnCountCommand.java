package fil.command;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;

/**
 * Install a breakpoint at line lineNumber of file fileName.
 * This breakpoint is only activated after being reached a certain number of times count.
 */
public class BreakOnCountCommand extends BreakCommand {

    public BreakOnCountCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    protected void customizeBreakpoint(BreakpointRequest bReq, String[] args) {
        int count = Integer.parseInt(args[2]);
        bReq.addCountFilter(count);
    }

}
