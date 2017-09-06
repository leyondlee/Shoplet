package Util;

import android.content.Context;
import android.content.SharedPreferences;

import Singleton.UserSingleton;

/**
 * Created by Leyond on 17/12/2016.
 */

public class UserUtil {
    /*
        Function: isLoggedin()
        Description: Check if user is logged in based on Singleton object
        Parameters: <Context>
        Return: boolean
     */
    public boolean isLoggedin(Context context) {
        UserSingleton userSingleton = UserSingleton.getInstance(context);
        boolean login = userSingleton.isLogin();

        return login;
    }

    /*
        Function: checkLogin()
        Description: Check if user is logged in, attempt to login if not
        Parameters: <Context>, <Show Dialog>
        Return: boolean
     */
    public void checkLogin(Context context, boolean showDialog) {
        AppUtil appUtil = new AppUtil();
        WebUtil webUtil = new WebUtil();

        UserSingleton userSingleton = UserSingleton.getInstance(context);
        String username = userSingleton.getUsername();
        String password = userSingleton.getPassword();

        if (username != null && password != null) {
            webUtil.login(context, username, password, showDialog, false);
        } else if (isLoggedin(context)) {
            userSingleton.setLogin(false);
            appUtil.getActivity(context).invalidateOptionsMenu();
        }
    }

    /*
        Function: saveCredentials()
        Description: Save credentials to SharedPreferences
        Parameters: <Context>, <Username>, <Password>
        Return: NIL
     */
    public void saveCredentials(Context context, String username, String password) {
        SharedPreferences sp = context.getSharedPreferences("User",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username",username);
        editor.putString("password",password);
        editor.commit();
    }

    /*
        Function: getCredentials()
        Description: Get credentials from SharedPreferences
        Parameters: <Context>
        Return: String[]
     */
    public String[] getCredentials(Context context) {
        SharedPreferences sp = context.getSharedPreferences("User",context.MODE_PRIVATE);
        String username = sp.getString("username",null);
        String password = sp.getString("password",null);

        String[] userinfo = null;
        if (username != null || password != null) {
            userinfo = new String[] {username,password};
        }

        return userinfo;
    }

    /*
        Function: removeCredentials()
        Description: Remove credentials from SharedPreferences
        Parameters: <Context>
        Return: NIL
     */
    public void removeCredentials(Context context) {
        SharedPreferences sp = context.getSharedPreferences("User",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("username");
        editor.remove("password");
        editor.commit();
    }
}
