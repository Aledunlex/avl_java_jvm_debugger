package fil.command;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;

/**
 * Interface for all commands.
 *
 * @param <T> the type of the result of the command
 */
public interface Command<T> {

    /**
     * Execute the command with the given parameters.
     *
     * @param params the parameters of the command
     * @return the result of the command
     * @throws IncompatibleThreadStateException if the thread is not in a state where the command can be executed
     * @throws AbsentInformationException       if the information is not available
     */
    T execute(String... params) throws IncompatibleThreadStateException, AbsentInformationException;

}
