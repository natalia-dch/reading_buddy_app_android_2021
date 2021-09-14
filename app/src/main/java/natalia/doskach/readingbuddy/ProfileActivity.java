package natalia.doskach.readingbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    String id;
    ImageView profile;
    TextView longread;
    DatabaseReference ref;
    User u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        id = Data.mAuth.getCurrentUser().getUid();
        profile = findViewById(R.id.profile_pic);
        longread = findViewById(R.id.long_read);
        ref = FirebaseDatabase.getInstance().getReference("Users").child(id);
        profile.setImageResource(R.drawable.ic_group_5);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                u = snapshot.getValue(User.class);
                if(u.picURL!=null && !u.picURL.isEmpty()){
                        Glide.with(getApplicationContext()).load(u.picURL).placeholder(getDrawable(R.drawable.ic_group_5)).into(profile);}
               longread.setText(Data.createText(u));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
       });
    }

    public void toHome(View view) {
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }

    public void toChats(View view) {
        startActivity(new Intent(this,ChatsActivity.class));
        finish();
    }


    public void Logout(View view) {
         Data.mAuth.signOut();
        startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void changeData(View view) {
        Intent i= new Intent(this,ChangeProfileActivity.class);
        i.putExtra("user",u);
        startActivity(i);
    }
}