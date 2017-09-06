package mapp.teamkcl.shoplet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import Util.AppUtil;
import Util.UserUtil;

/**
 * Created by Leyond on 11/1/2017.
 */

public class Settings extends AppCompatActivity {
    private AppUtil appUtil = new AppUtil();
    private UserUtil userUtil = new UserUtil();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        appUtil.setupToolbar(this);

        //Set close button on top left
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        //Start Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_content,new SettingsFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0,0);

        invalidateOptionsMenu();
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
}
