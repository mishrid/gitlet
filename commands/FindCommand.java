package gitlet;

import java.io.File;
/** @author mishridaga
 * A class that defines the Find command.*/
public class FindCommand implements Command {
    @Override
    public void execute(String ... args) {
        File commits = new File(".gitlet/commits");
        boolean exists = false;
        for (File file :commits.listFiles()) {
            Commit commit = Utils.readObject(file, Commit.class);
            if (commit.getCommitMessage().equalsIgnoreCase(args[0])) {
                exists = true;
                System.out.println(file.getName());
            }
        }
        if (!exists) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

}
