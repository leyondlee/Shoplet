package Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import mapp.teamkcl.shoplet.Login;
import mapp.teamkcl.shoplet.R;
import mapp.teamkcl.shoplet.ShopMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import DB.DBHelper;
import Model.Category;
import Model.Shop;
import Singleton.UserSingleton;

import static DB.Constants.SHOP_TABLE_NAME;

/**
 * Created by Leyond on 7/2/2017.
 */

public class ListenerUtil {
    private AppUtil appUtil = new AppUtil();

    /*
        Function: getResponseCode()
        Description: Get Response Code from JSONObject
        Parameters: <Response>
        Return: int
     */
    private int getResponseCode(String response) {
        int code = -1;
        try {
            JSONObject jsonObject = new JSONObject(response);
            code = jsonObject.getInt("Code");
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return code;
    }

    /*
        Function: getLoginResponse()
        Description: Get Login Response from JSONObject
        Parameters: <Response>
        Return: boolean
     */
    private boolean getLoginResponse(String response) {
        boolean login = false;
        try {
            JSONObject jsonObject = new JSONObject(response);
            login = jsonObject.getBoolean("Success");
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return login;
    }

    /*
        Function: registerResponseListener()
        Description: Response Listener
        Parameters: <Context>, <Params>, <ProgressDialog>
        Return: Response.Listener<String>
     */
    public Response.Listener<String> registerResponseListener(final Context context, Map<String,String> params, final ProgressDialog pd) {
        final String username = params.get("username");
        final String password = params.get("password");

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int code = getResponseCode(response);
                if (code == 0) {
                    Intent i = new Intent();
                    i.putExtra("username",username);
                    i.putExtra("password",password);

                    Activity activity = appUtil.getActivity(context);
                    activity.setResult(Activity.RESULT_OK,i);
                    activity.finish();
                }

                if (pd != null) {
                    pd.dismiss();
                    switch (code) {
                        case 0: {
                            Toast.makeText(context,context.getString(R.string.registrationsuccess_text),Toast.LENGTH_SHORT).show();
                            break;
                        }

                        case 1: {
                            Toast.makeText(context,context.getString(R.string.usernameexists_text),Toast.LENGTH_SHORT).show();
                            break;
                        }

                        case 2: {
                            Toast.makeText(context,context.getString(R.string.passwordmismatch_text),Toast.LENGTH_SHORT).show();
                            break;
                        }

                        case 3: {
                            Toast.makeText(context,context.getString(R.string.missinginfo_text),Toast.LENGTH_SHORT).show();
                            break;
                        }

                        default: {
                            Toast.makeText(context,context.getString(R.string.defaulterror_text),Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            }
        };

        return listener;
    }

    /*
        Function: loginResponseListener()
        Description: Response Listener
        Parameters: <Context>, <Params>, <ProgressDialog>, <Finish>
        Return: Response.Listener<String>
     */
    public Response.Listener<String> loginResponseListener(final Context context, Map<String,String> params, final ProgressDialog pd, final boolean finish) {
        final String username = params.get("username");
        final String password = params.get("password");

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Activity activity = appUtil.getActivity(context);

                if (getLoginResponse(response)) {
                    UserSingleton userSingleton = UserSingleton.getInstance(context);
                    userSingleton.setUsername(username);
                    userSingleton.setPassword(password);
                    userSingleton.setLogin(true);
                    userSingleton.save();

                    if (finish) {
                        activity.finish();
                    }

                    if (pd != null) {
                        Toast.makeText(context, context.getString(R.string.loginsuccess_text), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (pd != null) {
                        Toast.makeText(context, context.getString(R.string.unabletologin_text), Toast.LENGTH_SHORT).show();
                    }
                }

                activity.invalidateOptionsMenu();

                if (pd != null) {
                    pd.dismiss();
                }
            }
        };

        return listener;
    }

    /*
        Function: addShopResponseListener()
        Description: Response Listener
        Parameters: <Context>, <Params>, <ProgressDialog>
        Return: Response.Listener<String>
     */
    public Response.Listener<String> addShopResponseListener(final Context context, final ProgressDialog pd) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int code = getResponseCode(response);

                switch (code) {
                    case 0: {
                        appUtil.getActivity(context).finish();
                        break;
                    }
                }

                if (pd != null) {
                    pd.dismiss();

                    switch (code) {
                        case -1: {
                            Toast.makeText(context, context.getString(R.string.invalidinfo_text), Toast.LENGTH_SHORT).show();
                            break;
                        }

                        case 0: {
                            Toast.makeText(context, context.getString(R.string.shopadded_text), Toast.LENGTH_SHORT).show();
                            break;
                        }

                        case 1: {
                            Toast.makeText(context, context.getString(R.string.shopnameused_text), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            }
        };

        return listener;
    }

    /*
        Function: displayShopsListener()
        Description: Response Listener
        Parameters: <Context>, <ListView>, <SwipeRefreshLayout>, <ProgressDialog>
        Return: Response.Listener<String>
     */
    public Response.Listener<String> displayShopsListener(final Context context, final ListView listView, final SwipeRefreshLayout swipeRefreshLayout, final ProgressDialog pd) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Iterator<String> keys = jsonObject.keys();

                    ArrayList<Shop> shops = new ArrayList<Shop>();
                    while (keys.hasNext()) {
                        String name = keys.next();
                        JSONArray array = jsonObject.getJSONArray(name);
                        String contact = array.optString(0);
                        String postalcode = array.optString(1);
                        String unitno = array.optString(2);
                        String category = array.optString(3);
                        String description = array.optString(4);

                        if (description.equals("")) {
                            description = "No Description";
                        }

                        Shop shop = new Shop(name,contact,postalcode,unitno,category,description,null);

                        shops.add(shop);
                    }

                    DBHelper dbHelper = new DBHelper(context);
                    SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
                    sqLiteDatabase.delete(SHOP_TABLE_NAME,null,null);
                    dbHelper.close();

                    appUtil.createShopRows(context,listView,shops);

                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    if (pd != null) {
                        pd.dismiss();
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        };

        return listener;
    }

    /*
        Function: displayShopListener()
        Description: Response Listener
        Parameters: <Context>, <ProgressDialog>
        Return: Response.Listener<String>
     */
    public Response.Listener<String> displayShopListener(final Context context, final ProgressDialog pd) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String name = jsonObject.getString("Name");
                    String contact = jsonObject.getString("Contact");
                    String postal = jsonObject.getString("Postalcode");
                    String unitno = jsonObject.getString("Unitno");
                    String category = jsonObject.getString("Category");
                    String description = jsonObject.getString("Description");

                    if (description.equals("")) {
                        description = "No Description";
                    }

                    Shop shop = new Shop(name,contact,postal,unitno,category,description,null);
                    appUtil.cacheShop(context,shop);

                    appUtil.openShopPage(context,shop);

                    if (pd != null) {
                        pd.dismiss();
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        };

        return listener;
    }

    /*
        Function: updateAccountListener()
        Description: Response Listener
        Parameters: <Context>, <ProgressDialog>, <Username>, <Password>
        Return: Response.Listener<String>
     */
    public Response.Listener<String> updateAccountListener(final Context context, final ProgressDialog pd, final String username, final String password) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    int code = getResponseCode(response);
                    switch (code) {
                        case 0: {
                            UserSingleton userSingleton = UserSingleton.getInstance(context);
                            userSingleton.remove();
                            userSingleton.setUsername(username);
                            userSingleton.setPassword(password);

                            appUtil.startActivity(context,Login.class,true,null);
                            break;
                        }
                    }

                    if (pd != null) {
                        pd.dismiss();

                        switch (code) {
                            case 0: {
                                Toast.makeText(context, context.getString(R.string.accountupdated_text), Toast.LENGTH_SHORT).show();
                                break;
                            }

                            case 1: {
                                Toast.makeText(context, context.getString(R.string.passwordmismatch_text), Toast.LENGTH_SHORT).show();
                                break;
                            }

                            case 2: {
                                Toast.makeText(context, context.getString(R.string.passwordempty_text), Toast.LENGTH_SHORT).show();
                                break;
                            }

                            case 3: {
                                Toast.makeText(context, context.getString(R.string.wrongpassword_text), Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        };

        return listener;
    }

    /*
        Function: getCategoriesListener()
        Description: Response Listener
        Parameters: <Context>, <Container>, <Layout Resource>, <ProgressDialog>
        Return: Response.Listener<String>
     */
    public Response.Listener<String> getCategoriesListener(final Context context, final LinearLayout container, final int layoutresource, final ProgressDialog pd) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("Categories");

                    ArrayList<Category> categories = new ArrayList<Category>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String name = jsonArray.optString(i);

                        Category category = new Category(name,null);

                        categories.add(category);
                    }

                    appUtil.cacheCategories(context,categories);
                    appUtil.createCategoryRows(context,categories,container,layoutresource);

                    if (pd != null) {
                        pd.dismiss();
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        };

        return listener;
    }

    /*
        Function: showShopMapLocationListener()
        Description: Response Listener
        Parameters: <Context>, <Name>, <ProgressDialog>
        Return: Response.Listener<String>
     */
    public Response.Listener<String> showShopMapLocationListener(final Context context, final String name, final ProgressDialog pd) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    jsonObject = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

                    double lat = jsonObject.getDouble("lat");
                    double lng = jsonObject.getDouble("lng");
                    double[] coordinates = {lat,lng};

                    Intent intent = new Intent(context, ShopMap.class);
                    intent.putExtra("name",name);
                    intent.putExtra("coordinates",coordinates);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, context.getString(R.string.invalidpostalcode_text), Toast.LENGTH_SHORT).show();
                }

                if (pd != null) {
                    pd.dismiss();
                }
            }
        };

        return listener;
    }

    /*
        Function: getErrorListener()
        Description: Error Listener
        Parameters: <Context>, <ProgressDialog>
        Return: Response.ErrorListener
     */
    public Response.ErrorListener getErrorListener(final Context context, final ProgressDialog pd) {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (pd != null) {
                    pd.dismiss();
                    Toast.makeText(context,context.getString(R.string.unabletoconnect_text),Toast.LENGTH_SHORT).show();
                }
            }
        };

        return errorListener;
    }
}
