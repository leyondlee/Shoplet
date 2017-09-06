package DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static DB.Constants.CATEGORY_NAME;
import static DB.Constants.CATEGORY_TABLE_NAME;
import static DB.Constants.SHOP_CATEGORY;
import static DB.Constants.SHOP_CONTACT;
import static DB.Constants.SHOP_DESCRIPTION;
import static DB.Constants.SHOP_NAME;
import static DB.Constants.SHOP_POSTALCODE;
import static DB.Constants.SHOP_TABLE_NAME;
import static DB.Constants.SHOP_UNITNO;

/**
 * Created by Zhong Yi on 2/2/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Shoplet.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + CATEGORY_TABLE_NAME + " ("
                + CATEGORY_NAME + " TEXT PRIMARY KEY);");

        sqLiteDatabase.execSQL("CREATE TABLE " + SHOP_TABLE_NAME + " ("
                + SHOP_NAME + " TEXT PRIMARY KEY, "
                + SHOP_CONTACT + " TEXT NOT NULL, "
                + SHOP_POSTALCODE + " TEXT NOT NULL, "
                + SHOP_UNITNO + " TEXT NOT NULL, "
                + SHOP_CATEGORY + " TEXT NOT NULL, "
                + SHOP_DESCRIPTION + " TEXT NULL, "
                + "FOREIGN KEY (" + SHOP_CATEGORY + ") REFERENCES " + CATEGORY_TABLE_NAME + "(" + CATEGORY_NAME + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SHOP_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
