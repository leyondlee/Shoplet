package mapp.teamkcl.shoplet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.Map;

import Util.AppUtil;
import Util.WebUtil;

/**
 * Created by Leyond on 25/12/2016.
 */

public class Shop extends AppCompatActivity implements OnClickListener {
    private static final int CALL_PERMISSION = 0;
    private static final int LOCATION_PERMISSION = 1;

    private AppUtil appUtil = new AppUtil();
    private WebUtil webUtil = new WebUtil();

    private Model.Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop);

        appUtil.setupToolbar(this);

        final ImageView imageView = (ImageView) findViewById(R.id.shop_image);
        TextView nameET = (TextView) findViewById(R.id.shop_name);
        TextView descriptionET = (TextView) findViewById(R.id.shop_description);
        TextView contactET = (TextView) findViewById(R.id.shop_contactno);
        TextView postalET = (TextView) findViewById(R.id.shop_postalcode);
        TextView unitnoET = (TextView) findViewById(R.id.shop_unitno);

        //Get shop object from Intent
        shop = (Model.Shop) getIntent().getSerializableExtra("Shop");

        //Get shop details
        nameET.setText(shop.getName());
        descriptionET.setText(shop.getDescription());
        contactET.setText(shop.getContact());
        postalET.setText("Singapore " + shop.getPostalcode());
        unitnoET.setText(shop.getUnitno());

        //Craft URL for shop image and load with Glide
        String url = webUtil.getUrlServlet("GetShopImage");
        Map<String,String> params = new HashMap<>();
        params.put("name",shop.getName());
        url = webUtil.addParametersToURL(url,params);

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.shopimage_loading)
                .error(R.drawable.shopimage_placeholder);

        Glide.with(this).load(url).apply(requestOptions).into(imageView);

        Button callButton = (Button) findViewById(R.id.shop_callButton);
        callButton.setOnClickListener(this);

        Button locateButton = (Button) findViewById(R.id.shop_locateButton);
        locateButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0,0);

        invalidateOptionsMenu();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CALL_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Check if user allow call permission
                    callShop();
                } else {
                    Toast.makeText(this,getString(R.string.requirecall_text),Toast.LENGTH_LONG).show();
                }

                break;
            }

            case LOCATION_PERMISSION: {
                locateShop();

                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        appUtil.createMenuItems(this,menu,false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        appUtil.onMenuItemSelected(this,item);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shop_callButton: {
                if (appUtil.hasPermission(this,Manifest.permission.CALL_PHONE)) { //Check if have call permission
                    callShop();
                } else {
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CALL_PHONE},CALL_PERMISSION);
                }

                break;
            }

            case R.id.shop_locateButton: {
                if (appUtil.hasPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)) { //Check if have locate permission
                    locateShop();
                } else {
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);
                }

                break;
            }
        }
    }

    private void callShop() {
        if (appUtil.hasPermission(this,Manifest.permission.CALL_PHONE)) { //Check here to remove error
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+65" + shop.getContact()));
            startActivity(intent);
        }
    }

    private void locateShop() {
        webUtil.showShopMapLocation(this,shop,true);
    }
}
