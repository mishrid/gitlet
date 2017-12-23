package gitlet;

import java.io.Serializable;

/** @author mishridaga
 * A class that defines the data type Branch which stores the branch
 * name and the SHA of the current head of that branch.*/

public class Branch implements Serializable {
    /** Sha that denotes the head of the branch's sha1.*/
    private String sha;
    /** String that denotes the branchName.*/
    private String branchName;

    /** Branch constructor that requires a BRANCH name and a SHAHEAD id.*/
    public Branch(String branch, String shaHead) {
        this.branchName = branch;
        this.sha = shaHead;

    }

    /** Returns branchName.*/
    public String getBranchName() {
        return branchName;
    }

    /** Returns this.sha.*/
    public String getSHA() {
        return sha;
    }

    /** Method that allows reset of a branch's sha to SHAHEAD.*/
    void resetSHA(String shaHead) {
        this.sha = shaHead;
    }

}
