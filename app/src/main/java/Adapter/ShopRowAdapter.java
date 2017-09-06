package Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import mapp.teamkcl.shoplet.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Model.Shop;
import Util.WebUtil;

/**
 * Created by Leyond on 30/1/2017.
 */

//Custom adapter for listView
public class ShopRowAdapter extends ArrayAdapter<Shop> {
    private Context context;
    private ArrayList<Shop> shops;

    private static class ViewHolder {
        ImageView imageView;
        TextView nameTV;
        TextView descriptionTV;
    }

    public ShopRowAdapter(Context context, ArrayList<Shop> shops) {
        super(context, R.layout.shoprow, shops);
        this.context = context;
        this.shops = shops;
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Shop shop = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            //Load xml
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.shoprow,parent,false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.shoprow_image);
            viewHolder.nameTV = (TextView) convertView.findViewById(R.id.shoprow_name);
            viewHolder.descriptionTV = (TextView) convertView.findViewById(R.id.shoprow_description);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;

        //Constants to be used in Glide listener
        final ImageView imageView = viewHolder.imageView;
        final TextView nameTV = viewHolder.nameTV;
        final TextView descriptionTV = viewHolder.descriptionTV;

        lastPosition = position;

        String name = shop.getName();
        String description = shop.getDescription();

        //Crafting URL request and loading image with Glide
        WebUtil webUtil = new WebUtil();
        String url = webUtil.getUrlServlet("GetShopImage");
        Map<String,String> params = new HashMap<>();
        params.put("name",name);
        url = webUtil.addParametersToURL(url,params);

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.shopimage_loading)
                .error(R.drawable.shopimage_placeholder);

        RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                LinearLayout linearLayout = (LinearLayout) imageView.getParent();
                int padding = linearLayout.getPaddingTop();
                int imageHeight = resource.getIntrinsicHeight();
                int height = imageHeight + padding * 2;

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
                layoutParams.height = height;
                linearLayout.setLayoutParams(layoutParams);

                LinearLayout textLL = (LinearLayout) descriptionTV.getParent();
                layoutParams = (LinearLayout.LayoutParams) textLL.getLayoutParams();
                layoutParams.height = imageHeight;
                textLL.setLayoutParams(layoutParams);

                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) descriptionTV.getLayoutParams();
                int marginTop = marginLayoutParams.topMargin;

                int maxH = imageHeight - nameTV.getMeasuredHeight() - marginTop;
                int lineH = descriptionTV.getLineHeight();
                int lines = maxH / lineH;
                descriptionTV.setMaxLines(lines);

                return false;
            }
        };

        Glide.with(context).load(url).apply(requestOptions).listener(requestListener).into(viewHolder.imageView);

        //Set shop name and description
        viewHolder.nameTV.setText(name);
        viewHolder.descriptionTV.setText(description);

        return convertView;
    }
}
