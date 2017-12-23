package gitlet;

/** @author mishridaga
 * An interface that defines a command. */
public interface Command {
     /*** ARGS as the inputs, runs a command specific
      *  execute method.  */
    void execute(String ... args);
}
