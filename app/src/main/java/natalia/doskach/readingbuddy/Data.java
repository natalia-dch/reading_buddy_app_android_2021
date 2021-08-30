package natalia.doskach.readingbuddy;

import android.text.Spannable;
import android.text.SpannableStringBuilder;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Data {
    public static FirebaseAuth mAuth;
    public static String[] genresArray = {"Fiction","Nonfiction","Fantasy","Crime & Thriller","Sci-Fi","AutoBiography & Memoir","Historical Fiction","History","Classics","Young Adult"};


    public static String createSmallText(User user){
        return "likes "+strFromArray(user.genres);
    }





    public static SpannableStringBuilder createText(User user){
        String[] arr = new String[10];
        arr[1] = strFromArrayBooks(user.TBR);
        arr[3] = strFromArrayBooks(user.favBooks);
        arr[5] = strFromArray(user.genres);
        arr[7] = user.address==null?"": user.address;
        arr[9] = user.about==null?"": user.about;
        arr[0]="TBR:";
        arr[2]="\nFavorite books:";
        arr[4]="\n" +"Favorite genres:";
        arr[6]="\n"+"Location:";
        arr[8]="\n"+"About:";
        String text = "";
        for (int i = 0; i < 10; i++) {
            text+=arr[i];
        }
        final SpannableStringBuilder str = new SpannableStringBuilder(text);
        int size = 0;
        for (int i = 0; i < 10; i++) {
            if(i%2==0)
            {str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), size, size+arr[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            size+= arr[i].length();
            }
            else{

                size+=arr[i].length();

            }        }
        return str;
    }

    private static String strFromArray(HashMap<String,Boolean> genres) {
        String s = "";
        Iterator it = genres.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if((Boolean) pair.getValue())
                    s +=pair.getKey() + ", ";
//            it.remove(); // avoids a ConcurrentModificationException
        }
        return s.substring(0,s.length()-2);
    }

    private static String strFromArrayBooks(HashMap<String, Book> books) {
        String s = "";
        Iterator it = books.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            s +="\""+((Book)pair.getValue()).title+"\" by "+((Book)pair.getValue()).author+", ";
//            it.remove(); // avoids a ConcurrentModificationException
        }
        return s.substring(0,s.length()-2);
    }
}


