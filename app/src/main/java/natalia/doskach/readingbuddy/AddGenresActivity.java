package natalia.doskach.readingbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AddGenresActivity extends AppCompatActivity {
    HashMap<String,Boolean> genresList = new HashMap<String,Boolean>();
    boolean[] myGenres;
    int counter = 0;
    int globalCounter;
    boolean isOne = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getIntent().hasExtra("isOne"))
            isOne = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_genres);
        myGenres = new boolean[10];
        for (int i = 0; i < 10; i++) {
            genresList.put(Data.genresArray[i],false);
        }
        LinearLayout layout = findViewById(R.id.list1);
        int count = layout.getChildCount();
        Button b = null;
        for(int i=0; i<count; i++) {
            b = (Button)layout.getChildAt(i);
            b.setText(Data.genresArray[i]);
            b.setBackgroundColor(Color.parseColor("#E5E5E5"));
            b.setTextColor(Color.BLACK);
            //do something with your child element
        }

    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void back() {
        if(counter==0){
            Toast.makeText(AddGenresActivity.this,"Add at least 1 genre!",Toast.LENGTH_LONG).show();
            return;
        }
        Intent data = new Intent();
        data.putExtra("list",genresList);
        setResult(RESULT_OK, data);
        finish();


    }

    public void addGenre(View view) {
        String genre = ((Button)view).getText().toString();
        genresList.replace(genre,genresList.get(genre) ^ true);
        if(genresList.get(genre)){
            ((Button)view).setTextColor(Color.WHITE);
            view.setBackgroundColor(Color.parseColor("#F8B101"));
            counter++;
            if(isOne){
                Intent data = new Intent();
                data.putExtra("value",genre);
                setResult(RESULT_OK, data);
                finish();
            }
        }
        else{
            ((Button)view).setTextColor(Color.BLACK);
            view.setBackgroundColor(Color.parseColor("#E5E5E5"));
            counter--;
        }
    }
}