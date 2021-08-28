package natalia.doskach.readingbuddy;

public class Message {
    public String sender;
    public String receiver;
    public String message;
    public Message(String s, String r, String m){
        this.sender = s;
        this.receiver = r;
        this.message = m;
    }
    public Message(){};

}
