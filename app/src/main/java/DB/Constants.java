package DB;

import android.provider.BaseColumns;

/**
 * Created by Zhong Yi on 2/2/2017.
 */

public interface Constants extends BaseColumns {
    String CATEGORY_TABLE_NAME = "Category";
    String CATEGORY_NAME = "name";
    String CATEGORY_ORDER_BY = CATEGORY_NAME + " ASC";

    String SHOP_TABLE_NAME = "Shop";
    String SHOP_NAME = "name";
    String SHOP_CONTACT = "contact";
    String SHOP_POSTALCODE = "postalcode";
    String SHOP_UNITNO = "unitno";
    String SHOP_CATEGORY = "category";
    String SHOP_DESCRIPTION = "description";
    String SHOP_ORDER_BY = SHOP_NAME + " ASC";
}
