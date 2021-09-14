package natalia.doskach.readingbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    DatabaseReference userChatsRef;
    DatabaseReference chatRef;
    DatabaseReference userRef;
    User receiver;
    TextView userName;
    EditText inputET;
    ListView messagesRV;
    ArrayList<Message> mess;
    ArrayList<LocalDate> dates;
    String receiverID;//
    String senderID;
    MessageAdapter ma;
    ImageView userPic;
    int senderFlag;

    public void back(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        senderID = Data.mAuth.getCurrentUser().getUid();
        receiverID = getIntent().getStringExtra("id");
        senderFlag = senderID.compareTo(receiverID);
        receiver = (User) getIntent().getSerializableExtra("user");
        setContentView(R.layout.activity_chat);
        userName = findViewById(R.id.userName);
        userName.setText(receiver.name);
        userPic = findViewById(R.id.userPic);
        inputET = findViewById(R.id.input);
        messagesRV = findViewById(R.id.messages);
        mess = new ArrayList<>();
        dates = new ArrayList<LocalDate>();
        Glide.with(ChatActivity.this).load(receiver.picURL).into(userPic);
//        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                for (DataSnapshot u: snapshot.getChildren()){
//                    FirebaseDatabase.getInstance().getReference("UserChats").child(u.getKey()).setValue("");
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
        userChatsRef = FirebaseDatabase.getInstance().getReference("UserChats/" + senderID);
        userChatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Log.i("snapshot", snapshot.toString());
                if (snapshot.getValue().equals("") || !snapshot.hasChild(receiverID)) {
                    chatRef = FirebaseDatabase.getInstance().getReference("Chats").push();
                    chatRef.setValue("");
                    userChatsRef.child(receiverID).setValue(chatRef.getKey());
                    FirebaseDatabase.getInstance().getReference("UserChats/" + receiverID).child(senderID).setValue(chatRef.getKey());
                } else {
                    String chatID = snapshot.child(receiverID).getValue(String.class);
                    chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatID);
                }
                receive();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void receive() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!snapshot.getValue().equals("")) {
                    System.out.println("got messages");
                    mess.clear();
                    dates.clear();
                    for (DataSnapshot d : snapshot.getChildren()) {
                        if (!d.getKey().equals("lastMessage")) {
                            Message m = d.getValue(Message.class);
                            mess.add(m);
                            LocalDate date =
                                    Instant.ofEpochMilli(m.date).atZone(ZoneId.systemDefault()).toLocalDate();
                            dates.add(date);
                        }
                    }
                    MessageAdapter ma = new MessageAdapter(ChatActivity.this, mess);
                    messagesRV.setAdapter(ma);
                    messagesRV.setSelection(messagesRV.getAdapter().getCount() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void send(View view) {
        String input = inputET.getText().toString();
        long millis = System.currentTimeMillis();
        //send
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", senderFlag);
        hashMap.put("message", input);
        hashMap.put("date", millis);
        chatRef.push().setValue(hashMap);
        chatRef.child("lastMessage").setValue(input);
        inputET.setText("");
    }
//
//    public void receive() {
//        mess = new ArrayList<Message>();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                System.out.println("got mess");
//                mess.clear();
//                for (DataSnapshot d: snapshot.getChildren()){
//                    Message m = d.getValue(Message.class);
//                    if(m.receiver.equals(fuser.getUid()) && m.sender.equals(userID) ||
//                            m.receiver.equals(userID) && m.sender.equals(fuser.getUid()))
//                        mess.add(m);
//                }
//                ma = new MessageAdapter(ChatActivity.this,mess);
//                messagesRV.setAdapter(ma);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
//    }

    class MessageAdapter extends ArrayAdapter {
        Context context;
        ArrayList<Message> messages;

        public MessageAdapter(Context c, ArrayList<Message> messages) {
            super(c, R.layout.book_row);
            this.context = c;
            this.messages = messages;
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Message m = messages.get(position);
            View row;
            if (m.sender == senderFlag) { //senderMessage
                row = li.inflate(R.layout.message1, parent, false);
            } else {
                row = li.inflate(R.layout.message2, parent, false);
            }
            TextView text = row.findViewById(R.id.message);
            TextView date = row.findViewById(R.id.date);
            TextView timestamp = row.findViewById(R.id.timestamp);
            text.setText(m.message);
            if(position==0 || ChronoUnit.DAYS.between(dates.get(position),dates.get(position-1))>0)
            {
                String formatDate = "MMMM d";
                final SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
                String dateString = sdf.format(new Date(messages.get(position).date));
                date.setText(dateString);
            }
            else{
                date.setVisibility(View.GONE);
            }
            String formatTimestamp = "HH:mm";
            final SimpleDateFormat sdf = new SimpleDateFormat(formatTimestamp);
            String dateString = sdf.format(new Date(messages.get(position).date));
            timestamp.setText(dateString);
            return row;
        }
    }

//
//    class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
//        Context context;
//        ArrayList<Message> mMessages;
//
//        MessageAdapter(Context context, ArrayList<Message> mMessages) {
//            this.context = context;
//            this.mMessages = mMessages;
//
//
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            public TextView message;
//
//            public ViewHolder(View v) {
//                super(v);
//                message = v.findViewById(R.id.showMessage);
//            }
//
//            @NonNull
//            @NotNull
//            @Override
//            public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(context).inflate(R.layout.message, parent, false);
//                return new MessageAdapter.ViewHolder(view);
//            }
//
//            @Override
//            public void onBindViewHolder(@NonNull @NotNull MessageAdapter.ViewHolder holder, int position) {
//                final Message mes = mMessages.get(position);
//                this.message.setText(mes.message);
//            }
//
//            @Override
//            public int getItemCount() {
//                return mMessages.size();
//            }
//
//        }
//
//        }


}