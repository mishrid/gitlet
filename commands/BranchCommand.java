package gitlet;
import java.io.File;
import java.util.ArrayList;

/** @author mishridaga
 * A class that defines the Branch command.*/

public class BranchCommand implements Command {
    @Override
    public void execute(String ... args) {
        ArrayList<Branch> branches = Utils.readObject(
                new File(".gitlet/BRANCHES"), ArrayList.class);
        for (Branch branch : branches) {
            if (branch.getBranchName().equalsIgnoreCase(args[0])) {
                System.out.println("A branch with that name already exists.");
                System.exit(0);
            }
        }
        String branchName = args[0];
        Branch currentHead = Utils.readObject(
                new File(".gitlet/HEAD"), Branch.class);
        Branch branch = new Branch(branchName, currentHead.getSHA());
        branches.add(branch);
        Utils.writeObject(new File(".gitlet/BRANCHES"), branches);
    }
}
