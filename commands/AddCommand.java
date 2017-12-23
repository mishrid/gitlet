package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/** @author mishridaga
 * Class that implements Command and defines a Command that stages a file.*/
public class AddCommand implements Command {
    @Override
    public void execute(String ... args) {
        if (!new File(args[0]).exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String shaFile = Utils.sha1(Utils.readContents(new File(args[0])));

        Branch head = Utils.readObject(new File(".gitlet/HEAD"), Branch.class);

        String shaHead = head.getSHA();

        ArrayList<String> marked = Utils.readObject(
                new File(".gitlet/REMOVAL"), ArrayList.class);

        Commit headCommit = Utils.readObject(new File(".gitlet/commits/"
                + shaHead), Commit.class);

        if (marked.contains(args[0])) {
            marked.remove(args[0]);
            Utils.writeObject(new File(".gitlet/REMOVAL"), marked);
        } else {
            if (headCommit.getContents().containsKey(shaFile)) {
                File existing = new File(".gitlet/staging/" + shaFile);
                if (existing.exists()) {
                    existing.delete();
                }
            } else if (headCommit.getContents().size()
                    != 0 && headCommit.getContents().containsKey(args[0])
                    && headCommit.getContents().get(args[0]).equalsIgnoreCase(
                            shaFile)) {
                System.exit(0);
            } else {
                Utils.writeContents(new File(".gitlet/staging/" + shaFile),
                        Utils.readContentsAsString(new File(args[0])));
                HashMap stagingReferences = Utils.readObject(new File(
                        ".gitlet/REFERENCES"), HashMap.class);
                stagingReferences.put(args[0], shaFile);
                Utils.writeObject(new File(".gitlet/REFERENCES"),
                        stagingReferences);
            }


        }
    }

}
