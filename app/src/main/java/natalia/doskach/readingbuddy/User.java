package natalia.doskach.readingbuddy;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {
    public String email;
    public String name;
    public String address;
    public String about;
    public String picURL;
    public HashMap<String, Book> favBooks;
    public HashMap<String, Book> TBR;
    public HashMap<String,String> friends;
    public HashMap<String,Boolean> genres;
    public User(String email, String username){
        this.email = email;
        this.name = username;
    };

    public User() { 
    }

}
