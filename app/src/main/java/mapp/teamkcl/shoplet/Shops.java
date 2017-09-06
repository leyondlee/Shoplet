package mapp.teamkcl.shoplet;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import Util.AppUtil;
import Util.WebUtil;

/**
 * Created by Leyond on 14/1/2017.
 */

public class Shops extends AppCompatActivity {
    private AppUtil appUtil = new AppUtil();
    private WebUtil webUtil = new WebUtil();

    private boolean firstRun = true;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shops);

        appUtil.setupToolbar(this);

        //Get category from Intent and set as title
        category = getIntent().getStringExtra("Category");
        setTitle(category);

        //Add listener to swipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.shops_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        refresh();
        firstRun = false;
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
        appUtil.onMenuItemSelected(this, item);

        return true;
    }

    private void refresh() {
        //Refresh shops
        listView = (ListView) findViewById(R.id.shops_listview);
        webUtil.getAndDisplayShops(this,listView,category,swipeRefreshLayout,firstRun);
    }
}
