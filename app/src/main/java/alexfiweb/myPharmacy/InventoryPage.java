package alexfiweb.myPharmacy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class InventoryPage extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceSearch;
    List<Product> listProducts = new ArrayList<>();
    ListView listView;
    final String idUser = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Mi inventario");
        setContentView(R.layout.activity_inventory_page);
        listView = (ListView)findViewById(R.id.listViewInventory);
        databaseReferenceSearch = FirebaseDatabase.getInstance().getReference("productos");
        initFirebase();
        initListProduct();
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /* Obtenemos los productos del inventario de un usuario de la base de datos y se la pasamos a nuestra listView para que la pinte */
    private void initListProduct() {
        databaseReference.child("usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    final User usuario = objSnapshot.getValue(User.class);
                    if (usuario.getUserId().equals(idUser)) {
                        databaseReference.child("productos").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                listProducts.clear();
                                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                                    Product product = objSnapshot.getValue(Product.class);
                                    if (usuario.getInventoryProducts().contains(product.getId())) {
                                        listProducts.add(product);
                                    }
                                }
                                listView.setAdapter(new CustomAdapter(InventoryPage.this, listProducts, "inventory"));
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /* Añadimos en la barra superior de nuestra aplicacion, un menu que hemos creado con tres botones, los cuales haremos visible segun en que pantalla nos encontremos */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.getItem(2).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    /* Mediante un switch case le asignamos la funcionalidad a los botones del menu superior de nuestra aplicacion */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_add:
                Intent addProductView = new Intent(this, AddProduct.class);
                this.startActivity(addProductView);
                break;
            case R.id.icon_home:
                Intent homeView = new Intent(this, MainActivity.class);
                this.startActivity(homeView);
                break;
        }
        return true;
    }

    /* Funcion del buscador para buscar un producto en la base de datos, se realiza de tal manera con toString y toLowerCase para que la busqueda no sea caseSensitive */
    public void searchProduct(View view) {
        TextView s = (TextView) findViewById(R.id.search_inventory_field);
        if (s.getText().toString().toLowerCase().equals("")) {
            listView.setAdapter(new CustomAdapter(InventoryPage.this, listProducts, "inventory"));
        } else {
            List<Product> listProductsResult = new ArrayList<>();
            for (Product producto : listProducts) {
                if (producto.getName().startsWith(s.getText().toString().substring(0,1).toUpperCase() + s.getText().toString().substring(1).toLowerCase())) {
                    listProductsResult.add(producto);
                }
            }
            listView.setAdapter(new CustomAdapter(InventoryPage.this, listProductsResult, "inventory"));
        }
    }
}
