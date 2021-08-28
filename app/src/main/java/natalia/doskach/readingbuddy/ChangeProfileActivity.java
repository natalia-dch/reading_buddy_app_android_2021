package natalia.doskach.readingbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

public class ChangeProfileActivity extends AppCompatActivity {
    User u;
    EditText name;
    EditText location;
    EditText about;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        u = (User)getIntent().getSerializableExtra("user");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);
        name = findViewById(R.id.username1);
        location = findViewById(R.id.city);
        about = findViewById(R.id.about);
        back = findViewById(R.id.navb1);
        name.setText(u.name);
        location.setText(u.address==null?"":u.address);
        about.setText(u.about==null?"":u.about);

    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void back() {
        finish();
    }

    public void addFavBooks(View view) {
        startActivityForResult(new Intent(this, AddABookActivity.class),3);
    }

    public void addReadingList(View view) {
        startActivityForResult(new Intent(this, AddABookActivity.class),1);
    }

    public void addGenres(View view) {
        startActivityForResult(new Intent(this,AddGenresActivity.class),2);
    }


    public void changePicture(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),0);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 0: break;
            case 1: u.TBR.putAll((HashMap<String, Book>) data.getSerializableExtra("list")); break;
            case 2: u.genres = (HashMap<String, Boolean>) data.getSerializableExtra("list");  break;
            case 3:  u.favBooks.putAll((HashMap<String, Book>) data.getSerializableExtra("list")); break;
        }
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(ChangeProfileActivity.this.getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(ChangeProfileActivity.this,"Cancelled",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void saveData(View view) {

        u.name = name.getText().toString();
        u.address = location.getText().toString();
        u.about = about.getText().toString();
        String id = Data.mAuth.getCurrentUser().getUid();
        back.setClickable(false);
        FirebaseDatabase.getInstance().getReference("Users").child(id).removeValue();
        FirebaseDatabase.getInstance().getReference("Users").child(id).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                finish();
            }
        });
    }
}