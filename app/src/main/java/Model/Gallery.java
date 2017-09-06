package Model;

import android.content.*;
import android.net.Uri;
import android.os.Build;

import Util.*;

/**
 * Created by Leyond on 14/1/2017.
 */

//Reference - https://github.com/kosalgeek/PhotoUtil/blob/master/PhotoUtil/app/src/main/java/com/kosalgeek/android/photoutil/GalleryPhoto.java
public class Gallery {
    private Context context;
    private Uri photoUri;

    public Gallery(Context context) {
        this.context = context;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public Intent getIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        return Intent.createChooser(intent, "");
    }

    public String getPath() {
        RealPathUtil realPathUtil = new RealPathUtil();

        String path;
        if (Build.VERSION.SDK_INT < 11) { // SDK < API11
            path = realPathUtil.getRealPathFromURI_BelowAPI11(context, photoUri);
        }  else if (Build.VERSION.SDK_INT < 19) { // SDK >= 11 && SDK < 19
            path = realPathUtil.getRealPathFromURI_API11to18(context, photoUri);
        } else { // SDK > 19 (Android 4.4)
            path = realPathUtil.getRealPathFromURI_API19(context, photoUri);
        }

        return path;
    }
}
