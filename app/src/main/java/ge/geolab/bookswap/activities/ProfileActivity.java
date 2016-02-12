package ge.geolab.bookswap.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
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
import ge.geolab.bookswap.utils.TypeFaceSpan;
import ge.geolab.bookswap.utils.UnitConverters;
import ge.geolab.bookswap.views.adapters.ProfileListAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity {
    @Bind(R.id.profile_book_list) RecyclerView bookListView;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindString(R.string.list_array_url) String jsonUrl;
    @BindString(R.string.remove_entry_url) String removeUrl;
    private ProfileListAdapter profileListAdapter;
    private ArrayList<Book> bookList;
    Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SpannableString title= new SpannableString(getResources().getString(R.string.title_activity_profile));
        title.setSpan(new TypeFaceSpan(this, "bpg_nino_mtavruli_bold.ttf"), 0, title.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setTitle(title);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,AddBookActivity.class);
                startActivity(intent);
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
                new AlertDialog.Builder(context)
                        .setTitle("")
                        .setMessage("გსურთ წაშალოთ განცხადება ?")
                        .setCancelable(false)
                        .setPositiveButton("კი", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeBook(itemData);

                            }
                        })
                        .setNegativeButton("არა", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();

                return true; //true will move the front view to its starting position
            }

            @Override
            public boolean swipeRight(Book itemData) {
                //do something
                Intent intent=new Intent(ProfileActivity.this,EditActivity.class);
                intent.putExtra("book",itemData);
                startActivity(intent);
                return true;
            }

            @Override
            public void onClick(Book itemData) {
                //do something
                Intent intent=new Intent(ProfileActivity.this,DetailsActivity.class);
                intent.putExtra("book",itemData);
                startActivity(intent);
            }

            @Override
            public void onLongClick(Book itemData) {
                //do something
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSuggestionsData(jsonUrl, Profile.getCurrentProfile().getId(),bookList,profileListAdapter);
            }
        });
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void getSuggestionsData(String url, String id, final ArrayList<Book> list, final ProfileListAdapter adapter){
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        refreshLayout.setRefreshing(true);
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url+"0/user_id/"+id, new Response.Listener<JSONArray>() {


            @Override
            public void onResponse(JSONArray jsonArray) {
                       list.clear();

                     refreshLayout.setRefreshing(false);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = null;



                                try {

                                    obj = jsonArray.getJSONObject(i);
                                    Book bookObject = new Book();
                                    bookObject.setServer_id(obj.getString("id"));
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
                         refreshLayout.setRefreshing(false);

                        //Retry sending request
                        displaySnackbar(getResources().getString(R.string.fb_error_snackbar_msg), "RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getSuggestionsData(jsonUrl, Profile.getCurrentProfile().getId(),bookList,profileListAdapter);
                            }
                        });


                    }
                });
        //jsonArrayRequest.setTag("REQUEST");
        requestQueue.add(jsonArrayRequest);

    }
    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(bookListView, text, Snackbar.LENGTH_INDEFINITE)
                .setAction(actionName, action);

        snack.show();
    }

    private void removeBook(Book book) {
        int pos = bookList.indexOf(book);
        sendRemoveRequest(book,pos);

    }
    private void addBook(int pos, Book book) {
        bookList.add(pos, book);
        profileListAdapter.notifyItemInserted(pos);
    }
    private void sendRemoveRequest(final Book book, final int position){
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        final StringRequest jsonArrayRequest = new StringRequest(Request.Method.GET,removeUrl+book.getServer_id(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                bookList.remove(book);
                profileListAdapter.notifyItemRemoved(position);
                Snackbar.make(bookListView,book.getTitle()+" წაიშალა",Snackbar.LENGTH_LONG).show();

            }



        },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Volley=>", "Error: " + error.getMessage());
                        displaySnackbar(getResources().getString(R.string.fb_error_snackbar_msg), "RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeBook(book);
                            }
                        });

                    }
                });
        requestQueue.add(jsonArrayRequest);
    }
}
