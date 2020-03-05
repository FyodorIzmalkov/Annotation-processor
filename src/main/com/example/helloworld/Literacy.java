package main.com.example.helloworld;

public class Literacy {
    private String literacy;
    private long code;

    public Literacy(String literacy, long code){
        this.literacy = literacy;
        this.code = code;
    }

    public String getLiteracy() {
        return literacy;
    }

    public void setLiteracy(String literacy) {
        this.literacy = literacy;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }
}
