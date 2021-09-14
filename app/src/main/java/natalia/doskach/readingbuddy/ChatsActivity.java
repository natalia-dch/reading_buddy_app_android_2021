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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatsActivity extends AppCompatActivity {
    DatabaseReference userChatsRef;
    ChatsAdapter a;
    ProgressBar pb;
    ListView list;
    ArrayList<User> users = new ArrayList<User>();
    ArrayList<String> comments = new ArrayList<String>();
    ArrayList<String> chatIDs = new ArrayList<String>();
    ArrayList<String> userIDs = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
//        pb = findViewById(R.id.pb);
//        pb.setVisibility(View.VISIBLE);
        list = findViewById(R.id.list);
        String senderID = Data.mAuth.getCurrentUser().getUid();
        userChatsRef = FirebaseDatabase.getInstance().getReference("UserChats/" + senderID);
        userChatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot value:snapshot.getChildren()){
                    chatIDs.add((String) value.getValue());
                    userIDs.add(value.getKey());
                }
                a = new ChatsAdapter(getApplicationContext(), chatIDs, userIDs);
//                pb.setVisibility(View.INVISIBLE);
                list.setAdapter(a);
                a.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    public void toProfile(View view) {
        startActivity(new Intent(this,ProfileActivity.class));
        finish();
    }

    public void toHome(View view) {
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }

    class ChatsAdapter extends ArrayAdapter {
        Context context;
        ArrayList<String> chatIDs;
        ArrayList<String> userIDs;

        public ChatsAdapter(Context c, ArrayList<String> chatIDs, ArrayList<String> userIDs) {
            super(c, R.layout.book_row);
            this.context = c;
            this.chatIDs = chatIDs;
            this.userIDs = userIDs;

        }

        @Override
        public int getCount() {
            return userIDs.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = li.inflate(R.layout.chat_row, parent, false);
            ImageView image = row.findViewById(R.id.userPic);
            TextView name = row.findViewById(R.id.name);
            TextView about = row.findViewById(R.id.about);
            String userID = userIDs.get(position);
            String chatID = chatIDs.get(position);
            FirebaseDatabase.getInstance().getReference("Chats").child(chatID).child("lastMessage").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    about.setText(snapshot.getValue(String.class));
                    FirebaseDatabase.getInstance().getReference("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            User u = snapshot.getValue(User.class);
                            name.setText(u.name);
                            try {
                                Glide.with(context).load(u.picURL).into(image);
                            } catch (Exception e) {
                            }
                            row.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                                    i.putExtra("user", u); //TODO
                                    i.putExtra("id",userIDs.get(position));
                                    startActivity(i);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });




            return row;
        }


    }



}