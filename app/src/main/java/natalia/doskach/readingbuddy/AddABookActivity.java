package natalia.doskach.readingbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class AddABookActivity extends AppCompatActivity {

    EditText input;
    ListView list;
    ArrayList<String> mTitle = new ArrayList<String>();
    ArrayList<String> mAuthor = new ArrayList<String>();
    ArrayList<String> images = new ArrayList<String>();
    ArrayList<String> ISBNs = new ArrayList<String>();
    BookAdapter a;
    HashMap<String, Book> bookList = new HashMap<String, Book>();
    int counter;
    boolean isOne = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getIntent().hasExtra("isOne"))
            isOne = true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_book);
        input = findViewById(R.id.input);
        input.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    search(input);
                    return true;
                }
                return false;
            }
        });
        list = findViewById(R.id.list);
        a = new BookAdapter(this, mTitle, mAuthor, images,ISBNs);
        list.setAdapter(a);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void toProfile(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void toChats(View view) {
        startActivity(new Intent(this, ChatsActivity.class));
    }

    public void search(View view) {
        mTitle.clear();
        mAuthor.clear();
        images.clear();
        ISBNs.clear();
        String author, title;
// Get the search string from the input field.
        String queryString = input.getText().toString();
        // Hide the keyboard when the button is pushed.
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // Check the status of the network connection.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If the network is active and the search field is not empty, start a FetchBook AsyncTask.
        if (networkInfo != null && networkInfo.isConnected() && queryString.length() != 0) {
            new FetchBook(mTitle, mAuthor, images, ISBNs, input, a).execute(queryString);
        }
        // Otherwise update the TextView to tell the user there is no connection or no search term.
        else {
            if (queryString.length() == 0) {
                Toast.makeText(AddABookActivity.this, "No result", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(AddABookActivity.this, "No network", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void back() {
        Intent data = new Intent();
        data.putExtra("list",bookList);
        setResult(RESULT_OK, data);
        finish();
    }

    class BookAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> rTitle;
        ArrayList<String> rAuthor;
        ArrayList<String> rImage;
        ArrayList<String> rID;

        public BookAdapter(Context c, ArrayList<String> title, ArrayList<String> author, ArrayList<String> image,ArrayList<String> IDs) {
            super(c, R.layout.book_row, R.id.title, title);
            this.context = c;
            this.rTitle = title;
            this.rAuthor = author;
            this.rImage = image;
            this.rID = IDs;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = li.inflate(R.layout.book_row, parent, false);
            ImageView image = row.findViewById(R.id.userPic);
            TextView title = row.findViewById(R.id.title);
            TextView author = row.findViewById(R.id.author);
            Button btn = row.findViewById(R.id.button);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBook(v);
                }
            });
            Book b = new Book(rAuthor.get(position),rTitle.get(position),rID.get(position),images.get(position));
            title.setText(b.title);
            btn.setTag(b);
            author.setText(b.author);
           /* image.setImageResource(R.drawable.ic_book);*/
            Glide.with(context).load(b.picURL).into(image);
          /*  Drawable drawable = LoadImage("http://books.google.com/books/content?id=NdvPAAAAMAAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api");
            image.setImageDrawable(drawable);*/
            return row;
        }

    }

    void addBook(View v){
        Book b = (Book)v.getTag();
        bookList.put(b.ISBN,b);
        ((Button)v).setText("book added");
        ((Button)v).setTextColor(Color.BLACK);
        v.setBackgroundColor(Color.parseColor("#E5E5E5"));
            if(isOne){
                Intent data = new Intent();
                data.putExtra("value",b.ISBN);
                setResult(RESULT_OK, data);
                finish();
            }

    }
}