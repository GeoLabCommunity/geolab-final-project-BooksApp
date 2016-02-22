package ge.geolab.bookswap.utils;

/**
 * Created by dalkh on 21-Feb-16.
 */
public class TextTransformer {
    public static String ellipsize(String text,int length){
        if(text.length()<=length){
            return text;
        }
        if(text.charAt(length-1)==' '){
            length-=1;
        }

        return text.substring(0,length)+"…";
    }
}
