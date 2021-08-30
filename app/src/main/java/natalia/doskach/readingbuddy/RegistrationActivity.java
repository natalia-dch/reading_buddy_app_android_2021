package natalia.doskach.readingbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity<Firebase> extends AppCompatActivity {
    EditText emailET, passwordET, repeat_passwordET, usernameET;
    Button favBooksBtn,genresBtn, newBooksBtn;
    User user;
    boolean addedTBR = false, addedBooks = false, addedGenres = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        user = new User();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        repeat_passwordET = findViewById(R.id.repeat_password);
        usernameET = findViewById(R.id.username);
        favBooksBtn = findViewById(R.id.favBooksButton);
        genresBtn = findViewById(R.id.genresButton);
        newBooksBtn = findViewById(R.id.newBooksButton);
        emailET.setText(email);

    }

    public void back(View v){
        back();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void Register(View view) {
        System.out.println("Yet");
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String repeat_password = repeat_passwordET.getText().toString().trim();
        String username = usernameET.getText().toString().trim();
        if (!canRegister(email, username, password, repeat_password)) {
            return;
        }
        if (!addedBooks) {
            return;
        }
        if (!addedGenres) {
            Toast.makeText(RegistrationActivity.this, "Add favorite genres!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!addedTBR) {
            Toast.makeText(RegistrationActivity.this, "Add books you want to read!", Toast.LENGTH_LONG).show();
            return;
        }
        user.email = email;
        user.name = username;
        user.address = "?";
        user.about = "?";
        user.picURL = "";
        Data.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful())
                    Toast.makeText(RegistrationActivity.this, ((FirebaseAuthException) task.getException()).getErrorCode(), Toast.LENGTH_LONG).show();

                if (task.isSuccessful()) {

                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegistrationActivity.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                                Registered();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Wasn't able to register", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean canRegister(String email, String username, String password, String repeat_password) {
        boolean flag = true;
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || repeat_password.isEmpty()) {
            Toast.makeText(RegistrationActivity.this, "Values cannot be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
        ;
        if (!password.equals(repeat_password)) {
            Toast.makeText(RegistrationActivity.this, "Passwords must match!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
            Toast.makeText(RegistrationActivity.this, "Wrong email", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void Registered() {
        startActivity(new Intent(this, HomeActivity.class));
    }

    public void addReadingList(View view) {
        startActivityForResult(new Intent(this, AddBooksActivity.class).putExtra("title","Books To Read"), 1);
    }

    public void addGenres(View view) {
        startActivityForResult(new Intent(this, AddGenresActivity.class), 2);
    }

    public void addFavBooks(View view) {
        startActivityForResult(new Intent(this, AddBooksActivity.class).putExtra("title","Favorite books"), 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                user.TBR = (HashMap<String, Book>) data.getSerializableExtra("list");
                if (!user.TBR.isEmpty())
                {addedTBR = true;
                 newBooksBtn.setText("added books you want to read \u2713");
                newBooksBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(RegistrationActivity.this, "You've already added TBR books! Change them after registering", Toast.LENGTH_LONG).show();
                    }
                });}
                break;
            case 2:
                user.genres = (HashMap<String, Boolean>) data.getSerializableExtra("list");
                if (!user.genres.isEmpty())
                {addedGenres = true;
                 genresBtn.setText("added favorite genres \u2713");
                genresBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(RegistrationActivity.this, "You've already added genres! Change them after registering", Toast.LENGTH_LONG).show();
                    }
                });}
                break;
            case 3:
                user.favBooks = (HashMap<String, Book>) data.getSerializableExtra("list");
                if (!user.favBooks.isEmpty())
                {addedBooks = true;
                 favBooksBtn.setText("added favorite books \u2713");
                 favBooksBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(RegistrationActivity.this, "You've already added your favorite books! Change them after registering", Toast.LENGTH_LONG).show();
                        }
                    });}
                break;
        }
    }


    public void back() {
        finish();
    }
}