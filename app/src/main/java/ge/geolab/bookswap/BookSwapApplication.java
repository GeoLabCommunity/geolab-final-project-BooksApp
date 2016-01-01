package ge.geolab.bookswap;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by dalkh on 31-Dec-15.
 */
public class BookSwapApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/bpg_dejavu_sans_0.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
