package fil.command;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;

/**
 * install a breakpoint at line lineNumber of file fileName.
 */
public class BreakCommand implements Command<Void> {

    protected final VirtualMachine vm;

    public BreakCommand(VirtualMachine vm) {
        this.vm = vm;
    }

    @Override
    public Void execute(String... args) throws AbsentInformationException {
        try {
            Location location = getLocation(args);
            BreakpointRequest bReq = vm.eventRequestManager().createBreakpointRequest(location);
            customizeBreakpoint(bReq, args);
            bReq.enable();
        } catch (IndexOutOfBoundsException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Returns the location of where the breakpoint should be placed.
     *
     * @param args the first argument is the filename, the second is the line number
     * @return the location of where the breakpoint should be placed
     */
    protected Location getLocation(String[] args) {
        String filename = args[0];
        int lineNumber = Integer.parseInt(args[1]);
        try {
            for (ReferenceType refType : vm.classesByName(filename)) {
                return refType.locationsOfLine(lineNumber).get(0);
            }
            throw new RuntimeException("No class found for " + filename);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't place a breakpoint at " + filename + ":" + lineNumber);
        }
    }

    /**
     * Customize the breakpoint request : add conditions, etc. By default, does nothing.
     *
     * @param bReq the breakpoint request to customize
     * @param args the arguments passed to the command
     */
    protected void customizeBreakpoint(BreakpointRequest bReq, String[] args) {
        // no additional customization by default
    }

}
