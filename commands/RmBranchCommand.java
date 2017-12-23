package gitlet;

import java.util.ArrayList;
import java.io.File;

/** @author mishridaga
 * A class that defines the RmBranch command.*/

public class RmBranchCommand implements Command {
    @Override
    public void execute(String ... args) {
        Branch head = Utils.readObject(new File(".gitlet/HEAD"), Branch.class);
        ArrayList<Branch> branches = Utils.readObject(
                new File(".gitlet/BRANCHES"), ArrayList.class);
        if (args[0].equalsIgnoreCase(head.getBranchName())) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        boolean existing = false;
        for (Branch branch : branches) {
            if (branch.getBranchName().equalsIgnoreCase(args[0])) {
                branches.remove(branch);
                existing = true;
                break;
            }
        }
        if (!existing) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        Utils.writeObject(new File(".gitlet/BRANCHES"), branches);

    }

}
