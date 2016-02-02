package ge.geolab.bookswap.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

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
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.models.GroupItem;
import ge.geolab.bookswap.views.adapters.ExpandableListAdapter;
import ge.geolab.bookswap.views.customViews.AnimatedExpandableListView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OffersActivity extends AppCompatActivity {
    @Bind(R.id.expandable_list) AnimatedExpandableListView listView;
    @BindString(R.string.get_my_offers_url) String offersUrl;
    private ExpandableListAdapter adapter;
    private List<GroupItem> headerItemsList = new ArrayList<GroupItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter=new ExpandableListAdapter(this);
        adapter.setData(headerItemsList);
        listView.setAdapter(adapter);
        drawList(offersUrl,headerItemsList,adapter);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;

            }


        });
        setChildClickListeners();

    }
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
     private void drawList(String url, final List<GroupItem> headerItemsList, final ExpandableListAdapter adapter){

         final RequestQueue requestQueue = Volley.newRequestQueue(this);

         //refreshLayout.setRefreshing(true);
         final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url+Profile.getCurrentProfile().getId(), new Response.Listener<JSONArray>() {

             @Override
             public void onResponse(JSONArray jsonArray) {
                 headerItemsList.clear();

                // refreshLayout.setRefreshing(false);
                 for (int i = 0; i < jsonArray.length(); i++) {
                     JSONObject obj = null;
                     GroupItem parentItem = new GroupItem();
                     try {

                         obj = jsonArray.getJSONObject(i);
                         parentItem.setTitle(obj.getString("title"));
                         parentItem.setCategoryId(obj.getString("category_id"));
                         ArrayList<Book> childList=new ArrayList<>();
                         JSONArray childJsonArray=obj.getJSONArray("offers");
                         for (int k = 0; k < childJsonArray.length(); k++) {
                             JSONObject childObject=childJsonArray.getJSONObject(k);
                             Book bookObject = new Book();
                             bookObject.setServer_id(childObject.getString("id"));
                             bookObject.setCategory(childObject.getString("category_id"));
                             bookObject.setAdType(childObject.getString("type"));
                             bookObject.setCondition(childObject.getString("state"));
                             bookObject.setAuthor(childObject.getString("author"));
                             bookObject.setTitle(childObject.getString("title"));
                             bookObject.setLocation(childObject.getString("location"));
                             bookObject.setExchangeItem(childObject.getString("item"));
                             bookObject.setDescription(childObject.getString("description"));
                             bookObject.seteMail(childObject.getString("email"));
                             bookObject.setMobileNum(childObject.getString("mobile"));
                             JSONArray imgArrayJSON = childObject.getJSONArray("img2");
                             ArrayList<String> imgArray = new ArrayList<>();
                             for (int j = 0; j < imgArrayJSON.length(); j++) {
                                 imgArray.add(imgArrayJSON.getString(j));
                                 if (j == 0) {
                                     bookObject.setFrontImageUrl(imgArrayJSON.getString(0));
                                 }
                             }
                             bookObject.setPictures(imgArray);
                             bookObject.setId(childObject.getString("user_id"));

                             childList.add(bookObject);
                         }

                         parentItem.setItems(childList);
                         headerItemsList.add(parentItem);



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
                        // refreshLayout.setRefreshing(false);
                         // hide the progress dialog
                         // hidepDialog();

                     }
                 });

         requestQueue.add(jsonArrayRequest);
     }
    private void setChildClickListeners(){
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Book book=headerItemsList.get(groupPosition).getItems().get(childPosition);
                Intent intent=new Intent(OffersActivity.this,DetailsActivity.class);
                intent.putExtra("book",book);
                startActivity(intent);
                return false;
            }
        });
    }
}
