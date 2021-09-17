package natalia.doskach.readingbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class HomeActivity extends AppCompatActivity {
    EditText input;
    ListView list;
    Button aBtn;
    Button bBtn;
    Button resetBtn;
    ArrayList<UserInList> usersInList = new ArrayList<>();
    ArrayList<UserInList> filteredUsersInList = new ArrayList<>();
    //    ArrayList<User> filteredUsers = new ArrayList<User>();
//    ArrayList<User> users = new ArrayList<User>();
//    ArrayList<String> comments = new ArrayList<String>();
//    ArrayList<String> IDs = new ArrayList<String>();
//    ArrayList<Integer> ranks = new ArrayList<Integer>();
//    ArrayList<String> filteredComments = new ArrayList<String>();
    ArrayList<String> TBRCodes = new ArrayList<>();
    ArrayList<String> favCodes = new ArrayList<>();
    ArrayList<String> favGenres = new ArrayList<>();
    DatabaseReference ref;
    DatabaseReference ref2;
    User u;
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
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                users.clear();
//                IDs.clear();
//                comments.clear();
                usersInList.clear();
                TBRCodes.clear();
                favCodes.clear();
                String id = Data.mAuth.getCurrentUser().getUid();
                User user = snapshot.child(id).getValue(User.class);
                Iterator<String> iter1 = user.TBR.keySet().iterator();
                while (iter1.hasNext())
                    TBRCodes.add(iter1.next());
                Iterator<String> iter2 = user.favBooks.keySet().iterator();
                while (iter2.hasNext())
                    favCodes.add(iter2.next());
                for (String genre : Data.genresArray) {
                    if (user.genres.get(genre))
                        favGenres.add(genre);
                }
                Boolean[] genres = new Boolean[10];
                user.genres.values().toArray(genres);
                for (DataSnapshot d : snapshot.getChildren()) {
                    User u = (User) d.getValue(User.class);
                    if (u.email == user.email)
                        continue;
                    UserInList uInL = new UserInList(d.getKey());
//                    IDs.add(d.getKey());
                    int rank = 0;
                    String comment = "also wants to read ";
                    for (String book : TBRCodes) {
                        if (u.TBR.containsKey(book)) {
                            rank = rank | 4;
                            comment += "\"" + u.TBR.get(book).title + "\" by " + u.TBR.get(book).author + ", ";
                        }
                    }
                    if (rank == 0) {
                        comment = "also likes ";
                        for (String book : favCodes) {
                            if (u.favBooks.containsKey(book)) {
                                rank = rank | 2;
                                comment += "\"" + u.favBooks.get(book).title + "\" by " + u.favBooks.get(book).author + ", ";
                            }
                        }
                    }
                    if (rank == 0) {
                        for (String genre : favGenres) {
                            if (u.genres.get(genre)) {
                                rank = rank | 1;
                                comment += genre + ", ";
                            }

                        }
                    }
                    uInL.user = u;
                    uInL.rank = rank;
//                    users.add(u);
//                    ranks.add(rank);
                    if (rank != 0)
                        uInL.comment = comment.substring(0, comment.length() - 2);
//                        comments.add(comment.substring(0, comment.length()-2));
                    else
                        uInL.comment = Data.createSmallText(u);
//                        comments.add(Data.createSmallText(u));
                    usersInList.add(uInL);
                }
                Collections.sort(usersInList);
                a = new UserAdapter(getApplicationContext(), usersInList);
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

//    public void search(View view) {
//        String str = input.getText().toString();
//        if (Arrays.asList(Data.genresArray).contains(str))
//            searchByGenre(str);
//        else
//            searchByBook(str);
//    }
//
//    private void searchByBook(String str) {
//        ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                users.clear();
//                for (DataSnapshot d : snapshot.getChildren()) {
//                    User u = d.getValue(User.class);
//                    System.out.print("added values");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//
//    private void searchByGenre(String str) {
//    }

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
        filteredUsersInList.clear();
        for (UserInList user : usersInList
        ) {
            if (user.user.genres.get(value)) {
                filteredUsersInList.add(new UserInList(user.user, "likes " + value, 0, user.id));
            }
        }
        if (filteredUsersInList.isEmpty())
            Toast.makeText(this, "Nothing was found! Try another search", Toast.LENGTH_LONG).show();
        else {
            changeBtns(value);
            a = new UserAdapter(getApplicationContext(), filteredUsersInList);
            list.setAdapter(a);
        }
    }

    private void filterByThisBook(Book book) {
        String value = book.ISBN;
        String title = "\"" + book.title + "\" by " + book.author;
        filteredUsersInList.clear();
        for (UserInList user : usersInList
        ) {
            if (user.user.TBR.containsKey(value)) {
                filteredUsersInList.add(new UserInList(user.user, "wants to read " + title, 0, user.id));
            } else if (user.user.favBooks.containsKey(value)) {
                filteredUsersInList.add(new UserInList(user.user, "likes " + title, 0, user.id));
            }
        }
        if (filteredUsersInList.isEmpty())
            Toast.makeText(this, "Nothing was found! Try another search", Toast.LENGTH_LONG).show();
        else {
            changeBtns(title);
            a = new UserAdapter(getApplicationContext(), filteredUsersInList);
            list.setAdapter(a);
        }
    }

    public void filterReset(View view) {
        resetBtn.setVisibility(View.GONE);
        aBtn.setVisibility(View.VISIBLE);
        bBtn.setVisibility(View.VISIBLE);
        a = new UserAdapter(getApplicationContext(), usersInList);
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
        ArrayList<UserInList> users;

        public UserAdapter(Context c, ArrayList<UserInList> users) {
            super(c, R.layout.book_row);
            this.context = c;
            this.users = users;

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
                    i.putExtra("user", users.get(position).user);
                    i.putExtra("id", users.get(position).id);
                    startActivity(i);
                }
            });
            name.setText(users.get(position).user.name);
            about.setText(users.get(position).comment);
            try {
                Glide.with(context).load(users.get(position).user.picURL).into(image);
            } catch (Exception e) {
            }


            return row;
        }


    }


}



