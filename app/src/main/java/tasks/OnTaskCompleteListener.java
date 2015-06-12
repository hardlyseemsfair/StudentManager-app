package tasks;

import java.util.ArrayList;

import com.classroom.applicationactivity.ApplicationActivityAccessorInterface;

import helper.FileMoveCopyResponse;
import helper.FileUploadResponse;
import helper.ServerDeleteResponse;
import util.MessageBank;

public interface OnTaskCompleteListener extends ApplicationActivityAccessorInterface {

    //Register device task
    public void onRegisterDeviceComplete(ArrayList<String> result);

    //Get chat messages task
    public void setAllChatGroupMessages(ArrayList<MessageBank> ml);


    //Delete from server
    public void serverFileDeleted(ServerDeleteResponse args);

    //Upload to server
    public void uploadFileToServerComplete(FileUploadResponse fur);
    //FileMoveCopy response
    public void onFileMoveCopyResponse(FileMoveCopyResponse s);

}
