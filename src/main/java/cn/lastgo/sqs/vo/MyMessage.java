package cn.lastgo.sqs.vo;

import java.io.Serializable;
import java.util.Date;

public class MyMessage implements Serializable {

    private static final long serialVersionUID = -8013965441896177936L;

    public String id;
    public String content;
    public Date date;

    public MyMessage(String id,String content,Date date) {
        this.id = id;
        this.content = content;
        this.date = date;
    }

    public MyMessage() {
    }
}
