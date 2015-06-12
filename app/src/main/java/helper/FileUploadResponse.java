package helper;

public class FileUploadResponse {


    private int response;
    private String filename;
    private String destDIR;


    public FileUploadResponse(int response, String filename, String destDIR) {
        super();
        this.response = response;
        this.filename = filename;
        this.destDIR = destDIR;
    }


    public int getResponse() {
        return response;
    }


    public void setResponse(int response) {
        this.response = response;
    }


    public String getFilename() {
        return filename;
    }


    public String getDestDIR() {
        return destDIR;
    }

}
