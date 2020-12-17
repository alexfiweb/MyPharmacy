package alexfiweb.myPharmacy;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FullScreenImageView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Inicio");
        setContentView(R.layout.activity_image_view);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewFull);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        // Se comprueba si se le ha pasado una imagen a la vista, y se pinta en el imageView
        if(b!=null) {
            Uri uri = Uri.parse(b.getString("IMG"));
            Glide.with(this)
                    .load(uri)
                    .into(imageView);
        }
    }
}
