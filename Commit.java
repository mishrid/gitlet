package gitlet;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/** @author mishridaga
 * A class that defines a Commit data structure.*/
public class Commit implements Serializable {
    /** The commit message of a commit. */
    private String commitMessage;
    /** The date the commit was created. */
    private Date timeStamp;
    /** A Hashmap mapping the filename to its blob SHA
     * of the contents of the commit. */
    private HashMap<String, String> contents;
    /** The SHA ID of the parent commit. */
    private String parent;
    /** Arraylist of files that have been marked for removal
     *  in this commit's child. */
    private ArrayList<String> marked;
    /** The SHA ID of this commit.*/
    private String sha;

    /** The merge ID's of this commit.*/
    private String[] merge;
    /** A constructor for a commit that takes input MESSAGE
     * as the commit message and PARENTID and returns a new
     * instance of the Commit data type.*/
    Commit(String message, String parentID) {
        this.commitMessage = message;
        this.contents = new HashMap<>();
        this.parent = parentID;
        this.timeStamp = new Date();
        this.marked = new ArrayList<>();
        this.merge =  new String[2];
    }

    /** A constructor for a merge commit that takes in a MESSAGE
     * and two parent ID's, PARENTID1 and PARENTID2.*/
    Commit(String message, String parentID1, String parentID2) {
        this.commitMessage = message;
        this.contents = new HashMap<>();
        this.parent = parentID1;
        this.timeStamp = new Date();
        this.marked = new ArrayList<>();
        this.merge =  new String[2];
        this.merge[0] = parentID1;
        this.merge[1] = parentID2;

    }


    /** A method that allows the re-setting of the contents
     *  of an instance of a commit with the input POTENTIALCONTENTS.  */
    void setContents(HashMap<String, String> potentialContents) {
        this.contents = potentialContents;
        this.sha = Utils.sha1(this.getCommitMessage()
                + this.getTimeStamp() + this.getParent() + this.getContents());
    }

    /** A method that allows you to set FILENAME to be marked
     * for removal in the commits child's contents.*/
    void setMarked(String fileName) {
        this.marked.add(fileName);
    }

    /** A constructor for the initial commit.*/
    Commit() {
        this.commitMessage = "initial commit";
        this.contents = new HashMap<>();
        this.parent = "0";
        this.timeStamp = new Date(0);
        this.sha = Utils.sha1(this.getCommitMessage()
                + this.getTimeStamp() + this.getParent() + this.getContents());
        this.merge = new String[2];

    }

    /** Returns a date, the timestamp of the commit.*/
    public Date getTimeStamp() {
        return this.timeStamp;
    }

    /** Returns a string, the commit message of the commit.*/
    public String getCommitMessage() {
        return this.commitMessage;
    }

    /** Returns a hashmap, the contents mapped of the commit.*/
    public HashMap<String, String> getContents() {
        return this.contents;
    }

    /** Returns a string, the shaID of the commit.*/
    public String getSha() {
        return sha;
    }

    /** Returns a sha1ID, parent ID of this commit.*/
    public String getParent() {
        return this.parent;
    }

    /** Returns a string to be printed in log of the commit.*/
    public String toString() {
        SimpleDateFormat formatter =
                new SimpleDateFormat("EEE MMM dd HH:MM:ss yyyy Z");
        String dateStr = formatter.format(timeStamp);
        if (this.merge[0] == null) {
            return "===\n" + "commit " + this.sha + "\n"
                    + "Date: " + dateStr + "\n" + this.commitMessage + "\n";
        } else {
            return "===\n" + "commit " + this.sha + "\n"
                    + "Merge: " + this.merge[0].substring(0, 7)
                    + " " + this.merge[1].substring(0, 7) + "\n"
                    + "Date: " + dateStr + "\n" + this.commitMessage + "\n";
        }
    }






}
