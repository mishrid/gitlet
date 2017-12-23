package gitlet;
import java.util.ArrayList;
import java.io.File;
import java.util.HashMap;

/** @author mishridaga
 * A class that defines the Merge command.*/
public class MergeCommand implements Command {
    @Override
    public void execute(String... args) {
        String givenBranchName = args[0];
        Branch givenBranch = new Branch("", "");
        ArrayList<Branch> branches = Utils.readObject(
                new File(".gitlet/BRANCHES"), ArrayList.class);
        Branch headBranch = Utils.readObject(
                new File(".gitlet/HEAD"), Branch.class);
        String headSHA = headBranch.getSHA();
        Commit headCommit = Utils.readObject(
                new File(".gitlet/commits/" + headSHA), Commit.class);
        Commit givenCommit;
        if (givenBranchName.equalsIgnoreCase(headBranch.getBranchName())) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        for (Branch branch : branches) {
            if (branch.getBranchName().equalsIgnoreCase(givenBranchName)) {
                givenBranch = branch;
            }
        }
        if (givenBranch.getBranchName().equalsIgnoreCase("")) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (new File(".gitlet/staging").listFiles().length > 0) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        for (String file : Utils.plainFilenamesIn(new File("."))) {
            if (!file.substring(0, 1).equalsIgnoreCase(".")
                    && !file.equalsIgnoreCase("Makefile")
                    && !file.equalsIgnoreCase("proj3.iml")) {
                if (!headCommit.getContents().containsKey(file)) {
                    System.out.println(
                            "There is an untracked file in the way;"
                                    + " delete it or add it first.");
                    System.exit(0);
                }
            }
        }
        givenCommit = Utils.readObject(new File(
                ".gitlet/commits/" + givenBranch.getSHA()), Commit.class);
        Commit splitPoint = findSplitPoint(headCommit, givenCommit);
        if (givenBranch.getSHA().equalsIgnoreCase(splitPoint.getSha())) {
            System.out.println(
                    "Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (headBranch.getSHA().equalsIgnoreCase(splitPoint.getSha())) {
            Command checkoutBranch = new CheckoutCommand();
            checkoutBranch.execute(givenBranchName);
            System.out.println(
                    "Current branch fast-forwarded.");
            System.exit(0);
        }
        merging(givenCommit, splitPoint,
                headCommit, headBranch, givenBranchName);
    }

    /** A merging helper method that takes in a givenCommit a GC,
     *  a SPLITPOINT, a HEADCOMMIT, the HEADBRANCH, and a GIVENBRANCHNAME. */
    public static void merging(Commit gC, Commit splitPoint,
                               Commit headCommit, Branch headBranch,
                               String givenBranchName) {
        HashMap<String, String> gCC = gC.getContents();
        HashMap<String, String> sPP = splitPoint.getContents();
        HashMap<String, String> cHC = headCommit.getContents();
        Command checkout = new CheckoutCommand();
        HashMap<String, String> contents = new HashMap<>();
        for (String gF : gCC.keySet()) {
            if (!sPP.containsKey(gF)) {
                if (!cHC.containsKey(gF)) {
                    checkout.execute("checkout",
                            gC.getSha(), "--", gF);
                    contents.put(gF, gCC.get(gF));
                } else if (!gCC.get(gF).equalsIgnoreCase(
                        cHC.get(gF))) {
                    String shaofFile = conflict1(
                            gCC, cHC, gF);
                    contents.put(gF, shaofFile);
                } else {
                    contents.put(gF, gCC.get(gF));
                }
            } else {
                if (gCC.get(gF).equalsIgnoreCase(
                        sPP.get(gF))) {
                    if (!cHC.containsKey(gF)) {
                        continue;
                    } else {
                        contents.put(gF, cHC.get(gF));
                    }
                } else {
                    if (!cHC.containsKey(gF)) {
                        String shaofFile = conflict2(gCC, gF);
                        contents.put(gF, shaofFile);
                    } else if (!cHC.get(
                            gF).equalsIgnoreCase(
                            sPP.get(gF))) {
                        if (!cHC.get(
                                gF).equalsIgnoreCase(
                                        gCC.get(gF))) {
                            String shaofFile = conflict3(gCC,
                                    cHC, gF);
                            contents.put(gF, shaofFile);
                        } else {
                            contents.put(gF,
                                    cHC.get(gF));
                        }
                    } else {
                        checkout.execute(
                                "checkout", gC.getSha(),
                                "--", gF);
                        contents.put(
                                gF, gCC.get(gF));
                    }
                }
            }
        }
        moreMerging(cHC, sPP,
                gCC, contents, headCommit,
                givenBranchName, headBranch, gC);
    }

    /** A merging helper method that takes in a givenCommit the
     * CURRENTHEADCONTENTS, SPLITPOINTCONTENTS, GIVENCOMMITCONTENTS,
     * CONTENTS for the future commit, the HEADCOMMIT, the GIVENBRANCHNAME,
     * the HEADBRANCH, and the GIVENCOMMIT. */
    public static void moreMerging(HashMap<String, String> currentHeadContents,
          HashMap<String, String> splitPointContents,
          HashMap<String, String> givenCommitContents,
          HashMap contents, Commit headCommit,
          String givenBranchName, Branch headBranch, Commit givenCommit) {
        for (String headFile : currentHeadContents.keySet()) {
            if (!splitPointContents.containsKey(headFile)) {
                if (!givenCommitContents.containsKey(headFile)) {
                    contents.put(headFile, currentHeadContents.get(headFile));
                }
            } else {
                if (!givenCommitContents.containsKey(headFile)
                        && !splitPointContents.get(headFile).equalsIgnoreCase(
                                currentHeadContents.get(headFile))) {
                    System.out.println("Encountered a merge conflict");
                    String contentsofGiven = "";
                    String contentsofHead = Utils.readContentsAsString(
                            new File(".gitlet/blobs/"
                                    + currentHeadContents.get(headFile)));
                    String contentofConflict = "<<<<<<< HEAD\n"
                            + contentsofHead + "=======\n"
                            + contentsofGiven + ">>>>>>>\n";
                    Utils.writeContents(new File(headFile), contentofConflict);
                    String shaofFile = Utils.sha1(contentofConflict);
                    Utils.writeContents(new File(".gitlet/blobs/"
                            + shaofFile), contentofConflict);
                    contents.put(headFile, shaofFile);
                } else if (splitPointContents.get(
                        headFile).equalsIgnoreCase(
                                currentHeadContents.get(headFile))) {
                    if (!givenCommitContents.containsKey(headFile)) {
                        Utils.restrictedDelete(new File(headFile));
                    }
                }
            }
        }

        Commit commit = new Commit("Merged "
                + givenBranchName + " into "
                + headBranch.getBranchName() + ".",
                headCommit.getSha(), givenCommit.getSha());
        commit.setContents(contents);
        headBranch.resetSHA(commit.getSha());
        Utils.writeObject(new File(
                ".gitlet/HEAD"), headBranch);
        Utils.writeObject(new File(
                ".gitlet/commits/" + commit.getSha()), commit);

    }

     /** A method that takes in two commits, HEAD and GIVENBRANCH
      *  and returns their shared parent Commit. */
    private Commit findSplitPoint(Commit head, Commit givenBranch) {
        ArrayList<String> headParentID = new ArrayList<>();
        String sha;
        Commit splitPoint;
        String result = "";
        while (!head.getParent().equalsIgnoreCase("0")) {
            headParentID.add(head.getSha());
            sha = head.getParent();
            head = Utils.readObject(new File(
                    ".gitlet/commits/" + sha), Commit.class);
        }
        headParentID.add(head.getParent());

        while (!givenBranch.getParent().equalsIgnoreCase("0")) {
            if (headParentID.contains(givenBranch.getSha())) {
                result = givenBranch.getSha();
                break;
            } else {
                givenBranch = Utils.readObject(new File(
                        ".gitlet/commits/"
                                + givenBranch.getParent()), Commit.class);
            }
        }
        if (result.length() == 0) {
            result = head.getParent();
        }

        splitPoint = Utils.readObject(new File(
                ".gitlet/commits/"
                        + result), Commit.class);
        return splitPoint;
    }

    /** A conflict resolution method that takes in the GIVENCOMMITCONTENTS,
     * CURRENTHEADCONTENTS, and a GIVENFILE and returns the SHA1 of the
     * conflict file. */
    public static String conflict1(HashMap<String, String> givenCommitContents,
                                HashMap<String, String> currentHeadContents,
                                String givenfile) {
        System.out.println("Encountered a merge conflict");
        String contentsofGiven = Utils.readContentsAsString(
                new File(".gitlet/blobs/"
                        + givenCommitContents.get(givenfile)));
        String contentsofHead = Utils.readContentsAsString(
                new File(".gitlet/blobs/"
                        + currentHeadContents.get(givenfile)));
        String contentofConflict = "<<<<<<< HEAD\n" + contentsofHead
                + "=======\n" + contentsofGiven + ">>>>>>>\n";
        Utils.writeContents(new File(givenfile), contentofConflict);
        String shaofFile = Utils.sha1(contentofConflict);
        Utils.writeContents(new File(".gitlet/blobs/"
                + shaofFile), contentofConflict);
        return shaofFile;

    }

    /** A conflict resolution method that takes in the GIVENCOMMITCONTENTS,
     * and a GIVENFILE and returns the SHA1 of the conflict file. */
    public static String conflict2(HashMap<String, String> givenCommitContents,
                                   String givenfile) {
        System.out.println("Encountered a merge conflict");
        String contentsofGiven = Utils.readContentsAsString(
                new File(".gitlet/blobs/"
                        + givenCommitContents.get(givenfile)));
        String contentsofHead = "";
        String contentofConflict = "<<<<<<< HEAD\n"
                + contentsofHead + "=======\n"
                + contentsofGiven + ">>>>>>>\n";
        Utils.writeContents(
                new File(givenfile), contentofConflict);
        String shaofFile = Utils.sha1(contentofConflict);
        Utils.writeContents(new File(
                        ".gitlet/blobs/" + shaofFile),
                contentofConflict);
        return shaofFile;
    }

    /** A conflict resolution method that takes in the GIVENCOMMITCONTENTS,
     * CURRENTHEADCONTENTS, and a GIVENFILE and returns the SHA1 of the
     * conflict file. */
    public static String conflict3(HashMap<String, String> givenCommitContents,
                                   HashMap<String, String> currentHeadContents,
                                   String givenfile) {
        System.out.println("Encountered a merge conflict");
        String contentsofGiven = Utils.readContentsAsString(
                new File(".gitlet/blobs/"
                        + givenCommitContents.get(
                                givenfile)));
        String contentsofHead = Utils.readContentsAsString(
                new File(".gitlet/blobs/"
                        + currentHeadContents.get(
                                givenfile)));
        String contentofConflict = "<<<<<<< HEAD\n"
                + contentsofHead + "=======\n"
                + contentsofGiven + ">>>>>>>\n";
        Utils.writeContents(new File(givenfile),
                contentofConflict);
        String shaofFile = Utils.sha1(contentofConflict);
        Utils.writeContents(new File(".gitlet/blobs/"
                + shaofFile), contentofConflict);
        Utils.writeContents(new File(givenfile), contentofConflict);
        return shaofFile;
    }



}




