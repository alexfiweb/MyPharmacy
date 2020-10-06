package alexfiweb.myPharmacy;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ProductView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

        ImageView imageView = (ImageView) findViewById(R.id.imageProductView);
        TextView nameProduct = (TextView) findViewById(R.id.nameProductView);
        TextView descProduct = (TextView) findViewById(R.id.descProductView);
        descProduct.setMovementMethod(new ScrollingMovementMethod());
        TextView refProduct = (TextView) findViewById(R.id.refProductView);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b!=null) {
            Uri uri = Uri.parse(b.getString("IMG"));
            Glide.with(this)
                    .load(uri)
                    .into(imageView);
            nameProduct.setText(b.getString("NAME"));
            descProduct.setText(b.getString("DESC"));
            refProduct.setText(b.getString("REF"));
        }
    }
}
