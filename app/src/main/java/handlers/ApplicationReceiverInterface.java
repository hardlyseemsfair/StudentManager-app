package handlers;

import com.classroom.applicationactivity.ApplicationActivityAccessorInterface;

import java.util.ArrayList;

import helper.Message;

/**
 * Created by JACK on 3/30/2015.
 */
public interface ApplicationReceiverInterface extends ApplicationActivityAccessorInterface {

    public void updateFolderViewContents();

    public void updateMessageBanks(String chatGroup, Message m);

    public void updateServerFile(String filename, String workingDIR);

    public void updateGroupDirectory(String dir);





}
