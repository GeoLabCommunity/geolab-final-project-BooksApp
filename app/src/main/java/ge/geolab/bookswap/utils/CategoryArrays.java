package ge.geolab.bookswap.utils;

import android.content.Context;

import java.util.HashMap;

import ge.geolab.bookswap.R;

/**
 * Created by dalkh on 07-Jan-16.
 */
public class CategoryArrays {
    private Context context;
     public static String[] categories={"","მხატვრული ლიტერატურა","სკოლის წიგნები","საუნივერსიტეტო წიგნები","ლექსიკონები",
             "კონსპექტები","კომიქსები"};
     public static String[] conditions={"ახალი","ძველი"};

    public static String[] adTypes={"","ვცვლი","ვეძებ"};
    public static int[] categoryIcons={0,R.drawable.ic_quill,R.drawable.ic_school,
            R.drawable.ic_university,R.drawable.ic_lexicon,R.drawable.ic_lect_notes,R.drawable.ic_comics};
    public  CategoryArrays(Context context){
        this.context=context;
    }

}
