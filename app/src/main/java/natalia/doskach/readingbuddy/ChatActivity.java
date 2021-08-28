package natalia.doskach.readingbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    FirebaseUser fuser;
    DatabaseReference userRef;

    TextView userName;
    EditText inputET;
    RecyclerView messagesRV;
    ArrayList<Message> mess;
    String userID;// = "HwGRYn6pmVYKsfBKChDz8SE1HmD3";
    MessageAdapter ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        userID = getIntent().getStringExtra("id");
        setContentView(R.layout.activity_chat);
        fuser = Data.mAuth.getCurrentUser();
        userName = findViewById(R.id.userName);
        inputET = findViewById(R.id.input);
        messagesRV = findViewById(R.id.messages);
        messagesRV.setHasFixedSize(true);

        LinearLayoutManager man = new LinearLayoutManager(getApplicationContext());
        man.setStackFromEnd(true);
        messagesRV.setLayoutManager(man);

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userName.setText(user.name);

                receive();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void send(View view) {
        String input = inputET.getText().toString();
        //send
        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("sender",fuser.getUid());
        hashMap.put("receiver",userID);
        hashMap.put("message",input);
        FirebaseDatabase.getInstance().getReference("Chats").push().setValue(hashMap);

        inputET.setText("");
    }

    public void receive() {
        mess = new ArrayList<Message>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                System.out.println("got mess");
                mess.clear();
                for (DataSnapshot d: snapshot.getChildren()){
                    Message m = d.getValue(Message.class);
                    if(m.receiver.equals(fuser.getUid()) && m.sender.equals(userID) ||
                            m.receiver.equals(userID) && m.sender.equals(fuser.getUid()))
                        mess.add(m);
                }
                ma = new MessageAdapter(ChatActivity.this,mess);
                messagesRV.setAdapter(ma);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }



    class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
        Context context;
        ArrayList<Message> mMessages;

        MessageAdapter(Context context,ArrayList<Message> mMessages){
            this.context = context;
            this.mMessages = mMessages;


        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView message;
            public ViewHolder(View v) {
                super(v);
                message = v.findViewById(R.id.showMessage);
            }
        }

        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.message,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MessageAdapter.ViewHolder holder, int position) {
final Message mes = mMessages.get(position);
holder.message.setText(mes.message);
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }

    }
}