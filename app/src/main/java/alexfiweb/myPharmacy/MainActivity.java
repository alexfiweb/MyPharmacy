package alexfiweb.myPharmacy;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceSearch;
    List<Product> listProducts = new ArrayList<>();
    ListView listView;
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Inicio");
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView);
        databaseReferenceSearch = FirebaseDatabase.getInstance().getReference("productos");
        initFirebase();
        initListProduct();
    }

    private void initListProduct() {
        databaseReference.child("productos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listProducts.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    Product product = objSnapshot.getValue(Product.class);
                    listProducts.add(product);
                }
                listView.setAdapter(new CustomAdapter(MainActivity.this, listProducts, "home"));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.getItem(1).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_add:
                Intent addProductView = new Intent(this, AddProduct.class);
                this.startActivity(addProductView);
                break;
            case R.id.icon_account:
                Intent inventoryView = new Intent(this, InventoryPage.class);
                this.startActivity(inventoryView);
                break;
        }
        return true;
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void scanProduct(View view) {
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermissions();
            }
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(MainActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permission[], int grantResult[]) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if(grantResult.length > 0) {
                    boolean cameraAccepted = grantResult[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted) {
                        Toast.makeText(MainActivity.this, "Permission granted!", Toast.LENGTH_LONG).show();
                        scannerView.setResultHandler(this);
                        scannerView.startCamera();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_LONG).show();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(shouldShowRequestPermissionRationale(CAMERA)) {
                                displayAlertMessage("You need to allow access for both permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    public void onBackPressed() {
        if (scannerView != null) {
            scannerView.stopCamera();
            scannerView = null;
            setContentView(R.layout.activity_main);
            listView = (ListView)findViewById(R.id.listView);
            listView.setAdapter(new CustomAdapter(this, listProducts, "home"));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scannerView != null) {
            scannerView.stopCamera();
            scannerView = null;
        }
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(new CustomAdapter(this, listProducts, "home"));
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("Ok", listener)
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();
        String message = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resultado");
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(new CustomAdapter(this, listProducts, "home"));
        boolean productExist = false;
        String imageUrl = "";
        for (int i = 0; i < listProducts.size(); i++) {
            if(scanResult.toLowerCase().equals(listProducts.get(i).getRef().toLowerCase())) {
                imageUrl = listProducts.get(i).getImage();
                productExist = true;
                break;
            }
        }
        if(productExist) {
            Intent fullScreenImageView = new Intent(this, FullScreenImageView.class);
            fullScreenImageView.putExtra("IMG", imageUrl);
            this.startActivity(fullScreenImageView);
        } else {
            message = "El producto con referencia "+scanResult+" no existe";
            builder.setMessage(message);
            builder.setNeutralButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scannerView.resumeCameraPreview(MainActivity.this);
                }
            });
            builder.setPositiveButton("Crear", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent addProductView = new Intent(MainActivity.this, AddProduct.class);
                    MainActivity.this.startActivity(addProductView);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void searchProduct(View view) {
        TextView s = (TextView) findViewById(R.id.search_field);
        databaseReference.child("productos").orderByChild("name").startAt(s.getText().toString().toLowerCase()).endAt(s.getText().toString().toLowerCase()+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listProducts.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    Product product = objSnapshot.getValue(Product.class);
                    listProducts.add(product);
                }
                listView.setAdapter(new CustomAdapter(MainActivity.this, listProducts, "home"));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
