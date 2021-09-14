package natalia.doskach.readingbuddy;

import java.util.Date;

public class Message {
    public int sender;
    public String message;
    public long date;
    public Message(int s,  String m, long d){
        this.sender = s;
        this.message = m;
        this.date = d;
    }
    public Message(){};

}
