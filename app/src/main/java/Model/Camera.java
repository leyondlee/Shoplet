package Model;

import android.content.*;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Leyond on 14/1/2017.
 */

/*
    References:
        Taking Photos Simply - https://developer.android.com/training/camera/photobasics.html
        CameraPhoto - https://github.com/kosalgeek/PhotoUtil/blob/master/PhotoUtil/app/src/main/java/com/kosalgeek/android/photoutil/CameraPhoto.java
 */
public class Camera {
    private Context context;
    private String photoPath;

    public Camera(Context context) {
        this.context = context;
    }

    public String getPhotoPath() {
        return this.photoPath;
    }

    public Intent getIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI;
                if (Build.VERSION.SDK_INT < 24) {
                    photoURI = Uri.fromFile(photoFile);
                } else {
                    photoURI = FileProvider.getUriForFile(context, "com.example.android.fileprovider", photoFile);
                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
        }

        return intent;
    }

    private File createImageFile() {
        File image = null;

        try {
            //Create unique filename
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(imageFileName,".jpg",storageDir);

            //Save path to Camera object
            photoPath = image.getAbsolutePath();
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return image;
    }

    public void addPicToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
