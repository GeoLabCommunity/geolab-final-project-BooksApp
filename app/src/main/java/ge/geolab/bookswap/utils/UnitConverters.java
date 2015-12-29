package ge.geolab.bookswap.utils;

import android.content.res.Resources;

/**
 * Created by dalkh on 29-Dec-15.
 */
public class UnitConverters {

    public static int getPx(int dimensionDp, Resources res) {
        float density = res.getDisplayMetrics().density;
        return (int) (dimensionDp * density + 0.5f);
    }
}
