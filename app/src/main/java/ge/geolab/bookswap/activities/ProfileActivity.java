package ge.geolab.bookswap.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import co.dift.ui.SwipeToAction;
import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.utils.UnitConverters;
import ge.geolab.bookswap.views.adapters.ProfileListAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity {
    @Bind(R.id.profile_book_list) RecyclerView bookListView;
    @BindString(R.string.list_array_url) String jsonUrl;
    private ProfileListAdapter profileListAdapter;
    private ArrayList<Book> bookList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           bookList=new ArrayList<>();
        profileListAdapter=new ProfileListAdapter(bookList,this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        bookListView.setLayoutManager(layoutManager);
        bookListView.setAdapter(profileListAdapter);
        getSuggestionsData(jsonUrl, Profile.getCurrentProfile().getId(),bookList,profileListAdapter);
        final SwipeToAction swipeToAction = new SwipeToAction(bookListView, new SwipeToAction.SwipeListener<Book>() {
            @Override
            public boolean swipeLeft(final Book itemData) {
                final int pos = removeBook(itemData);
                return true; //true will move the front view to its starting position
            }

            @Override
            public boolean swipeRight(Book itemData) {
                //do something
                return true;
            }

            @Override
            public void onClick(Book itemData) {
                //do something
            }

            @Override
            public void onLongClick(Book itemData) {
                //do something
            }
        });

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void getSuggestionsData(String url, String id, final ArrayList<Book> list, final ProfileListAdapter adapter){
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url+"0/user_id/"+id, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray jsonArray) {



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = null;



                                try {

                                    obj = jsonArray.getJSONObject(i);
                                    Book bookObject = new Book();
                                    bookObject.setCategory(obj.getString("category_id"));
                                    bookObject.setAdType(obj.getString("type"));
                                    bookObject.setCondition(obj.getString("state"));
                                    bookObject.setAuthor(obj.getString("author"));
                                    bookObject.setTitle(obj.getString("title"));
                                    bookObject.setLocation(obj.getString("location"));
                                    bookObject.setExchangeItem(obj.getString("item"));
                                    bookObject.setDescription(obj.getString("description"));
                                    bookObject.seteMail(obj.getString("email"));
                                    bookObject.setMobileNum(obj.getString("mobile"));
                                    JSONArray imgArrayJSON = obj.getJSONArray("img");
                                    ArrayList<String> imgArray = new ArrayList<>();
                                    for (int k = 0; k < imgArrayJSON.length(); k++) {
                                        imgArray.add(imgArrayJSON.getString(k));
                                        if (k == 0) {
                                            bookObject.setFrontImageUrl(imgArrayJSON.getString(0));
                                        }
                                    }
                                    bookObject.setPictures(imgArray);
                                    bookObject.setId(obj.getString("user_id"));
                                    list.add(bookObject);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                    }
                    adapter.notifyDataSetChanged();

                    }



        },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Volley=>", "Error: " + error.getMessage());

                        // hide the progress dialog
                        // hidepDialog();

                    }
                });
        //jsonArrayRequest.setTag("REQUEST");
        requestQueue.add(jsonArrayRequest);

    }
    private int removeBook(Book book) {
        int pos = bookList.indexOf(book);
       bookList.remove(book);
       profileListAdapter.notifyItemRemoved(pos);
        return pos;
    }
}
