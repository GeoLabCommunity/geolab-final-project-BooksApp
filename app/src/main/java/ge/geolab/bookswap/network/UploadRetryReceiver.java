package ge.geolab.bookswap.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;

import ge.geolab.bookswap.models.Book;

/**
 * Created by dalkh on 01-Jan-16.
 */
public class UploadRetryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
             Book book= (Book) intent.getSerializableExtra("book");
              ArrayList<String> deletedImages= (ArrayList<String>) intent.getSerializableExtra("deletedImages");
            /* ArrayList<String> picList=new ArrayList<>();
             picList=intent.getExtras().getStringArrayList("picList");*/
            // book.setPictures(picList);
             //System.out.println("&#&&#&#&#&"+picList.get(0));
             new UploadFileToServer(context,book,deletedImages).execute();
    }
}
