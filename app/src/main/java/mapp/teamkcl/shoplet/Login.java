package mapp.teamkcl.shoplet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import Singleton.UserSingleton;
import Util.AppUtil;
import Util.UserUtil;
import Util.WebUtil;

/**
 * Created by Leyond on 17/12/2016.
 */

public class Login extends AppCompatActivity implements OnClickListener {
    static final int REGISTER_REQUEST = 0;

    private AppUtil appUtil = new AppUtil();
    private UserUtil userUtil = new UserUtil();
    private WebUtil webUtil = new WebUtil();

    private EditText usernameET;
    private EditText passwordET;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        appUtil.setupToolbar(this);

        //Set close button on top left
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        usernameET = (EditText) findViewById(R.id.login_username);
        passwordET = (EditText) findViewById(R.id.login_password);

        Button login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(this);

        //Get username and password from Singleton class
        UserSingleton userSingleton = UserSingleton.getInstance(this);
        String username = userSingleton.getUsername();
        String password = userSingleton.getPassword();

        if (username != null) {
            //Set username if have
            usernameET.setText(username);
        }

        if (password != null) {
            //Set password if have
            passwordET.setText(password);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0,0);

        invalidateOptionsMenu();

        //Check if user is logged in. If logged in, end Activity
        if (userUtil.isLoggedin(this)) {
            finish();
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
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REGISTER_REQUEST) {
            if (resultCode == RESULT_OK) {
                //Get and set username and password from Intent
                Bundle bundle = data.getExtras();
                username = bundle.getString("username","");
                password = bundle.getString("password","");

                usernameET.setText(username);
                passwordET.setText(password);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button: {
                username = usernameET.getText().toString();
                password = passwordET.getText().toString();

                webUtil.login(this,username,password,true,true);
                break;
            }

            case R.id.signup_button: {
                //Start Register Activity and wait for result
                appUtil.startActivityForResult(this,Register.class,REGISTER_REQUEST,null);
                break;
            }

            default: {
                appUtil.onClickDefault(this,v);
                break;
            }
        }
    }
}
