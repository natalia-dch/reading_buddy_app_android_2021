package natalia.doskach.readingbuddy;

import java.io.Serializable;

public class Book implements Serializable {
    public String author;
    public String title;
    public String ISBN;
    public String picURL;
    Book(String author, String title, String ISBN, String picURL){
        this.author = author;
        this.title = title;
        this.ISBN = ISBN;
        this.picURL = picURL;
    }
    Book(){}
}
