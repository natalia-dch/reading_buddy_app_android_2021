package natalia.doskach.readingbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class HomeActivity extends AppCompatActivity {
    EditText input;
    ListView list;
    Button aBtn;
    Button bBtn;
    Button resetBtn;
    ArrayList<User> filteredUsers = new ArrayList<User>();
    ArrayList<User> users = new ArrayList<User>();
    ArrayList<String> comments = new ArrayList<String>();
    ArrayList<String> filteredComments = new ArrayList<String>();
    DatabaseReference ref;
    UserAdapter a;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        aBtn = findViewById(R.id.input);
        bBtn = findViewById(R.id.search);
        resetBtn = findViewById(R.id.filterReset);
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        list = findViewById(R.id.list);
        ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    User u = (User) d.getValue(User.class);
                    users.add(u);
                    comments.add(Data.createSmallText(u));
                }
                a = new UserAdapter(getApplicationContext(), users, comments);
                pb.setVisibility(View.INVISIBLE);
                list.setAdapter(a);
                a.notifyDataSetChanged();
                System.out.print("added values");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
    }

    public void toProfile(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void toChats(View view) {
        startActivity(new Intent(this, ChatsActivity.class));
    }

    public void search(View view) {
        String str = input.getText().toString();
        if (Arrays.asList(Data.genresArray).contains(str))
            searchByGenre(str);
        else
            searchByBook(str);
    }

    private void searchByBook(String str) {
        ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    User u = d.getValue(User.class);
                    System.out.print("added values");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


    private void searchByGenre(String str) {
    }

    public void filterByBook(View view) {
        Intent i = new Intent(this, AddABookActivity.class);
        i.putExtra("isOne", true);
        startActivityForResult(i, 1);
    }

    public void filterByGenre(View view) {
        Intent i = new Intent(this, AddGenresActivity.class);
        i.putExtra("isOne", true);
        startActivityForResult(i, 2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case 1:
                    Book book = null;
                    String code = "", name = "";
                    HashMap<String, Book> newData = (HashMap<String, Book>) data.getSerializableExtra("list");
                    if (!newData.isEmpty()) {
                        for (HashMap.Entry<String, Book> entry : newData.entrySet()) {
                            book = entry.getValue();
                            name = book.title;
                        }
                        filterByThisBook(book);
                    }
                    break;
                case 2:
                    String genre = (String) data.getSerializableExtra("value");
                    filterByThisGenre(genre);
                    break;
            }
    }

    private void filterByThisGenre(String value) {
        filteredUsers.clear();
        filteredComments.clear();
        for (User user : users
        ) {
            if (user.genres.get(value)) {
                filteredUsers.add(user);
                filteredComments.add("likes " + value);
            }
        }
        if(filteredUsers.isEmpty())
            Toast.makeText(this, "Nothing was found! Try another search", Toast.LENGTH_LONG).show();
        else{
        changeBtns(value);
        a = new UserAdapter(getApplicationContext(), filteredUsers, filteredComments);
        list.setAdapter(a);}
    }

    private void filterByThisBook(Book book) {
        String value = book.ISBN;
        String title = "\""+book.title +"\" by "+book.author;
        filteredUsers.clear();
        filteredComments.clear();
        for (User user : users
        ) {
            if (user.TBR.containsKey(value))
            {
                filteredUsers.add(user);
                filteredComments.add("wants to read "+title);
            }
            else if(user.favBooks.containsKey(value)){
                filteredUsers.add(user);
                filteredComments.add("likes "+title);
            }
        }
        if(filteredUsers.isEmpty())
            Toast.makeText(this, "Nothing was found! Try another search", Toast.LENGTH_LONG).show();
        else{
           changeBtns(value);
        a = new UserAdapter(getApplicationContext(), filteredUsers, filteredComments);
        list.setAdapter(a);}
    }

    public void filterReset(View view) {
        resetBtn.setVisibility(View.GONE);
        aBtn.setVisibility(View.VISIBLE);
        bBtn.setVisibility(View.VISIBLE);
        a = new UserAdapter(getApplicationContext(), users, comments);
        list.setAdapter(a);
    }

    private void changeBtns(String text) {
        aBtn.setVisibility(View.GONE);
        bBtn.setVisibility(View.GONE);
        resetBtn.setVisibility(View.VISIBLE);
        resetBtn.setText(text + "\u2717");
    }

    class UserAdapter extends ArrayAdapter {
        Context context;
        ArrayList<User> users;
        ArrayList<String> comments;

        public UserAdapter(Context c, ArrayList<User> users, ArrayList<String> comments) {
            super(c, R.layout.book_row);
            this.context = c;
            this.users = users;
            this.comments = comments;

        }

        @Override
        public int getCount() {
            return users.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = li.inflate(R.layout.user_row, parent, false);
            ImageView image = row.findViewById(R.id.userPic);
            TextView name = row.findViewById(R.id.name);
            TextView about = row.findViewById(R.id.about);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(HomeActivity.this, UserActivity.class);
                    i.putExtra("user", users.get(position));
                    startActivity(i);
                }
            });
            name.setText(users.get(position).name);
            about.setText(comments.get(position));
            try {
                Glide.with(context).load(users.get(position).picURL).into(image);
            } catch (Exception e) {
            }


            return row;
        }


    }


}



