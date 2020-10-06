package alexfiweb.myPharmacy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddProduct extends AppCompatActivity {

    EditText name, desc, ref;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    static final int GALLERY_INTENT = 1;
    Button uploadImgButton;
    Uri uriImage;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        initFirebase();
        progressDialog = new ProgressDialog(this);
        name = findViewById(R.id.nameInput);
        desc = findViewById(R.id.descInput);
        ref = findViewById(R.id.refInput);
        uploadImgButton = (Button) findViewById(R.id.buttonUploadImg);
        uploadImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void saveProduct(View view) {
        String name = this.name.getText().toString();
        String desc = this.desc.getText().toString();
        String ref = this.ref.getText().toString();
        if (name.equals("")) {
            this.name.setError("Campo obligatorio");
        } else if (desc.equals("")) {
            this.desc.setError("Campo obligatorio");
        } else if (ref.equals("")) {
            this.ref.setError("Campo obligatorio");
        } else {
            progressDialog.setTitle("Cargando...");
            progressDialog.setMessage("Subiendo producto");
            progressDialog.setCancelable(false);
            progressDialog.show();
            final DataModel product = new DataModel();
            product.setId(UUID.randomUUID().toString());
            product.setName(name);
            product.setDescription(desc);
            product.setRef(ref);
            if (uriImage != null) {
                final StorageReference filePath = storageReference.child("images").child(uriImage.getLastPathSegment());
                filePath.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String downloadUrl = task.getResult().toString();
                                product.setImage(downloadUrl);
                                databaseReference.child("productos").child(product.getId()).setValue(product);
                                progressDialog.dismiss();
                                Intent mainActivityView = new Intent(AddProduct.this, MainActivity.class);
                                AddProduct.this.startActivity(mainActivityView);
                                Toast.makeText(AddProduct.this, "¡Producto guardado con éxito!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            uriImage = data.getData();
        }
    }
}
