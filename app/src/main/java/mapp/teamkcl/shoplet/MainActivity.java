package mapp.teamkcl.shoplet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import Util.AppUtil;
import Util.UserUtil;
import Util.WebUtil;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private AppUtil appUtil = new AppUtil();
    private UserUtil userUtil = new UserUtil();
    private WebUtil webUtil = new WebUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appUtil.setupToolbar(this);

        //Set default app icon as it is the first page
        getSupportActionBar().setIcon(R.mipmap.icon);

        //Just in case, remove all existing views
        LinearLayout container = (LinearLayout) findViewById(R.id.main_container);
        container.removeAllViews();

        //Get and load categories from server
        webUtil.getCategories(this,container,R.layout.categoryrow,true);

        //Check if auto login is enabled and login if it is
        SharedPreferences sp = getSharedPreferences("Settings",MODE_PRIVATE);
        boolean autologin = sp.getBoolean("autologin",true);
        if (autologin) {
            userUtil.checkLogin(this, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0,0);

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        appUtil.createMenuItems(this,menu,false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        appUtil.onMenuItemSelected(this,item);
        return true;
    }

    @Override
    public void onClick(View v) {
        TextView textView;
        if (v.getId() == R.id.categoryrow_box1) {
            //Left box
            textView = (TextView) v.findViewById(R.id.categoryrow_name1);
        } else {
            //Right box
            textView = (TextView) v.findViewById(R.id.categoryrow_name2);
        }

        //Get category from View
        String category = textView.getText().toString();
        Map<String,String> params = new HashMap<>();
        params.put("Category",category);

        //Load shops based on category selected
        appUtil.startActivity(this,Shops.class,false,params);
    }
}
