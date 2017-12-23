package gitlet;
import java.io.File;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author mishridaga
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        dirExists(args);
        switch (args[0]) {
        case "init":
            Command init = new InitCommand();
            init.execute();
            break;
        case "add":
            Command add = new AddCommand();
            add.execute(args[1]);
            break;
        case "commit":
            Command commit = new CommitCommand();
            commit.execute(args[1]);
            break;
        case "checkout":
            Command checkout = new CheckoutCommand();
            checkout.execute(args);
            break;
        case "log":
            Command log = new LogCommand();
            log.execute();
            break;
        case "global-log":
            Command globalLog = new GlobalLogCommand();
            globalLog.execute();
            break;
        case "find":
            Command find = new FindCommand();
            find.execute(args[1]);
            break;
        case "branch":
            Command branch = new BranchCommand();
            branch.execute(args[1]);
            break;
        case "reset":
            Command reset = new ResetCommand();
            reset.execute(args[1]);
            break;
        case "rm":
            Command rm = new RemoveCommand();
            rm.execute(args[1]);
            break;
        case "rm-branch":
            Command rmBranch = new RmBranchCommand();
            rmBranch.execute(args[1]);
            break;
        case "status":
            Command status = new StatusCommand();
            status.execute();
            break;
        case "merge":
            Command merge = new MergeCommand();
            merge.execute(args[1]);
            break;
        default:
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
    }

    /** Checks if a directory exists, if ARGS isn't init
     *  and the directory doesn't exist - exit. Returns whether
     *  a directory exists */
    public static boolean dirExists(String ... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
            return true;
        } else if (!new File(".gitlet").exists()
                && !args[0].equalsIgnoreCase("init")) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
            return true;
        } else {
            return false;
        }
    }
}


