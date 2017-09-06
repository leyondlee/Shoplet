package Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import mapp.teamkcl.shoplet.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Model.Category;
import Model.Shop;
import DB.DBHelper;
import Singleton.UserSingleton;
import Singleton.VolleySingleton;

import static DB.Constants.CATEGORY_NAME;
import static DB.Constants.CATEGORY_ORDER_BY;
import static DB.Constants.CATEGORY_TABLE_NAME;
import static DB.Constants.SHOP_CATEGORY;
import static DB.Constants.SHOP_CONTACT;
import static DB.Constants.SHOP_DESCRIPTION;
import static DB.Constants.SHOP_NAME;
import static DB.Constants.SHOP_ORDER_BY;
import static DB.Constants.SHOP_POSTALCODE;
import static DB.Constants.SHOP_TABLE_NAME;
import static DB.Constants.SHOP_UNITNO;

/**
 * Created by Leyond on 17/12/2016.
 */

public class WebUtil {
    private final String IP = Constants.url; //10.0.2.2:8080

    private String loginurl = getUrlServlet("Login");
    private String registerurl = getUrlServlet("Register");
    private String addshopurl = getUrlServlet("AddShop");
    private String getcategoriesurl = getUrlServlet("GetCategories");
    private String shopsurl = getUrlServlet("Shops");
    private String shopurl = getUrlServlet("GetShop");
    private String updateaccounturl = getUrlServlet("UpdateAccount");
    private String maplocationurl = "http://maps.googleapis.com/maps/api/geocode/json";

    private AppUtil appUtil = new AppUtil();
    private ListenerUtil listenerUtil = new ListenerUtil();

    /*
        Function: getUrlServlet()
        Description: Remove credentials from SharedPreferences
        Parameters: <Servlet>
        Return: String
     */
    public String getUrlServlet(String servlet) {
        String url = "http://" + IP + "/Shoplet/" + servlet;
        return url;
    }

    /*
        Function: addParametersToURL()
        Description: Remove credentials from SharedPreferences
        Parameters: <URL>, <Params>
        Return: String
     */
    public String addParametersToURL(String URL, Map<String,String> params) {
        boolean first = true;
        for (Map.Entry<String,String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (first) {
                first = false;
                URL += "?";
            } else {
                URL += "&";
            }

            URL += key + "=";
            try {
                URL += URLEncoder.encode(value, "UTF-8");
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        return URL;
    }

    /*
        Function: showShopMapLocation()
        Description: Remove credentials from SharedPreferences
        Parameters: <Context>, <Shop>, <Show Dialog>
        Return: NIL
     */
    public void showShopMapLocation(Context context, Shop shop, boolean showDialog) {
        ProgressDialog pd = null;

        if (showDialog) {
            pd = appUtil.createPD(context,"","Loading...",ProgressDialog.STYLE_SPINNER,true,true,true);
        }

        String url = maplocationurl + "?address=" + shop.getPostalcode();

        doPostRequest(context,url,listenerUtil.showShopMapLocationListener(context,shop.getName(),pd),listenerUtil.getErrorListener(context,pd),null,pd);
    }

    /*
        Function: getCategories()
        Description: Get Categories from Server
        Parameters: <Context>, <container>, <Layout Resource>, <Show Dialog>
        Return: NIL
     */
    public void getCategories(final Context context, final LinearLayout container, final int layoutresource, boolean showDialog) {
        ProgressDialog pd = null;

        if (showDialog) {
            pd = appUtil.createPD(context,"","Loading...",ProgressDialog.STYLE_SPINNER,true,false,false);
        }

        final ProgressDialog tempPD = pd;
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (tempPD != null) {
                    tempPD.dismiss();
                    Toast.makeText(context, context.getString(R.string.unabletoconnectcache_text),Toast.LENGTH_SHORT).show();
                }

                DBHelper dbHelper = new DBHelper(context);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query(CATEGORY_TABLE_NAME,new String[] {CATEGORY_NAME},null,null,null,null,CATEGORY_ORDER_BY);

                ArrayList<Category> categories = new ArrayList<Category>();
                while (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    Category category = new Category();
                    category.setName(name);

                    categories.add(category);
                }

                appUtil.createCategoryRows(context,categories,container,layoutresource);

                dbHelper.close();
            }
        };

        doPostRequest(context,getcategoriesurl,listenerUtil.getCategoriesListener(context,container,layoutresource,pd),errorListener,null,pd);
    }

    /*
        Function: getCategoriesSpinner()
        Description: Get Categories from Server and put into Spinner
        Parameters: <Context>, <Spinner>
        Return: NIL
     */
    public void getCategoriesSpinner(final Context context, final Spinner spinner) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("Categories");

                    ArrayList<String> categories = new ArrayList<String>();
                    categories.add(context.getResources().getString(R.string.selectcategory_text));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String name = jsonArray.optString(i);

                        categories.add(name);
                    }

                    Collections.sort(categories);

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,categories);
                    spinner.setAdapter(arrayAdapter);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        };

        Map<String,String> params = new HashMap<String,String>();
        params.put("spinner","1");

        doPostRequest(context,getcategoriesurl,listener,listenerUtil.getErrorListener(context,null),params,null);
    }

    /*
        Function: register()
        Description: Register with Server
        Parameters: <Context>, <Username>, <Password>, <Confirm Password>, <Show Dialog>
        Return: NIL
     */
    public void register(Context context, String username, String password, String confirmpassword, boolean showDialog) {
        ProgressDialog pd = null;

        Map<String,String> params = new HashMap<String,String>();
        params.put("username",username);
        params.put("password",password);
        params.put("confirmpassword",confirmpassword);

        if (showDialog) {
            pd = appUtil.createPD(context,"","Loading...",ProgressDialog.STYLE_SPINNER,true,true,false);
        }

        doPostRequest(context,registerurl,listenerUtil.registerResponseListener(context,params,pd),listenerUtil.getErrorListener(context,pd),params,pd);
    }

    /*
        Function: login()
        Description: Login with Server
        Parameters: <Context>, <Username>, <Password>, <Show Dialog>, <Finish>
        Return: NIL
     */
    public void login(Context context, String username, String password, boolean showDialog, boolean finish) {
        ProgressDialog pd = null;

        Map<String,String> params = new HashMap<String,String>();
        params.put("username",username);
        params.put("password",password);

        if (showDialog) {
            pd = appUtil.createPD(context,"","Loading...",ProgressDialog.STYLE_SPINNER,true,true,true);
        }

        doPostRequest(context,loginurl,listenerUtil.loginResponseListener(context,params,pd,finish),listenerUtil.getErrorListener(context,pd),params,pd);
    }

    /*
        Function: addShop()
        Description: Add shop to Server
        Parameters: <Context>, <Name>, <Contact>, <Postal Code>, <Unit No>, <Category>, <Description>, <Image>, <Show Dialog>
        Return: NIL
     */
    public void addShop(Context context, String name, String contact, String postal, String unitno, String category, String description, String image, boolean showDialog) {
        ProgressDialog pd = null;

        UserSingleton userSingleton = UserSingleton.getInstance(context);
        String username = userSingleton.getUsername();
        String password = userSingleton.getPassword();

        String encodedImage = "";
        if (image != null && !image.isEmpty()) {
            Bitmap bitmap = appUtil.getImage(image, 1024, 600);
            encodedImage = appUtil.encodeBase64(bitmap);
        }

        Map<String,String> params = new HashMap<String,String>();
        params.put("username",username);
        params.put("password",password);
        params.put("name",name);
        params.put("contact",contact);
        params.put("postalcode",postal);
        params.put("unitno",unitno);
        params.put("category",category);
        params.put("description",description);
        params.put("image",encodedImage);

        if (showDialog) {
            pd = appUtil.createPD(context,"","Loading...",ProgressDialog.STYLE_SPINNER,true,true,false);
        }

        doPostRequest(context,addshopurl,listenerUtil.addShopResponseListener(context,pd),listenerUtil.getErrorListener(context,pd),params,pd);
    }

    /*
        Function: updateAccount()
        Description: Update Account in Server
        Parameters: <Context>, <String Array of passwords>, <Show Dialog>
        Return: NIL
     */
    public void updateAccount(Context context, String[] passwords, boolean showDialog) {
        ProgressDialog pd = null;

        UserSingleton userSingleton = UserSingleton.getInstance(context);
        String username = userSingleton.getUsername();
        String password = userSingleton.getPassword();

        Map<String,String> params = new HashMap<String,String>();
        params.put("username",username);
        params.put("password",password);
        params.put("currentpassword",passwords[0]);
        params.put("newpassword",passwords[1]);
        params.put("confirmnewpassword",passwords[2]);

        if (showDialog) {
            pd = appUtil.createPD(context,"","Loading...",ProgressDialog.STYLE_SPINNER,true,true,false);
        }

        doPostRequest(context,updateaccounturl,listenerUtil.updateAccountListener(context,pd,username,passwords[1]),listenerUtil.getErrorListener(context,pd),params,pd);
    }

    /*
        Function: getAndDisplayShops()
        Description: Get shops from Server
        Parameters: <Context>, <ListView>, <Category>, <SwipeRefreshLayout>, <Show Dialog>
        Return: NIL
     */
    public void getAndDisplayShops(final Context context, final ListView listView, final String category, final SwipeRefreshLayout swipeRefreshLayout, boolean showDialog) {
        ProgressDialog pd = null;

        Map<String,String> params = null;
        if (category != null && !category.equals("All Categories")) {
            params = new HashMap<String, String>();
            params.put("category", category);
        }

        if (showDialog) {
            pd = appUtil.createPD(context,"","Loading...",ProgressDialog.STYLE_SPINNER,true,true,false);
        }

        final ProgressDialog tempPD = pd;
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String[] columns = {SHOP_NAME,SHOP_CONTACT,SHOP_POSTALCODE,SHOP_UNITNO,SHOP_CATEGORY,SHOP_DESCRIPTION};

                DBHelper dbHelper = new DBHelper(context);
                SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

                Cursor cursor;
                if (category.equals("All Categories")) {
                    cursor = sqLiteDatabase.query(SHOP_TABLE_NAME, columns, null, null, null, null, SHOP_ORDER_BY);
                } else {
                    cursor = sqLiteDatabase.query(SHOP_TABLE_NAME, columns, SHOP_CATEGORY + " = ?", new String[] {category}, null, null, SHOP_ORDER_BY);
                }

                ArrayList<Shop> shops = new ArrayList<>();
                while (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    String contact = cursor.getString(1);
                    String postalcode = cursor.getString(2);
                    String unitno = cursor.getString(3);
                    String category = cursor.getString(4);
                    String description = cursor.getString(5);

                    Shop shop = new Shop(name,contact,postalcode,unitno,category,description,null);
                    shops.add(shop);
                }

                dbHelper.close();

                appUtil.createShopRows(context,listView,shops);

                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (tempPD != null) {
                    tempPD.dismiss();
                    Toast.makeText(context, context.getString(R.string.unabletoconnectcache_text), Toast.LENGTH_SHORT).show();
                }
            }
        };

        doPostRequest(context,shopsurl,listenerUtil.displayShopsListener(context,listView,swipeRefreshLayout,pd),errorListener,params,pd);
    }

    /*
        Function: getAndDisplayShop()
        Description: Get shop from Server
        Parameters: <Context>, <Name>, <Show Dialog>
        Return: NIL
     */
    public void getAndDisplayShop(final Context context, final String name, boolean showDialog) {
        ProgressDialog pd = null;

        Map<String,String> params = new HashMap<String,String>();
        params.put("name",name);

        if (showDialog) {
            pd = appUtil.createPD(context,"","Loading...",ProgressDialog.STYLE_SPINNER,true,true,true);
        }

        final ProgressDialog tempPD = pd;
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String[] columns = {SHOP_NAME,SHOP_CONTACT,SHOP_POSTALCODE,SHOP_UNITNO,SHOP_CATEGORY,SHOP_DESCRIPTION};

                DBHelper dbHelper = new DBHelper(context);
                SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

                Cursor cursor = sqLiteDatabase.query(SHOP_TABLE_NAME,columns,SHOP_NAME + " = ?",new String[] {name},null,null,SHOP_ORDER_BY);
                System.out.println("Cursor = " + cursor.getCount());
                if (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    String contact = cursor.getString(1);
                    String postalcode = cursor.getString(2);
                    String unitno = cursor.getString(3);
                    String category = cursor.getString(4);
                    String description = cursor.getString(5);

                    Shop shop = new Shop(name,contact,postalcode,unitno,category,description,null);
                    appUtil.openShopPage(context,shop);
                }

                dbHelper.close();

                if (tempPD != null) {
                    tempPD.dismiss();
                    Toast.makeText(context, context.getString(R.string.unabletoconnectcache_text), Toast.LENGTH_SHORT).show();
                }
            }
        };

        doPostRequest(context,shopurl,listenerUtil.displayShopListener(context,pd),errorListener,params,pd);
    }

    /*
        Function: doPostRequest()
        Description: Perform POST Request
        Parameters: <Context>, <URL>, <Response.Listener<String>>, <Response.ErrorListener>, <Params>, <ProgressDialog>
        Return: NIL
     */
    public void doPostRequest(Context context, String url, Response.Listener<String> responseListener, Response.ErrorListener errorListener, final Map<String,String> params, ProgressDialog pd) {
        final String TAG = context.getClass().getSimpleName();

        final RequestQueue requestQueue = VolleySingleton.getInstance(context).getRequestQueue();

        if (errorListener == null) {
            errorListener = listenerUtil.getErrorListener(context,pd);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {
            @Override
            protected Map<String,String> getParams() {
                return params;
            }
        };

        stringRequest.setTag(TAG);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

        if (pd != null) {
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                   @Override
                   public void onCancel(DialogInterface dialog) {
                       requestQueue.cancelAll(TAG);
                   }
               }
            );

            pd.show();
        }
    }
}
