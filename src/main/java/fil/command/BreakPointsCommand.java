package fil.command;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequestManager;

import java.util.List;

/**
 * Liste the active breakpoints and their location in the code.
 */
public class BreakPointsCommand implements Command<List<BreakpointRequest>> {
    private final VirtualMachine vm;

    public BreakPointsCommand(VirtualMachine vm) {
        this.vm = vm;
    }

    @Override
    public List<BreakpointRequest> execute(String... args) throws AbsentInformationException {
        EventRequestManager erm = vm.eventRequestManager();
        List<BreakpointRequest> breakpoints = erm.breakpointRequests();
        for (BreakpointRequest bReq : breakpoints) {
            Location loc = bReq.location();
            System.out.println("Breakpoint at " + loc.sourcePath() + " line: " + loc.lineNumber());
        }
        return breakpoints;
    }
}
