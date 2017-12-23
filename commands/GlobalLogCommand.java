package gitlet;
import java.io.File;

/** @author mishridaga
 * A class that defines the globalLog command.*/

public class GlobalLogCommand implements Command {
    @Override
    public void execute(String ... args) {
        File commits = new File(".gitlet/commits");
        for (File file :commits.listFiles()) {
            Commit commit = Utils.readObject(file, Commit.class);
            System.out.println(commit.toString());
        }
        System.exit(0);
    }
}
