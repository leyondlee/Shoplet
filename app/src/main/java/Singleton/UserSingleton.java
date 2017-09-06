package Singleton;

import android.content.Context;

import Util.UserUtil;

/**
 * Created by Leyond on 26/1/2017.
 */

public class UserSingleton {
    private static UserSingleton mInstance;
    private Context context;
    private String username;
    private String password;
    private boolean login = false;

    private UserSingleton(Context context, String username, String password) {
        this.context = context;
        this.username = username;
        this.password = password;
    }

    public void save() {
        UserUtil userUtil = new UserUtil();
        userUtil.saveCredentials(context,username,password);
    }

    public void remove() {
        UserUtil userUtil = new UserUtil();
        userUtil.removeCredentials(context);
        username = null;
        password = null;
        login = false;
    }

    public static synchronized UserSingleton getInstance(Context context) {
        if (mInstance == null) {
            context = context.getApplicationContext();

            UserUtil userUtil = new UserUtil();
            String[] credentials = userUtil.getCredentials(context);

            String username = null;
            String password = null;
            if (credentials != null) {
                username = credentials[0];
                password = credentials[1];
            }

            mInstance = new UserSingleton(context,username,password);
        }

        return mInstance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }
}
