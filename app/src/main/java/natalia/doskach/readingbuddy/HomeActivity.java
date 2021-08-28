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
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;


public class HomeActivity extends AppCompatActivity {
    EditText input;
    ListView list;
    ArrayList<User> users = new ArrayList<User>();
    ArrayList<String> comments = new ArrayList<String>();
    DatabaseReference ref;
    UserAdapter a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
                System.out.print("added values");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        a = new UserAdapter(this, users,comments);
        list.setAdapter(a);
        a.notifyDataSetChanged();
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

        switch (requestCode) {
            case 1:
                filterByThisBook((String) data.getSerializableExtra("value"));
                break;
            case 2:
                filterByThisGenre((String) data.getSerializableExtra("value"));
                break;
        }
    }

    private void filterByThisGenre(String value) {
        ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    User u = d.getValue(User.class);
                    for (int i = 0; i < 10; i++) {
                        if (u.genres.get(value))
                            users.add(u);
                    }
                }
                System.out.print("added values");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void filterByThisBook(String value) {
        ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    User u = d.getValue(User.class);
                    if (u.TBR.containsKey(value) || u.favBooks.containsKey(value))
                        users.add(u);
                }
                System.out.print("added values");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
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
            Button btn = row.findViewById(R.id.button);
            btn.setOnClickListener(new View.OnClickListener() {
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



