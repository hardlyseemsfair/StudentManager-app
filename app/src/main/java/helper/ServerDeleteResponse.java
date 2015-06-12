package helper;

public class ServerDeleteResponse {

    private int response;
    private String workingDIR;
    private String filename;


    public ServerDeleteResponse(int response, String workingDIR, String filename) {
        super();
        this.response = response;
        this.workingDIR = workingDIR;
        this.filename = filename;
    }


    public int getResponse() {
        return response;
    }


    public void setResponse(int response) {
        this.response = response;
    }


    public String getWorkingDIR() {
        return workingDIR;
    }


    public void setWorkingDIR(String workingDIR) {
        this.workingDIR = workingDIR;
    }


    public String getFilename() {
        return filename;
    }


    public void setFilename(String filename) {
        this.filename = filename;
    }





}
