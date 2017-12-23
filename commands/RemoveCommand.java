package gitlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;

/** @author mishridaga
 * A class that defines the Remove command.*/
public class RemoveCommand implements Command {
    @Override
    public void execute(String ... args) {
        HashMap<String, String> stagingReferences
                = Utils.readObject(
                        new File(".gitlet/REFERENCES"), HashMap.class);
        Branch head = Utils.readObject(new File(".gitlet/HEAD"), Branch.class);
        String parentSHA = head.getSHA();
        Commit parentCommit = Utils.readObject(
                new File(".gitlet/commits/" + parentSHA), Commit.class);
        if (!stagingReferences.containsKey((args[0]))
                && !parentCommit.getContents().containsKey(args[0])) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        if (stagingReferences.containsKey(args[0])) {
            String sha = stagingReferences.get(args[0]);
            stagingReferences.remove(args[0]);
            File staged = new File(".gitlet/staging/" + sha);
            staged.delete();
        }
        if (parentCommit.getContents().containsKey(args[0])) {
            ArrayList<String> marked = Utils.readObject(
                    new File(".gitlet/REMOVAL"), ArrayList.class);
            marked.add(args[0]);
            File delete = new File(args[0]);
            Utils.restrictedDelete(delete);
            Utils.writeObject(
                    new File(".gitlet/REMOVAL"), marked);
        }

        Utils.writeObject(new File(".gitlet/REFERENCES"), stagingReferences);
    }
}
