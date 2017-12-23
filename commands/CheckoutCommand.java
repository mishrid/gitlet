package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/** @author mishridaga
 * A class that defines the Checkout command.*/
public class CheckoutCommand implements Command {
    @Override
    public void execute(String ... args) {
        if (args.length == 3) {
            if (!args[1].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            checkoutFile(args[2]);
        } else if (args.length == 4) {
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            String sha1 = "";
            if (args[1].length() <= Utils.UID_LENGTH) {
                for (String file : Utils.plainFilenamesIn(
                        new File(".gitlet/commits"))) {
                    if (file.contains(args[1])) {
                        sha1 = file;
                        break;
                    }
                }
                if (sha1.equalsIgnoreCase("")) {
                    sha1 = args[1];
                }
            } else {
                sha1 = args[1];
            }
            String[] arguments = new String[2];
            arguments[0] = sha1;
            arguments[1] = args[3];
            checkoutCommit(arguments);
        } else if (args.length == 2) {
            checkoutBranch(args[1]);
        }
    }

    /** A method that checks out a specific file using ARGS as the filename. */
    public void checkoutFile(String ... args) {
        String fileName = args[0];
        Branch head = Utils.readObject(new File(".gitlet/HEAD"), Branch.class);
        String shaHead = head.getSHA();
        Commit headCommit = Utils.readObject(new File(".gitlet/commits/"
                + shaHead), Commit.class);
        if (!headCommit.getContents().containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String shaOfFile = headCommit.getContents().get(fileName);
        String contents = Utils.readContentsAsString(
                new File(".gitlet/blobs/" + shaOfFile));
        Utils.writeContents(new File(fileName), contents);
    }

    /** A method that checks out a specific file from a
     *  specific commit ID using ARGS as the filename and ID. */
    public void checkoutCommit(String ... args) {
        String commitID = args[0];
        String fileName = args[1];
        if (!new File(".gitlet/commits/" + commitID).exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else {
            Commit commit = Utils.readObject(new File(".gitlet/commits/"
                    + commitID), Commit.class);
            if (!commit.getContents().containsKey(fileName)) {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            } else {
                String shaOfFile = commit.getContents().get(fileName);
                String contents = Utils.readContentsAsString(
                        new File(".gitlet/blobs/" + shaOfFile));
                Utils.writeContents(new File(fileName), contents);
            }
        }
    }

    /** A method that checks out a specific branch using
     *  ARGS as the branchname. */
    public void checkoutBranch(String ... args) {
        ArrayList<Branch> branches = Utils.readObject(
                new File(".gitlet/BRANCHES"), ArrayList.class);
        Branch result = new Branch("", "");
        Boolean existing = false;
        for (Branch branch : branches) {
            if (branch.getBranchName().equalsIgnoreCase(args[0])) {
                result = branch;
                existing = true;
            }
        }
        if (!existing) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (Utils.readObject(new File(
                ".gitlet/HEAD"),
                Branch.class).getBranchName().equalsIgnoreCase(args[0])) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        String shaHeadCommit = Utils.readObject(
                new File(".gitlet/HEAD"), Branch.class).getSHA();
        Commit headCommit = Utils.readObject(new File(
                ".gitlet/commits/" + shaHeadCommit), Commit.class);
        HashMap contentsOfHead = headCommit.getContents();
        Commit newCommit = Utils.readObject(new File(
                ".gitlet/commits/" + result.getSHA()), Commit.class);
        HashMap newContents = newCommit.getContents();
        for (String file : Utils.plainFilenamesIn(new File("."))) {
            if (!contentsOfHead.containsKey(file)
                    && newContents.containsKey(file)) {
                System.out.println(
                        "There is an untracked file in the way"
                                + " delete it or add it first.");
                System.exit(0);
            }
        }
        for (Object key : newContents.keySet()) {
            Object sha = newContents.get(key);
            String contentsOfFile = Utils.readContentsAsString(
                    new File(".gitlet/blobs/" + sha));
            Utils.writeContents(new File((String) key), contentsOfFile);
        }
        for (String filename : Utils.plainFilenamesIn(new File("."))) {
            if (!newContents.containsKey(filename)
                    && contentsOfHead.containsKey(filename)) {
                Utils.restrictedDelete(filename);
            }
        }
        Utils.writeObject(new File(".gitlet/HEAD"), result);
        for (File stagedFiles : new File(".gitlet/staging").listFiles()) {
            stagedFiles.delete();
        }
        Utils.writeObject(new File(
                ".gitlet/REFERENCES"), new HashMap<String, String>());
    }
}
