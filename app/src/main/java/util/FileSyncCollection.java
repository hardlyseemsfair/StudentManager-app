package util;

import java.io.File;
import java.util.ArrayList;

import helper.FileData;

import android.util.Log;

import util.Config;

public class FileSyncCollection {

    FileCollection serverFiles;
    FileCollection localFiles;
    ArrayList<SyncObject> syncObjects;
    private String workingDIR;

    private final String UPDATE_TO_LOCAL = "update_local";
    private final String UPDATE_TO_SERVER = "update_server";

    public FileSyncCollection(String workingDIR, FileCollection serverFiles, FileCollection localFiles) {
        this.serverFiles = serverFiles;
        this.localFiles = localFiles;
        this.workingDIR = workingDIR;
        syncObjects = new ArrayList<>();
    }

    /**
     * Constructs the syncObjects arraylist to be processed for file upload /
     * download
     */
    public void buildSyncCollection() {
        // Check that either local or server dir is not empty. If it is, dump
        // all contents

        if (serverFiles.isEmpty() && !localFiles.isEmpty()) {
            dumpFiles(localFiles, UPDATE_TO_SERVER);
        } else if (!serverFiles.isEmpty() && localFiles.isEmpty()) {
            dumpFiles(serverFiles, UPDATE_TO_LOCAL);
        } else {
            // add files that exist only in each list idividually and flag their
            // destination
            filesExistOnlyOnServer();
            filesExistOnlyOnDevice();
            // For the rest of the matched files check against companion to see
            // if modification is different and set accordingly
            checkMatchObjects();
        }
    }

    /**
     * Copy FileCollection into syncObjects using provided tag
     *
     * @param fc
     *            The file collection to be copied
     * @param tag
     *            The destination tag UPDATE_TO_SERVER or UPDATE_TO_LOCAL
     */

    private void dumpFiles(FileCollection fc, String tag) {
        String flag = "";
        if (tag.equalsIgnoreCase(UPDATE_TO_SERVER)) {
            flag = Config.SEND_TO_SERVER;
        } else if (tag.equalsIgnoreCase(UPDATE_TO_LOCAL)) {
            flag = Config.SEND_TO_DEVICE;
        }
        for (FileData f : fc) {
            SyncObject so = new SyncObject(fc.getWorkingDIR(), f.getName(), flag);
            if (so != null)
                syncObjects.add(so);
        }
    }

    /**
     * Check that a file is only present on the server and not local device
     */
    private void filesExistOnlyOnServer() {
        for (FileData sfile : serverFiles) {
            if (!localFiles.fileExistsInCollection(sfile)) {
                Log.v("FILE SYNC COLLECTION", "Server file not on device: " + sfile.getName());
                SyncObject so = new SyncObject(serverFiles.getWorkingDIR(), sfile.getName(), Config.SEND_TO_DEVICE);
                if (so != null)
                    syncObjects.add(so);
            }
        }
    }

    /**
     * Check that a file is only present on the device and not on the server
     */
    private void filesExistOnlyOnDevice() {
        for (FileData lfile : localFiles) {
            if (!serverFiles.fileExistsInCollection(lfile)) {
                SyncObject so = new SyncObject(serverFiles.getWorkingDIR(), lfile.getName(), Config.SEND_TO_SERVER);
                syncObjects.add(so);
            }
        }
    }

    /**
     * Check same named objects to determine if one if more recent than the
     * other and if so add to syncObject collection
     */
    private void checkMatchObjects() {
        for (FileData sFile : serverFiles) {
            for (FileData lFile : localFiles) {
                if (compareFiles(sFile, lFile).equals(UPDATE_TO_LOCAL)) {
                    SyncObject so = new SyncObject(serverFiles.getWorkingDIR(), lFile.getName(), Config.SEND_TO_DEVICE);
                    syncObjects.add(so);
                } else if (compareFiles(sFile, lFile).equals(UPDATE_TO_SERVER)) {
                    SyncObject so = new SyncObject(serverFiles.getWorkingDIR(), lFile.getName(), Config.SEND_TO_SERVER);
                    syncObjects.add(so);
                }
            }
        }
    }

    /**
     * Compare two files to determine which is most recent
     *
     * @param sFile
     *            server file to compare
     * @param lFile
     *            local file to compare
     * @return String flag for copy destination or null if files are the same
     */
    private String compareFiles(FileData sFile, FileData lFile) {
        if (sFile.getName().equalsIgnoreCase(lFile.getName())) {
            // If server last modified is less than local, local file is newer
            if (sFile.getSize() != lFile.getSize()) {
                if (sFile.lastModified() < lFile.lastModified()) {
                    return UPDATE_TO_SERVER;
                    // If server last modified is more than local, server file
                    // is
                    // newer
                } else if (sFile.lastModified() > lFile.lastModified()) {
                    return UPDATE_TO_LOCAL;
                }
            }
        }
        return "";
    }

    /**
     * Clear syncObjects
     */
    public void clearSyncObjects() {
        syncObjects.clear();
    }

    /**
     * Get working dir sync is associated with
     */
    public String getWorkingDir() {
        return workingDIR;
    }

    /**
     * Get syncObjects array
     */
    public ArrayList<SyncObject> getSyncObjectsArray() {
        return syncObjects;
    }

    /**
     * Prints syncObjects
     */
    public String toString() {
        return syncObjects.toString();
    }

    /**
     * Class for handling objects to sync between server and local device.
     * Contains names, the directory it belongs to and a destination flag
     *
     * @author JACK
     *
     */
    public class SyncObject {

        private String flag;
        private String sourceDIR;
        private String name;

        public SyncObject(String sourceDIR, String name, String flag) {
            this.sourceDIR = sourceDIR;
            this.name = name;
            this.flag = flag;
        }

        public void setAction(String flag) {
            this.flag = flag;
        }

        public String getFlag() {
            return flag;
        }

        public String getSourceDIR() {
            return sourceDIR;
        }

        public String getName() {
            return name;
        }

        public String toString() {
            return "{name: " + name + ", sourceDIR: " + sourceDIR + ", flag: " + flag + "}";
        }
    }

}
