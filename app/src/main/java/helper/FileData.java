package helper;

public class FileData{

    private String name;
    private long size;
    private long modifiedOn;
    private String currentDIR = "";


    public FileData(String name, long size, long modifiedOn) {
        super();
        this.name = name;
        this.size = size;
        this.modifiedOn = modifiedOn;
    }

    public FileData(String name, long size, long modifiedOn, String currentDIR) {
        super();
        this.name = name;
        this.size = size;
        this.modifiedOn = modifiedOn;
        this.currentDIR = currentDIR;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long lastModified() {
        return modifiedOn;
    }

    public String getDir(){
        return currentDIR;
    }

    @Override
    public String toString(){
        return "{Name: " + name + ", Size: " + size + ", Last Modfied" + lastModified() + "}";

    }

}

