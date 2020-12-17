package alexfiweb.myPharmacy;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductView extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String idProduct;
    final String idUser = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        initFirebase();
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
            idProduct = b.getString("ID");
            setTitle(b.getString("NAME"));
            nameProduct.setText(b.getString("NAME"));
            descProduct.setText(b.getString("DESC"));
            refProduct.setText(b.getString("REF"));
        }
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /* Funcion que ejecuta el boton añadir producto al inventario, que lo que hace es guardar en producto en la base de datos del usuario */
    public void addProductUserInventory(View view) {
        databaseReference.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    User usuario = objSnapshot.getValue(User.class);
                    if (usuario.getUserId().equals(idUser) && !usuario.getInventoryProducts().contains(idProduct)) {
                        usuario.getInventoryProducts().add(idProduct);
                        databaseReference.child("usuarios").child(idUser).setValue(usuario);
                        Toast.makeText(ProductView.this, "Producto añadido a su inventario", Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        Toast.makeText(ProductView.this, "Este producto ya está en su inventario", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
}
