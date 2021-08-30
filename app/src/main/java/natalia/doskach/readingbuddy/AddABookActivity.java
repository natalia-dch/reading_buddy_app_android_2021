package natalia.doskach.readingbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class AddABookActivity extends AppCompatActivity implements canManageBooks {

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
//        if(getIntent().hasExtra("isOne"))
//            isOne = true;

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
        a = new BookAdapter(this, mTitle, mAuthor, images,ISBNs,"add book");
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

    public void back(View v){
        back();
    }

    public void back() {
        Intent data = new Intent();
        setResult(RESULT_CANCELED);
        finish();
    }



    public void manageBook(View v){
        Book b = (Book)v.getTag();
        bookList.put(b.ISBN,b);
        ((Button)v).setTextColor(Color.BLACK);
//        v.setBackgroundColor(Color.parseColor("#E5E5E5"));
        Intent data = new Intent();
        data.putExtra("list", bookList);
        setResult(RESULT_OK, data);
        finish();

    }
}