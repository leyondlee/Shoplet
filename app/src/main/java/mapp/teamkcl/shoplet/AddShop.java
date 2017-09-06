package mapp.teamkcl.shoplet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import Model.Camera;
import Model.Gallery;
import Util.AppUtil;
import Util.UserUtil;
import Util.WebUtil;

/**
 * Created by Leyond on 19/12/2016.
 */

public class AddShop extends AppCompatActivity implements OnClickListener {
    private static final int CAMERA_PERMISSION = 0;
    private static final int STORAGE_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    private AppUtil appUtil = new AppUtil();
    private UserUtil userUtil = new UserUtil();
    private WebUtil webUtil = new WebUtil();

    private Camera camera;
    private Gallery gallery;

    private Spinner categorySpinner;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addshop);

        appUtil.setupToolbar(this);

        //Set close button on top left
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        //Get categories from server and set them into spinner
        categorySpinner = (Spinner) findViewById(R.id.addshop_category);
        webUtil.getCategoriesSpinner(this,categorySpinner);

        Button cameraButton = (Button) findViewById(R.id.addshop_camera);
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) { //Check if phone has a camera
            cameraButton.setOnClickListener(this);
        } else {
            cameraButton.setEnabled(false);
        }

        Button photolibraryButton = (Button) findViewById(R.id.addshop_photolibrary);
        photolibraryButton.setOnClickListener(this);

        Button addshopButton = (Button) findViewById(R.id.addshop_button);
        addshopButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0,0);

        invalidateOptionsMenu();

        //Check if user is logged in. If not, end Activity
        if (!userUtil.isLoggedin(this)) {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Check if user clicks "Allow"
                    accessCamera();
                } else {
                    Toast.makeText(this,getString(R.string.requirecamera_text),Toast.LENGTH_LONG).show();
                }

                break;
            }

            case STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Check if user clicks "Allow"
                    accessGallery();
                } else {
                    Toast.makeText(this,getString(R.string.requirestorage_text),Toast.LENGTH_LONG).show();
                }

                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        appUtil.createMenuItems(this,menu,true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        appUtil.onMenuItemSelected(this,item);
        if (item.getItemId() == R.id.menu_signout) { //If user clicks "Sign Out", end Activity
            finish();
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            photoPath = null;

            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE: {
                    //Add image to gallery
                    camera.addPicToGallery();
                    photoPath = camera.getPhotoPath();

                    break;
                }

                case REQUEST_IMAGE_GALLERY: {
                    gallery.setPhotoUri(data.getData());
                    photoPath = gallery.getPath();

                    break;
                }
            }

            if (photoPath != null) {
                //Load image into ImageView
                ImageView imageView = (ImageView) findViewById(R.id.addshop_image);
                Glide.with(this).load(new File(photoPath)).into(imageView);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addshop_camera: {
                if (appUtil.hasPermission(this,Manifest.permission.CAMERA)) { //Check if have camera permission
                    accessCamera();
                } else {
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA},CAMERA_PERMISSION);
                }

                break;
            }

            case R.id.addshop_photolibrary: {
                if (appUtil.hasPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) && appUtil.hasPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)) { //Check if have storage permission
                    accessGallery();
                } else {
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION);
                }

                break;
            }

            case R.id.addshop_button: {
                EditText nameET = (EditText) findViewById(R.id.addshop_name);
                EditText contactET = (EditText) findViewById(R.id.addshop_contactno);
                EditText postalET = (EditText) findViewById(R.id.addshop_postalcode);
                EditText unitno1ET = (EditText) findViewById(R.id.addshop_unitno_1);
                EditText unitno2ET = (EditText) findViewById(R.id.addshop_unitno_2);
                EditText descriptionET = (EditText) findViewById(R.id.addshop_description);

                //Get values from Views
                String name = nameET.getText().toString();
                String contact = contactET.getText().toString();
                String postal = postalET.getText().toString();
                String unitno1 = unitno1ET.getText().toString();
                String unitno2 = unitno2ET.getText().toString();
                String description = descriptionET.getText().toString();
                String image = photoPath;
                String category = categorySpinner.getSelectedItem().toString();

                //Craft unit number
                String unitno = "";
                if (!unitno1.isEmpty() && !unitno2.isEmpty()) {
                    unitno = "#" + unitno1ET.getText().toString() + "-" + unitno2ET.getText().toString();
                }

                //Client side checking
                if (name.length() == 0) {
                    Toast.makeText(this, "Shop name cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (contact.length() < 8) {
                    Toast.makeText(this, "Contact No. must be 8 numbers", Toast.LENGTH_SHORT).show();
                } else if (!(postal.length() > 0 && postal.length() <= 6)) {
                    Toast.makeText(this, "Postal Code cannot be empty and have a maximum of 6 digits", Toast.LENGTH_SHORT).show();
                } else if (!(unitno.length() > 5 && unitno.length() <= 7)) {
                    Toast.makeText(this, "Unit No. must be between 4 - 5 numbers combined", Toast.LENGTH_SHORT).show();
                } else if (category.equals(getResources().getString(R.string.selectcategory_text))) {
                    Toast.makeText(this, "Select a category", Toast.LENGTH_SHORT).show();
                } else {
                    webUtil.addShop(this,name,contact,postal,unitno,category,description,image,true);
                }

                break;
            }

            default: {
                appUtil.onClickDefault(this,v);
                break;
            }
        }
    }

    private void accessCamera() {
        camera = new Camera(this);
        startActivityForResult(camera.getIntent(), REQUEST_IMAGE_CAPTURE);
    }

    private void accessGallery() {
        gallery = new Gallery(this);
        startActivityForResult(gallery.getIntent(), REQUEST_IMAGE_GALLERY);
    }
}
