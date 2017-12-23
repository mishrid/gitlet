package gitlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;

/** @author mishridaga
 * A class that defines the Commit Command.*/
public class CommitCommand implements Command {

    @Override
    public void execute(String ... args) {
        HashMap<String, String> contents = new HashMap<>();
        HashMap<String, String> stagingReferences = Utils.readObject(
                new File(".gitlet/REFERENCES"), HashMap.class);
        Branch head = Utils.readObject(new File(".gitlet/HEAD"), Branch.class);
        Commit parentCommit = Utils.readObject(
                new File(".gitlet/commits/" + head.getSHA()), Commit.class);
        HashMap<String, String> parentContents = parentCommit.getContents();
        File staging = new File(".gitlet/staging");
        ArrayList<String> marked = Utils.readObject(
                new File(".gitlet/REMOVAL"), ArrayList.class);
        if (stagingReferences.size() == 0 && marked.size() == 0) {
            System.out.println(" No changes added to the commit.");
            System.exit(0);
        }
        if (args[0].equalsIgnoreCase("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        for (String entry : stagingReferences.keySet()) {
            String sha = stagingReferences.get(entry);
            contents.put(entry, sha);
        }
        for (String entry : parentContents.keySet()) {
            if (!contents.containsKey(entry)
                    && !marked.contains(entry)) {
                contents.put(entry, parentContents.get(entry));
            }
        }
        for (File file :staging.listFiles()) {
            file.renameTo(new File(".gitlet/blobs/" + file.getName()));
        }
        Utils.writeObject(new File(".gitlet/REFERENCES"),
                new HashMap<String, String>());
        Commit commit = new Commit(args[0], parentCommit.getSha());
        commit.setContents(contents);
        String sha1 = Utils.sha1(commit.getCommitMessage()
                + commit.getTimeStamp() + commit.getParent()
                + commit.getContents());
        Utils.writeObject(new File(".gitlet/commits/" + sha1), commit);
        head.resetSHA(sha1);
        Utils.writeObject(new File(".gitlet/HEAD"), head);
        HashMap<String, String> commitReferences = Utils.readObject(
                new File(".gitlet/commitREFERENCES"), HashMap.class);
        commitReferences.put(sha1.substring(0, 6), sha1);
        Utils.writeObject(new File(
                ".gitlet/commitREFERENCES"), commitReferences);
        ArrayList<Branch> branches = Utils.readObject(
                new File(".gitlet/BRANCHES"), ArrayList.class);
        for (Branch branch : branches) {
            if (branch.getBranchName().equalsIgnoreCase(head.getBranchName())) {
                branches.remove(branch);
                break;
            }
        }
        branches.add(head);
        Utils.writeObject(new File(".gitlet/BRANCHES"), branches);
        Utils.writeObject(new File(".gitlet/REMOVAL"), new ArrayList<>());

    }

}


