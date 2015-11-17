package dialogs;

import com.classroom.applicationactivity.ApplicationActivityAccessorInterface;

import helper.FileData;

/**
 * Interface for functions returned from dialogs
 * @author JACK
 *
 */
public interface DialogTaskListener extends ApplicationActivityAccessorInterface{

    //Generated from RequestHelpDialog
    public void onHelpRequested(String filename, String value, String message);

    //Generated from DeleteFileDialog
    public void deleteFileTask(String filename, String dir);
    //public void updateFolderViewContents();

    //Chat fragment
    public void executeChatFragment(String name);

    //Move / Copy call to upload to server
    public void fileMoveCopy(String filename, String sourceDIR, String destDIR, String mask);

    //RenameFile server change task
    public void renameServerFile(String newFilename, String workingDIR, String currentFilename);

    //Create a new file
    public void createNewFile(String filename, String workingDIR);

    //Store opened file data
    public void storeFileInfo(FileData fd);

    public void handleCameraDir(String destination);
}
