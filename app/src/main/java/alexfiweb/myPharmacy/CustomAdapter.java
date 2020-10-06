package alexfiweb.myPharmacy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;

    Context mContext;
    List<DataModel> listProducts;

    public CustomAdapter(Context context, List<DataModel> listProducts) {
        this.mContext=context;
        this.listProducts = listProducts;
        inflater = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.customlayout, null);

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView description = (TextView) view.findViewById(R.id.description);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        @SuppressLint("WrongViewCast")
        RelativeLayout productLayout = (RelativeLayout) view.findViewById(R.id.productLayout);
        final DataModel product;
        product = listProducts.get(position);
        Context context = imageView.getContext();
        Uri uri = Uri.parse(product.getImage());
        Glide.with(mContext)
                .load(uri)
                .into(imageView);
        name.setText(product.getName());
        description.setText(product.getDescription());

        imageView.setTag(position);
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
                productView.putExtra("NAME", product.getName());
                productView.putExtra("DESC", product.getDescription());
                productView.putExtra("REF", product.getRef());
                mContext.startActivity(productView);
            }
        });

        return view;
    }
}
