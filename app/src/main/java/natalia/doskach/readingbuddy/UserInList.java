package natalia.doskach.readingbuddy;

public class UserInList implements Comparable<UserInList>{
    public String comment;
    public User user;
    public int rank;
    String id;
    public UserInList(User u, String c, int r, String id){
        user = u;
        comment = c;
        rank = r;
        this.id = id;
    }
    public UserInList(String id){
        this.id = id;
    }

    @Override
    public int compareTo(UserInList o) {
        return o.rank - this.rank;
    }

}
