package natalia.doskach.readingbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class ChangeProfileActivity extends AppCompatActivity {
    User u;
    EditText name;
    EditText location;
    EditText about;
    ImageButton back;
    ProgressBar pb;
    boolean isPhotoDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        u = (User) getIntent().getSerializableExtra("user");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);
        name = findViewById(R.id.username1);
        location = findViewById(R.id.city);
        about = findViewById(R.id.about);
        back = findViewById(R.id.navb1);
        pb = findViewById(R.id.pb);
        name.setText(u.name);
        location.setText(u.address == null || u.address.equals("?") ? "" : u.address);
        about.setText(u.about == null || u.address.equals("?") ? "" : u.about);

    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void back(View v) {
        back();
    }

    public void back() {
        saveData(back);
    }

    public void addFavBooks(View view) {
        startActivityForResult(new Intent(this, AddBooksActivity.class).putExtra("title", "Favorite Books").putExtra("books", u.favBooks), 3);
    }

    public void addReadingList(View view) {
        startActivityForResult(new Intent(this, AddBooksActivity.class).putExtra("title", "Books To Read").putExtra("books", u.TBR), 1);
    }

    public void addGenres(View view) {
        startActivityForResult(new Intent(this, AddGenresActivity.class).putExtra("genres", u.genres), 2);
    }


    public void changePicture(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        isPhotoDownloading = true;
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                break;
            case 1:
                u.TBR = (HashMap<String, Book>) data.getSerializableExtra("list");
                Toast.makeText(this, "TBR changed", Toast.LENGTH_LONG).show();

                break;
            case 2:
                u.genres = (HashMap<String, Boolean>) data.getSerializableExtra("list");
                Toast.makeText(this, "favorite genres changed", Toast.LENGTH_LONG).show();
                break;
            case 3:
                u.favBooks = (HashMap<String, Book>) data.getSerializableExtra("list");
                Toast.makeText(this, "favorite books changed", Toast.LENGTH_LONG).show();
                break;
        }
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        pb.setVisibility(View.VISIBLE);
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        Uri filePath = data.getData();
                        String name = "images/" + u.email;
                        StorageTask<UploadTask.TaskSnapshot> profileRef = storageRef.child(name).putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                Task<Uri> profileRef = storageRef.child(name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        u.picURL = uri.toString();
                                        pb.setVisibility(View.INVISIBLE);
                                        isPhotoDownloading = false;
                                        Toast.makeText(getApplicationContext(), "profile picture changed", Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        });


                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(ChangeProfileActivity.this.getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(ChangeProfileActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                isPhotoDownloading = false;
            }
        }
    }

    public void saveData(View view) {

        if (isPhotoDownloading) {
            Toast.makeText(getApplicationContext(), "Wait! Profile picture is uploading", Toast.LENGTH_LONG).show();
            return;
        }
        String nameStr = name.getText().toString();
        String addressStr = location.getText().toString();
        String aboutStr = about.getText().toString();
        if (!nameStr.isEmpty())
            u.name = nameStr;
        if (!addressStr.isEmpty())
            u.address = addressStr;
        if (!aboutStr.isEmpty())
            u.about = aboutStr;
        String id = Data.mAuth.getCurrentUser().getUid();
        back.setClickable(false);
        FirebaseDatabase.getInstance().getReference("Users").child(id).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                finish();
            }
        });
    }
}