package gitlet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.io.File;

/** @author mishridaga
 * A class that defines the Status command.*/
public class StatusCommand implements Command {
    @Override
    public void execute(String ... args) {
        ArrayList<Branch> branches = (ArrayList<Branch>) Utils.readObject(
                new File(".gitlet/BRANCHES"), ArrayList.class);
        Branch head = Utils.readObject(new File(".gitlet/HEAD"), Branch.class);
        String headSHA = head.getSHA();
        Commit headCommit = Utils.readObject(
                new File(".gitlet/commits/" + headSHA), Commit.class);
        ArrayList<String> marked = Utils.readObject(
                new File(".gitlet/REMOVAL"), ArrayList.class);
        marked.sort(Comparator.naturalOrder());
        Comparator<Branch> comparator = new Comparator<Branch>() {
            @Override
            public int compare(Branch o1, Branch o2) {
                return o1.getBranchName().compareTo(o2.getBranchName());
            }
        };
        branches.sort(comparator);
        System.out.println("=== Branches ===");
        for (Branch branch : branches) {
            if (branch.getBranchName().equalsIgnoreCase(head.getBranchName())) {
                System.out.println('*' + branch.getBranchName());
            } else {
                System.out.println((branch.getBranchName()));
            }
        }
        HashMap<String, String> staging = Utils.readObject(
                new File(".gitlet/REFERENCES"), HashMap.class);
        List<String> keySet = new ArrayList<String>(staging.keySet());
        keySet.sort(Comparator.naturalOrder());
        System.out.println("\n=== Staged Files ===");
        for (String stagedFile : keySet) {
            System.out.println(stagedFile);
        }
        System.out.println("\n=== Removed Files ===");
        for (String mark : marked) {
            System.out.println(mark);
        }
        extraStatus(headCommit, marked, keySet, staging);
    }

    /** Extra set of status updates that takes in the
     * HEADCOMMIT, MARKED, KEYSET, and STAGING. */
    public static void extraStatus(Commit headCommit, List marked,
                                   List<String> keySet,
                                   HashMap<String, String> staging) {
        List<String> modifications = new ArrayList<>();
        List<String> removed = new ArrayList<>();
        String entry;
        for (String key : headCommit.getContents().keySet()) {
            if (!marked.contains(key) && !new File(key).exists()) {
                entry = key + " (deleted)";
                modifications.add(entry);
            } else if (!keySet.contains(key) && new File(key).exists()
                    && !Utils.sha1(Utils.readContents(
                    new File(key))).equalsIgnoreCase(
                    headCommit.getContents().get(key))) {
                entry = key + " (modified)";
                modifications.add(entry);
            }
        }
        for (String staged : keySet) {
            if (!new File(staged).exists()) {
                entry = staged + " (deleted)";
                modifications.add(entry);
            }
        }
        HashMap<String, String> shaToFileName = new HashMap<>();
        for (String key : staging.keySet()) {
            shaToFileName.put(staging.get(key), key);
        }
        for (File staged : new File(".gitlet/staging").listFiles()) {
            String sha1 = Utils.sha1(Utils.readContents(staged));
            String sha2 = Utils.sha1(Utils.readContents(
                    new File(shaToFileName.get(staged.getName()))));
            if (!sha1.equalsIgnoreCase(sha2)) {
                entry = staged.getName() + " (modified)";
                modifications.add(entry);
            }
        }
        for (String file : Utils.plainFilenamesIn(new File("."))) {
            if (!keySet.contains(file)
                    && !headCommit.getContents().containsKey(file)) {
                entry = file;
                removed.add(entry);
            }
        }
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        for (String modified : modifications) {
            System.out.println(modified);
        }
        System.out.println("\n=== Untracked Files ===");
        for (String remove : removed) {
            if (!remove.substring(0, 1).equalsIgnoreCase(".")) {
                if (!remove.equalsIgnoreCase("Makefile")) {
                    if (!remove.equalsIgnoreCase("proj3.iml")) {
                        System.out.println(remove);
                    }
                }

            }
        }
        System.out.println("\n");
    }
}





