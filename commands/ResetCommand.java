package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/** @author mishridaga
 * A class that defines the Reset command.*/

public class ResetCommand implements Command {
    @Override
    public void execute(String ... args) {
        String commitID = args[0];
        if (!new File(".gitlet/commits/" + commitID).exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit newCommit = Utils.readObject(
                new File(".gitlet/commits/" + commitID), Commit.class);
        Branch currentHeadBranch = Utils.readObject(
                new File(".gitlet/HEAD"), Branch.class);
        Commit currentCommit = Utils.readObject(
                new File(".gitlet/commits/"
                        + currentHeadBranch.getSHA()), Commit.class);
        HashMap<String, String> contentsOfHead = currentCommit.getContents();
        for (String file : Utils.plainFilenamesIn(new File("."))) {
            if (!contentsOfHead.containsKey(file)
                    && newCommit.getContents().containsKey(file)) {
                System.out.println("There is an untracked file in the way"
                        + " delete it or add it first.");
                System.exit(0);
            }
        }
        Commit commit = Utils.readObject(
                new File(".gitlet/commits/" + commitID), Commit.class);
        HashMap<String, String> newContents = commit.getContents();
        for (Object key : newContents.keySet()) {
            Object sha = newContents.get(key);
            String contentsOfFile = Utils.readContentsAsString(
                    new File(".gitlet/blobs/" + sha));
            Utils.writeContents(new File((String) key), contentsOfFile);
        }
        for (String filename : Utils.plainFilenamesIn(new File("."))) {
            if (!newContents.containsKey(filename)
                    && contentsOfHead.containsKey(filename)) {
                if (!filename.substring(0, 1).equalsIgnoreCase(".")
                        && !filename.equalsIgnoreCase("Makefile")
                        && !filename.equalsIgnoreCase("proj3.iml")) {
                    Utils.restrictedDelete(filename);
                }
            }
        }
        currentHeadBranch.resetSHA(commitID);
        Utils.writeObject(new File(".gitlet/HEAD"), currentHeadBranch);
        ArrayList<Branch> branches = Utils.readObject(
                new File(".gitlet/BRANCHES"), ArrayList.class);
        for (Branch branch : branches) {
            if (branch.getBranchName().equalsIgnoreCase(
                    currentHeadBranch.getBranchName())) {
                branches.remove(branch);
                break;
            }
        }
        branches.add(currentHeadBranch);
        Utils.writeObject(new File(".gitlet/BRANCHES"), branches);
        for (File stagedFiles : new File(".gitlet/staging").listFiles()) {
            stagedFiles.delete();
        }
        Utils.writeObject(new File(".gitlet/REFERENCES"),
                new HashMap<String, String>());
    }
}
