package natalia.doskach.readingbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class UserActivity extends AppCompatActivity {
    User u;
    String id;
    ImageView profile;
    TextView longread;
    TextView name;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        u = (User) getIntent().getSerializableExtra("user");
        id = getIntent().getStringExtra("id");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        profile = findViewById(R.id.profile_pic);
        longread = findViewById(R.id.long_read);
        name = findViewById(R.id.name);
        Glide.with(UserActivity.this).load(u.picURL).into(profile); //TODO
        longread.setText(Data.createText(u));
        name.setText(u.name);
    }


    public void back(View view) {
        finish();
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("user", u);

        startActivity(intent);
    }
}