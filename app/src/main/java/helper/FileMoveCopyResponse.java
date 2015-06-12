package helper;

/**
 * Created by NAPOLEON on 5/17/2015.
 */
public class FileMoveCopyResponse {

    private String mask;
    private String filename;
    private String sourceDIR;
    private String destDIR;
    int error = 0;
    String errormsg = "";

    public FileMoveCopyResponse( String mask, String filename, String sourceDIR, String destDIR) {
        this.mask = mask;
        this.filename = filename;
        if(filename.contains("Camera")) this.filename = filename.substring(0, filename.indexOf("/"));
        this.sourceDIR = sourceDIR;
        this.destDIR = destDIR;
    }

    public String getMask() {
        return mask;
    }

    public String getSourceDIR() {
        return sourceDIR;
    }

    public String getDestDIR() {
        return destDIR;
    }

    public int getError(){
        return error;
    }

    public void setError(int e){
        error = e;
    }

    public String getErrorMessage(){
        return errormsg;
    }

    public void setErrorMessage(String s){
        errormsg = s;
    }

    public String getFilename(){
        return filename;
    }



}
