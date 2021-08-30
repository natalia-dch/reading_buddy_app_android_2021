package natalia.doskach.readingbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

class BookAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> rTitle;
    ArrayList<String> rAuthor;
    ArrayList<String> rImage;
    ArrayList<String> rID;
    String btnText;

    public BookAdapter(Context c, ArrayList<String> title, ArrayList<String> author, ArrayList<String> image,ArrayList<String> IDs,String btnText) {
        super(c, R.layout.book_row, R.id.title, title);
        this.context = c;
        this.rTitle = title;
        this.rAuthor = author;
        this.rImage = image;
        this.rID = IDs;
        this.btnText = btnText;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = li.inflate(R.layout.book_row, parent, false);
        ImageView image = row.findViewById(R.id.userPic);
        TextView title = row.findViewById(R.id.title);
        TextView author = row.findViewById(R.id.author);
        Button btn = row.findViewById(R.id.button);
        btn.setText(btnText);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((canManageBooks)context).manageBook(v);
            }
        });
        Book b = new Book(rAuthor.get(position),rTitle.get(position),rID.get(position),rImage.get(position));
        title.setText(b.title);
        btn.setTag(b);
        author.setText(b.author);
        /* image.setImageResource(R.drawable.ic_book);*/
        Glide.with(context).load(b.picURL).into(image);
          /*  Drawable drawable = LoadImage("http://books.google.com/books/content?id=NdvPAAAAMAAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api");
            image.setImageDrawable(drawable);*/
        return row;
    }

}

