package natalia.doskach.readingbuddy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddBooksActivity extends AppCompatActivity implements canManageBooks {

    Button add;
    ListView list;
    TextView title;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);
        list = findViewById(R.id.list);
        title = findViewById(R.id.title);
        add = findViewById(R.id.addBooksBtn);
        a = new BookAdapter(this, mTitle, mAuthor, images, ISBNs, "delete book");
        list.setAdapter(a);
        if (getIntent().hasExtra("books"))
            addToList((HashMap<String, Book>) getIntent().getSerializableExtra("books"));
        title.setText(getIntent().getStringExtra("title"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    HashMap<String, Book> newData = (HashMap<String, Book>) data.getSerializableExtra("list");
                    addToList(newData);

                    break;
            }
        }
    }

    private void addToList(HashMap<String, Book> newData) {
        for (HashMap.Entry<String, Book> entry : newData.entrySet()) {
            String key = entry.getKey();
            Book value = entry.getValue();
            bookList.put(key, value);
            mTitle.add(value.title);
            mAuthor.add(value.author);
            images.add(value.picURL);
            ISBNs.add(key);
        }
        if (!newData.isEmpty()) {
            a.notifyDataSetChanged();
            add.setText("Add More Books");
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void back(View v) {
        back();
    }

    public void back() {
        if (bookList.isEmpty()) {
            Toast.makeText(this, "Add some books!", Toast.LENGTH_LONG).show();
        } else {
            Intent data = new Intent();
            data.putExtra("list", bookList);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    public void manageBook(View v) {
        Book b = (Book) v.getTag();
        bookList.put(b.ISBN, b);
        ((Button) v).setTextColor(Color.BLACK);
        bookList.remove(b.ISBN);
        mTitle.remove(b.title);
        mAuthor.remove(b.author);
        images.remove(b.picURL);
        ISBNs.remove(b.ISBN);
        a.notifyDataSetChanged();

    }

    public void openAddABook(View view) {
        startActivityForResult(new Intent(this, AddABookActivity.class), 1);
    }


}