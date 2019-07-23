package cn.lastgo.sqs.vo;


import java.io.Serializable;
import java.util.Date;

public class DemoMessage implements Serializable {

    private static final long serialVersionUID = -8013965441896177936L;
    public String url;
    public String content;
    public Date date;

    public DemoMessage(String url, String content, Date date) {
        this.url = url;
        this.content = content;
        this.date = date;
    }

    public DemoMessage() {
    }
}
