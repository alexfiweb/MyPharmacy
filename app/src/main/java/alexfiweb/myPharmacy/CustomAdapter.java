package alexfiweb.myPharmacy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Context mContext;
    List<Product> listProducts;
    String calledFrom;
    final String idUser = "123456";
    User user;
    public CustomAdapter(Context context, List<Product> listProducts, String calledFrom) {
        this.mContext = context;
        this.calledFrom = calledFrom;
        this.listProducts = listProducts;
        initFirebase();
        inflater = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(mContext);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    final User usuario = objSnapshot.getValue(User.class);
                    if (usuario.getUserId().equals(idUser)) {
                        user = usuario;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public int getCount() {
        return this.listProducts.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int idResource = 0;
        int idLayout = 0;
        int idName = 0;
        int idDesc = 0;
        int idImage = 0;
        if (calledFrom.equals("home")) {
            idResource = R.layout.productlayout;
            idName = R.id.name;
            idDesc = R.id.description;
            idImage = R.id.imageView;
            idLayout = R.id.productLayout;
        } else if (calledFrom.equals("inventory")) {
            idResource = R.layout.productinventorylayout;
            idName = R.id.nameInventory;
            idDesc = R.id.descriptionInventory;
            idImage = R.id.imageInventoryView;
            idLayout = R.id.productInventoryLayout;
        }
        final View view = inflater.inflate(idResource, null);
        TextView name = (TextView) view.findViewById(idName);
        TextView description = (TextView) view.findViewById(idDesc);
        ImageView imageView = (ImageView) view.findViewById(idImage);
        @SuppressLint("WrongViewCast")
        RelativeLayout productLayout = (RelativeLayout) view.findViewById(idLayout);
        final Product product;
        product = listProducts.get(position);
        Context context = imageView.getContext();
        Uri uri = Uri.parse(product.getImage());
        Glide.with(mContext)
                .load(uri)
                .into(imageView);
        name.setText(product.getName().substring(0,1).toUpperCase() + product.getName().substring(1));
        description.setText(product.getDescription());

        ImageButton buttonDelete = (ImageButton) view.findViewById(R.id.deleteProduct);
        if (buttonDelete != null) {
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user != null) {
                        List<String> productsId = new ArrayList<>();
                        for (String productId : user.getInventoryProducts()) {
                            if (!product.getId().equals(productId)) {
                                productsId.add(productId);
                            }
                        }
                        User userUpdated = new User();
                        userUpdated.setInventoryProducts(productsId);
                        userUpdated.setUserId(idUser);
                        databaseReference.child("usuarios").child(idUser).setValue(userUpdated);
                        Toast.makeText(mContext, "Producto eliminado de su inventario", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fullScreenImageView = new Intent(mContext, FullScreenImageView.class);
                fullScreenImageView.putExtra("IMG", product.getImage());
                mContext.startActivity(fullScreenImageView);
            }
        });

        productLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent productView = new Intent(mContext, ProductView.class);
                productView.putExtra("IMG", product.getImage());
                productView.putExtra("NAME", product.getName().substring(0,1).toUpperCase() + product.getName().substring(1));
                productView.putExtra("DESC", product.getDescription());
                productView.putExtra("REF", product.getRef());
                productView.putExtra("ID", product.getId());
                mContext.startActivity(productView);
            }
        });

        return view;
    }
}
