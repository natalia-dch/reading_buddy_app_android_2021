package natalia.doskach.readingbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPasswordActivity extends AppCompatActivity {
    int counter = 0;
    Button button;
    EditText form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        button =  findViewById(R.id.forgot_button);
        form =  findViewById(R.id.form);
    }

    public void back(View v){
        back();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void sendCodeToEmail(View view) {
        String email = form.getText().toString();
        if(email.isEmpty())
            Toast.makeText(ForgotPasswordActivity.this, "wrong value", Toast.LENGTH_LONG).show();
        Data.mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        if (isNewUser) {
                            Toast.makeText(ForgotPasswordActivity.this, "such email doesn't exists", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "sending email", Toast.LENGTH_LONG).show();
                            button.setEnabled(false);
                            Data.mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPasswordActivity.this, "Check your email for a letter to reset password", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(ForgotPasswordActivity.this, "something went wrong. try again", Toast.LENGTH_LONG).show();
                                        button.setEnabled(true);
                                    }
                                }
                            });
                        }
                    }
                });
    }


    public void back() {
        finish();
    }
}