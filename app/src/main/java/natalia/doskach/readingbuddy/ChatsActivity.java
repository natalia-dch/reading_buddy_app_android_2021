package natalia.doskach.readingbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
    }
    public void toProfile(View view) {
        startActivity(new Intent(this,ProfileActivity.class));
        finish();
    }

    public void toHome(View view) {
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }

    public void toChat(View view) {
        startActivity(new Intent(this,ChatActivity.class));
    }
}