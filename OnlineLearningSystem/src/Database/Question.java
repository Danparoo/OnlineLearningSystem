package Database;

import java.io.Serializable;
import java.sql.Timestamp;

public class Question implements Serializable {
    private int questionid;
    private String questioncontent, a, b, c, d, correctans;
    public Question(int questionid, String a, String b, String c, String d, String correctans) {
        super();
        this.questionid = questionid;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.correctans = correctans;
    }
    public int getQuestionid() {
        return questionid;
    }
    public void setQuestionid(int questionid) {
        this.questionid = questionid;
    }
    public String getA() {
        return a;
    }
    public void setA(String a) {
        this.a = a;
    }
    public String getB() {
        return b;
    }
    public void setB(String b) {
        this.b = b;
    }
    public String getC() {
        return c;
    }
    public void setC(String c) {
        this.c = c;
    }
    public String getD() {
        return d;
    }
    public void setD(String d) {
        this.d = d;
    }
    public String getCorrectans() {
        return correctans;
    }
    public void setCorrectans(String correctans) {
        this.correctans = correctans;
    }

}
