package mapp.teamkcl.shoplet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Leyond on 24/12/2016.
 */

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    //Wait for 3 seconds
                    int waited = 0;
                    while (waited < 3000) {
                        sleep(100);
                        waited += 100;
                    }
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                } finally {
                    finish();
                    Intent i = new Intent();
                    String packageName = getPackageName();
                    i.setClassName(packageName,packageName + ".MainActivity");
                    startActivity(i);
                }
            }
        };

        splashThread.start();
    }
}
