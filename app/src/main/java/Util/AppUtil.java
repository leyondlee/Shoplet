package Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import mapp.teamkcl.shoplet.About;
import mapp.teamkcl.shoplet.Account;
import mapp.teamkcl.shoplet.AddShop;
import mapp.teamkcl.shoplet.Login;
import mapp.teamkcl.shoplet.MainActivity;
import mapp.teamkcl.shoplet.R;
import mapp.teamkcl.shoplet.Settings;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import Adapter.ShopRowAdapter;
import Model.Category;
import Model.Shop;
import DB.DBHelper;
import Singleton.UserSingleton;

import static DB.Constants.CATEGORY_NAME;
import static DB.Constants.CATEGORY_TABLE_NAME;
import static DB.Constants.SHOP_CATEGORY;
import static DB.Constants.SHOP_CONTACT;
import static DB.Constants.SHOP_DESCRIPTION;
import static DB.Constants.SHOP_NAME;
import static DB.Constants.SHOP_POSTALCODE;
import static DB.Constants.SHOP_TABLE_NAME;
import static DB.Constants.SHOP_UNITNO;

/**
 * Created by Leyond on 14/1/2017.
 */

public class AppUtil {
    public AppCompatActivity getActivity(Context context) {
        return ((AppCompatActivity) context);
    }

    /*
        Function: setupToolbar()
        Description: Initialize Action Bar in Activity
        Parameters: <Context>
        Return: NIL
     */
    public void setupToolbar(Context context) {
        AppCompatActivity activity = getActivity(context);
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        if (context.getClass() != MainActivity.class) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /*
        Function: startActivity()
        Description: Start activity
        Parameters: <Context>, <Class of next Activity>, <Finish current>, <Intent Parameters>
        Return: NIL
     */
    public void startActivity(Context context, Class nextActivity, boolean finish, Map<String,String> params) {
        if (finish || context.getClass() == nextActivity) {
            getActivity(context).finish();
        }

        Intent i = new Intent(context, nextActivity);
        if (params != null) {
            for (Map.Entry<String,String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                i.putExtra(key,value);
            }
        }

        context.startActivity(i);
    }

    /*
        Function: startActivityForResult()
        Description: Start activity and wait for result
        Parameters: <Context>, <Class of next Activity>, <Code>, <Intent Parameters>
        Return: NIL
     */
    public void startActivityForResult(Context context, Class nextActivity, int code, Map<String,String> params) {
        Intent i = new Intent(context,nextActivity);
        if (params != null) {
            for (Map.Entry<String,String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                i.putExtra(key,value);
            }
        }

        getActivity(context).startActivityForResult(i,code);
    }

    /*
        Function: createMenuItems()
        Description: Create menu items
        Parameters: <Context>, <Menu>, <Empty Menu>
        Return: NIL
     */
    public void createMenuItems(Context context, Menu menu, boolean empty) {
        UserUtil userUtil = new UserUtil();

        Activity activity = getActivity(context);
        refreshTheme(activity);

        MenuInflater menuInflater = activity.getMenuInflater();

        if (!empty) {
            boolean login = userUtil.isLoggedin(context);
            if (login) {
                menuInflater.inflate(R.menu.menu_login, menu);
            } else {
                menuInflater.inflate(R.menu.menu_main, menu);
            }
        }
    }

    /*
        Function: onClickDefault()
        Description: Handles OnClick Defaults
        Parameters: <Context>, <View v>
        Return: NIL
     */
    public void onClickDefault(Context context, View v) {
        switch (v.getId()) {
            case R.id.outer_container: {
                hideKeyboard(context);
                break;
            }
        }
    }

    /*
        Function: onMenuItemSelected()
        Description: Handles Menu selection
        Parameters: <Context>, <MenuItem item>
        Return: NIL
     */
    public void onMenuItemSelected(Context context, MenuItem item) {
        Activity activity = getActivity(context);

        switch (item.getItemId()) {
            case android.R.id.home: {
                getActivity(context).finish();
                break;
            }

            case R.id.menu_about: {
                startActivity(context, About.class, false, null);
                break;
            }

            case R.id.menu_settings: {
                startActivity(context, Settings.class, false, null);
                break;
            }

            case R.id.menu_login: {
                startActivity(context, Login.class, false, null);
                break;
            }

            case R.id.menu_addshop: {
                startActivity(context, AddShop.class, false, null);
                break;
            }

            case R.id.menu_account: {
                startActivity(context, Account.class, false, null);
                break;
            }

            case R.id.menu_signout: {
                hideKeyboard(context);
                UserSingleton userSingleton = UserSingleton.getInstance(context);
                userSingleton.remove();
                activity.invalidateOptionsMenu();

                Toast.makeText(context,"Signed out",Toast.LENGTH_SHORT).show();

                break;
            }
        }
    }

    /*
        Function: createPD()
        Description: Creates ProgressDialog
        Parameters: <Context>, <Title>, <Message>, <Style>, <Indeterminate>, <Cancelable>, <Close when touch outside>
        Return: ProgressDialog
     */
    public ProgressDialog createPD(Context context, CharSequence title, CharSequence msg, int style, boolean indeterminate, boolean cancelable, boolean touchOutside) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setTitle(title);
        pd.setMessage(msg);
        pd.setProgressStyle(style);
        pd.setIndeterminate(indeterminate);
        pd.setCancelable(cancelable);
        pd.setCanceledOnTouchOutside(touchOutside);

        return pd;
    }

    /*
        Function: encodeBase64()
        Description: Encodes bitmap in base64
        Parameters: <Bitmap>
        Return: String
     */
    public String encodeBase64(Bitmap bitmap) {
        String encoded = null;

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }

            byte[] bytes = os.toByteArray();
            os.close();
            encoded = new String(Base64.encode(bytes, Base64.URL_SAFE),"UTF-8");
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return encoded;
    }

    /*
        Function: decodeBase64()
        Description: Decode base64 string to bytes
        Parameters: <Encoded String>
        Return: byte[]
     */
    public byte[] decodeBase64(String encoded) {
        byte[] bytes = null;

        try {
            bytes = Base64.decode(encoded.getBytes("UTF-8"), Base64.URL_SAFE);
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return bytes;
    }

    /*
        Function: calculateInSampleSize()
        Description: Calculate In Sample Size
        Parameters: <BitmapFactory Options>, <Target Width>, <Target Height>
        Return: int
     */
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /*
        Function: decodeSampledBitmapFromByte()
        Description: Byte to Bitmap
        Parameters: <Bytes>, <Target Width>, <Target Height>
        Return: Bitmap
     */
    public Bitmap decodeSampledBitmapFromByte(byte[] bytes, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        return bitmap;
    }

    /*
        Function: getImage()
        Description: Load image from location
        Parameters: <Image Location>, <Target Width>, <Target Height>
        Return: Bitmap
     */
    public Bitmap getImage(String imagelocation, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagelocation,bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(imagelocation, bmOptions);

        return bitmap;
    }

    /*
        Function: createShopRows()
        Description: Create shop rows and put into ListView
        Parameters: <Context>, <ListView>, <Shops>
        Return: NIL
     */
    public void createShopRows(final Context context, ListView listView, final ArrayList<Shop> shops) {
        Collections.sort(shops, new Comparator<Shop>() {
            @Override
            public int compare(Shop shop, Shop t1) {
                return (shop.getName()).compareTo(t1.getName());
            }
        });

        ShopRowAdapter adapter = new ShopRowAdapter(context,shops);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Shop shop = shops.get(i);
                String name = shop.getName();

                WebUtil webUtil = new WebUtil();
                webUtil.getAndDisplayShop(context, name, true);
            }
        });
    }

    /*
        Function: createCategoryRow()
        Description: Create category rows
        Parameters: <Context>, <Categories>, <Container>, <Layout Resource>
        Return: NIL
     */
    public void createCategoryRows(final Context context, ArrayList<Category> categories, LinearLayout container, int layoutresource) {
        Collections.sort(categories, new Comparator<Category>() {
            @Override
            public int compare(Category category, Category t1) {
                return (category.getName()).compareTo(t1.getName());
            }
        });

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        ImageView imageView;
        TextView nameTV;

        int size = categories.size();

        for (int i = 0; i < size; i++) {
            Category category = categories.get(i);

            if (i % 2 == 0) {
                view = layoutInflater.inflate(layoutresource, null);

                imageView = (ImageView) view.findViewById(R.id.categoryrow_image1);
                nameTV = (TextView) view.findViewById(R.id.categoryrow_name1);

                container.addView(view);
            } else {
                imageView = (ImageView) view.findViewById(R.id.categoryrow_image2);
                nameTV = (TextView) view.findViewById(R.id.categoryrow_name2);
            }

            String name = category.getName();
            nameTV.setText(category.getName());

            WebUtil webUtil = new WebUtil();
            String url = webUtil.getUrlServlet("GetCategoryImage");
            if (!name.equals("All Categories")) {
                Map<String,String> params = new HashMap<>();
                params.put("category",name);
                url = webUtil.addParametersToURL(url,params);
            }

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.categoryimage_loading)
                    .error(R.drawable.categoryimage_placeholder);

            Glide.with(context).load(url).apply(requestOptions).into(imageView);
        }

        if (size % 2 == 1) {
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.categoryrow_box2);
            linearLayout.setVisibility(View.INVISIBLE);
            linearLayout.setClickable(false);
        }
    }

    /*
        Function: hideKeyboard()
        Description: Hide keyboard
        Parameters: <Context>
        Return: NIL
     */
    public void hideKeyboard(Context context) {
        View view = getActivity(context).getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*
        Function: refreshTheme()
        Description: Refresh Theme
        Parameters: <Context>
        Return: NIL
     */
    public void refreshTheme(Context context) {
        Toolbar toolbar = (Toolbar) getActivity(context).findViewById(R.id.toolbar);


        SharedPreferences sp = context.getSharedPreferences("Settings",context.MODE_PRIVATE);
        int theme = sp.getInt("theme",-1);

        int[] colors;
        int backgroundcolor;
        int toolbarcolor;
        switch (theme) {
            case 1: {
                colors = context.getResources().getIntArray(R.array.red_colors);
                break;
            }

            case 2: {
                colors = context.getResources().getIntArray(R.array.purple_colors);
                break;
            }

            case 3: {
                colors = context.getResources().getIntArray(R.array.blue_colors);
                break;
            }

            case 4: {
                colors = context.getResources().getIntArray(R.array.green_colors);
                break;
            }

            case 5: {
                colors = context.getResources().getIntArray(R.array.amber_colors);
                break;
            }

            case 6: {
                colors = context.getResources().getIntArray(R.array.bluegrey_colors);
                break;
            }

            case 0: {

            }

            default: {
                colors = context.getResources().getIntArray(R.array.indigo_colors);
                break;
            }
        }

        backgroundcolor = colors[0];
        toolbarcolor = colors[1];

        toolbar.setBackgroundColor(toolbarcolor);

        View view = getActivity(context).findViewById(android.R.id.content);
        view.setBackgroundColor(backgroundcolor);
    }

    /*
        Function: cacheCategories()
        Description: Cache Categories into SQLite Database
        Parameters: <Context>, <Categories>
        Return: NIL
     */
    public void cacheCategories(Context context, ArrayList<Category> categories) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.delete(CATEGORY_TABLE_NAME,null,null);

        for (Category category : categories) {
            String name = category.getName();

            ContentValues values = new ContentValues();
            values.put(CATEGORY_NAME, name);
            sqLiteDatabase.insertOrThrow(CATEGORY_TABLE_NAME, null, values);
        }

        dbHelper.close();
    }

    /*
        Function: cacheShop()
        Description: Cache Shop into SQLite Database
        Parameters: <Context>, <Shop>
        Return: NIL
     */
    public void cacheShop(Context context, Shop shop) {
        String name = shop.getName();
        String contact = shop.getContact();
        String postalcode = shop.getPostalcode();
        String unitno = shop.getUnitno();
        String category = shop.getCategory();
        String description = shop.getDescription();

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.delete(SHOP_TABLE_NAME,SHOP_NAME + " = ?",new String[] {name});

        ContentValues values = new ContentValues();
        values.put(SHOP_NAME,name);
        values.put(SHOP_CONTACT,contact);
        values.put(SHOP_POSTALCODE,postalcode);
        values.put(SHOP_UNITNO,unitno);
        values.put(SHOP_CATEGORY,category);
        values.put(SHOP_DESCRIPTION,description);
        sqLiteDatabase.insertOrThrow(SHOP_TABLE_NAME,null,values);

        dbHelper.close();
    }

    /*
        Function: hasPermission()
        Description: Check if have given permission
        Parameters: <Context>, <Permission>
        Return: boolean
     */
    public boolean hasPermission(Context context, String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(context,permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    /*
        Function: openShopPage()
        Description: Open Shop Activity
        Parameters: <Context>, <Shop>
        Return: NIL
     */
    public void openShopPage(Context context, Shop shop) {
        Intent i = new Intent(context, mapp.teamkcl.shoplet.Shop.class);
        i.putExtra("Shop",shop);
        context.startActivity(i);
    }
}
