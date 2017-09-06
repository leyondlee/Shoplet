package mapp.teamkcl.shoplet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import Util.AppUtil;

/**
 * Created by Clement on 6/2/2017.
 */

public class ShopMap extends AppCompatActivity implements OnMapReadyCallback {
    private AppUtil appUtil = new AppUtil();

    private GoogleMap googleMap = null;

    private String name;
    private double lat;
    private double lng;

    Spinner shopmapSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopmap);

        appUtil.setupToolbar(this);

        //Get shop name from Intent and set as title from previous Activity
        name = getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(name);

        //Get coordinates from Intent via Shop Activty
        double[] coords = getIntent().getDoubleArrayExtra("coordinates"); // get shop coordinates from previos activity
        lat = coords[0];
        lng = coords[1];

        //Set map Fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.shopmap); //Get fragment from xml using frag mgr
        supportMapFragment.getMapAsync(this); //Load map into fragment

        //Set spinner listener, top left
        shopmapSpinner = (Spinner) findViewById(R.id.shopmap_spinner);
        shopmapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (googleMap != null) {
                    //Set map type
                    String maptype = adapterView.getSelectedItem().toString();
                    System.out.println(maptype);
                    if (maptype.equals(getString(R.string.normal_text))) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    } else if (maptype.equals(getString(R.string.hybrid_text))) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    } else if (maptype.equals(getString(R.string.satellite_text))) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    } else if (maptype.equals(getString(R.string.terrain_text))) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    } else {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0,0);

        invalidateOptionsMenu();
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
    public void onMapReady(GoogleMap googleMap) {   // map will be put in an activity
        this.googleMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) { //Check if have locate permission
            googleMap.setMyLocationEnabled(true);
        }

        //Add marker to shop location
        LatLng latLng = new LatLng(lat,lng);
        googleMap.addMarker(new MarkerOptions().position(latLng).title(name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //Make spinner visible
        shopmapSpinner.setVisibility(View.VISIBLE);
    }
}
