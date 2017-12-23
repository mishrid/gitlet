package gitlet;

import java.io.File;

/** @author mishridaga
 * A class that defines the log command.*/

public class LogCommand implements Command {
    @Override
    public void execute(String ... args) {
        Branch head = Utils.readObject(new File(".gitlet/HEAD"), Branch.class);
        String shaHead = head.getSHA();
        Commit headCommit = Utils.readObject(
                new File(".gitlet/commits/" + shaHead), Commit.class);
        while (!headCommit.getParent().equalsIgnoreCase(
                "0")) {
            System.out.println(headCommit.toString());
            shaHead = headCommit.getParent();
            headCommit = Utils.readObject(
                    new File(".gitlet/commits/" + shaHead), Commit.class);
        }
        System.out.println(headCommit.toString());
    }

}
