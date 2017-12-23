package gitlet;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/** @author mishridaga
 * A class that defines the Init command.*/
public class InitCommand implements Command {

    @Override
    public void execute(String ... args) {
        File directory = new File(".gitlet");
        File staging = new File(".gitlet/staging");
        File commits = new File(".gitlet/commits");
        File blobs = new File(".gitlet/blobs");
        if (directory.exists()) {
            System.out.println("A Gitlet version-control system already"
                            + " exists in the current directory.");
            System.exit(0);
        }

        directory.mkdir();
        staging.mkdir();
        commits.mkdir();
        blobs.mkdir();

        Commit initialCommit = new Commit();
        String sha1 = Utils.sha1(initialCommit.getCommitMessage()
                + initialCommit.getTimeStamp() + initialCommit.getParent()
                + initialCommit.getContents());

        ArrayList<Branch> branches = new ArrayList<>();
        Branch masterBranch = new Branch("master", sha1);
        branches.add(masterBranch);

        HashMap<String, String> stagingReferences
                = new HashMap<String, String>();

        HashMap<String, String> commitReferences =
                new HashMap<String, String>();

        ArrayList<String> marked = new ArrayList<>();

        Utils.writeObject(new File(".gitlet/commits/" + sha1), initialCommit);
        Utils.writeObject(new File(".gitlet/BRANCHES"), branches);
        Utils.writeObject(new File(".gitlet/HEAD"), masterBranch);
        Utils.writeObject(new File(".gitlet/REFERENCES"), stagingReferences);
        Utils.writeObject(new File(
                ".gitlet/commitREFERENCES"), commitReferences);
        Utils.writeObject(new File(".gitlet/REMOVAL"), marked);

    }
}
