package gitlet;
import ucb.junit.textui;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

/**
 * The suite of all JUnit tests for the gitlet package.
 *
 * @author
 */
public class UnitTest {

    /**
     * Run the JUnit tests in the loa package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    @Test
    public void checkOut() {
        Command initialize = new InitCommand();
        Command add = new AddCommand();
        Command commit = new CommitCommand();
        Command branch = new BranchCommand();
        Command checkout = new CheckoutCommand();
        initialize.execute();
        add.execute("g.txt");
        commit.execute("one");
        add.execute("f.txt");
        commit.execute("split");
        branch.execute("dog");
        add.execute("h.txt");
        commit.execute("two");
        checkout.execute("checkout", "dog");
    }

    @Test
    public void checkOutOther() {
        Command checkout = new CheckoutCommand();
        Command branch = new BranchCommand();
        Command commit = new CommitCommand();
        Command reset  = new ResetCommand();
        Command rm = new RemoveCommand();
        Command merge = new MergeCommand();
        Command log = new LogCommand();
        Main.main("status");
    }

    @Test
    public void step() {
        ArrayList<Branch> branches = Utils.readObject(
                new File(".gitlet/BRANCHES"), ArrayList.class);
        Branch headBranch = Utils.readObject(
                new File(".gitlet/HEAD"), Branch.class);
        String sha = headBranch.getSHA();
        Commit commit = Utils.readObject(
                new File(".gitlet/commits/" + sha), Commit.class);

    }
}



