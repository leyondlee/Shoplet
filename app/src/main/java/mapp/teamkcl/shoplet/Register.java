package mapp.teamkcl.shoplet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import Util.*;

/**
 * Created by Leyond on 17/12/2016.
 */

public class Register extends AppCompatActivity implements OnClickListener {
    private AppUtil appUtil = new AppUtil();
    private UserUtil userUtil = new UserUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        appUtil.setupToolbar(this);

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0,0);

        invalidateOptionsMenu();

        //If user is logged in, end Activity
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button: {
                //Get details from Views
                EditText usernameET = (EditText) findViewById(R.id.register_username);
                EditText passwordET = (EditText) findViewById(R.id.register_password);
                EditText confirmpasswordET = (EditText) findViewById(R.id.register_confirmpassword);

                String username = usernameET.getText().toString();
                String password = passwordET.getText().toString();
                String confirmpassword = confirmpasswordET.getText().toString();

                //Send request to server
                WebUtil webUtil = new WebUtil();
                webUtil.register(this,username,password,confirmpassword,true);

                break;
            }

            default: {
                appUtil.onClickDefault(this,v);
                break;
            }
        }
    }
}
