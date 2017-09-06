package mapp.teamkcl.shoplet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import Singleton.UserSingleton;
import Util.AppUtil;
import Util.UserUtil;
import Util.WebUtil;

/**
 * Created by Leyond on 8/1/2017.
 */

public class Account extends AppCompatActivity implements OnClickListener {
    private AppUtil appUtil = new AppUtil();
    private UserUtil userUtil = new UserUtil();
    private WebUtil webUtil = new WebUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        appUtil.setupToolbar(this);

        //Set close button on top left
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        //Get and display username from Singleton class
        String username = UserSingleton.getInstance(this).getUsername();
        TextView usernameTV = (TextView) findViewById(R.id.account_username);
        usernameTV.setText(username);

        Button button = (Button) findViewById(R.id.account_button);
        button.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0,0);

        invalidateOptionsMenu();

        if (!userUtil.isLoggedin(this)) {
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
            case R.id.account_button: {
                //Get passwords and pass to updateAccount function
                EditText currentpasswordTV = (EditText) findViewById(R.id.account_currentpassword);
                EditText newpasswordTV = (EditText) findViewById(R.id.account_newpassword);
                EditText confirmnewpasswordTV = (EditText) findViewById(R.id.account_confirmnewpassword);

                String[] passwords = {currentpasswordTV.getText().toString(),newpasswordTV.getText().toString(),confirmnewpasswordTV.getText().toString()};
                webUtil.updateAccount(this,passwords,true);

                break;
            }

            default: {
                appUtil.onClickDefault(this,v);
                break;
            }
        }
    }
}
