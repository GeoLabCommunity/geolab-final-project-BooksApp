package ge.geolab.bookswap.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.views.adapters.BookAdListAdapter;
import ge.geolab.bookswap.views.customListeners.ItemClickSupport;

public class SearchResultsActivity extends AppCompatActivity {
    @Bind(R.id.list_of_query)
    RecyclerView queryListView;
    @BindString(R.string.search_query_url) String queryUrl;
    private BookAdListAdapter adapter;
    private ArrayList<Book> bookAdList;
    private RequestQueue requestQueue;
    private Context context;
    private Snackbar loadingSnackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_out_top);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=this;
        final int columns = getResources().getInteger(R.integer.gallery_columns);
        final StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(columns, 1);
        queryListView.setLayoutManager(gridLayoutManager);
        bookAdList = new ArrayList<>();
        adapter = new BookAdListAdapter(this, bookAdList);
        queryListView.setAdapter(adapter);

        handleIntent(getIntent());

        ItemClickSupport.addTo(queryListView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("book", bookAdList.get(position));
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow

            getQueryData(query,requestQueue,queryUrl,bookAdList,adapter);
        }
    }

    private void getQueryData(final String newText, RequestQueue requestQueue, String url,
                              final ArrayList<Book> list,
                              final BookAdListAdapter adapter) {
        requestQueue= Volley.newRequestQueue(this);
        if(!newText.isEmpty()){
            loadingSnackbar=Snackbar.make(queryListView,getResources().getString(R.string.data_is_loading),Snackbar.LENGTH_INDEFINITE);
            loadingSnackbar.show();
        StringRequest jsonArrayRequest = new StringRequest(Request.Method.POST, queryUrl + newText, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                list.clear();
                JsonParser jsonParser = new JsonParser();
                try {
                    JsonArray jsonArray = jsonParser.parse(response).getAsJsonArray();
                    for (int i = 0; i < jsonArray.size(); i++) {

                        JsonObject obj = jsonArray.get(i).getAsJsonObject();
                        Book bookObject = new Book();
                        bookObject.setCategory(obj.get("category_id").getAsString());
                        bookObject.setAdType(obj.get("type").getAsString());
                        bookObject.setCondition(obj.get("state").getAsString());
                        bookObject.setAuthor(obj.get("author").getAsString());
                        bookObject.setTitle(obj.get("title").getAsString());
                        bookObject.setLocation(obj.get("location").getAsString());
                        bookObject.setExchangeItem(obj.get("item").getAsString());
                        bookObject.setDescription(obj.get("description").getAsString());
                        bookObject.seteMail(obj.get("email").getAsString());
                        bookObject.setMobileNum(obj.get("mobile").getAsString());
                        JsonArray imgArrayJSON = obj.get("img").getAsJsonArray();
                        ArrayList<String> imgArray = new ArrayList<>();
                        for (int k = 0; k < imgArrayJSON.size(); k++) {


                            if (imgArrayJSON.get(k).isJsonNull()) {
                                imgArray.add("null");
                            } else {
                                imgArray.add(imgArrayJSON.get(k).getAsString());
                                if(k==0){
                                    bookObject.setFrontImageUrl(imgArrayJSON.get(k).getAsString());
                                }

                            }


                        }
                        bookObject.setPictures(imgArray);
                        bookObject.setId(obj.get("user_id").getAsString());

                        list.add(bookObject);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }

                loadingSnackbar.dismiss();
                adapter.notifyDataSetChanged();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley=>", "Error: " + error.getMessage());
                loadingSnackbar.setDuration(Snackbar.LENGTH_LONG);
                loadingSnackbar.setText(getResources().getString(R.string.fb_error_snackbar_msg));

                // hide the progress dialog
                // hidepDialog();

            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("search", newText);


                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
            requestQueue.add(jsonArrayRequest);
    }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        final CursorAdapter suggestionAdapter = new SimpleCursorAdapter(this,
                R.layout.search_suggestion_item,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);
        final List<Book> suggestions = new ArrayList<>();

        searchView.setSuggestionsAdapter(suggestionAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                searchView.setQuery(suggestions.get(position).getTitle(), false);
                //searchView.clearFocus();
         /*       Intent intent=new Intent(MainActivity.this,DetailsActivity.class);
                intent.putExtra("book",suggestions.get(position));
                startActivity(intent);*/
                //doSearch(suggestions.get(position));
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                RequestQueue requestQueue= Volley.newRequestQueue(context);
                if(!newText.isEmpty()){
                    StringRequest jsonArrayRequest=new StringRequest(Request.Method.POST,queryUrl+newText,new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            suggestions.clear();
                            JsonParser jsonParser=new JsonParser();
                            try {
                                JsonArray jsonArray = jsonParser.parse(response).getAsJsonArray();
                                for (int i = 0; i < jsonArray.size(); i++) {

                                    JsonObject obj = jsonArray.get(i).getAsJsonObject();
                                    Book bookObject = new Book();
                                    bookObject.setTitle(obj.get("title").getAsString());

                                    suggestions.add(bookObject);
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }

                            String[] columns = {
                                    BaseColumns._ID,
                                    SearchManager.SUGGEST_COLUMN_TEXT_1,
                                    SearchManager.SUGGEST_COLUMN_INTENT_DATA
                            };

                            MatrixCursor cursor = new MatrixCursor(columns);

                            for (int k = 0; k < suggestions.size(); k++) {
                                String[] tmp = {Integer.toString(k), suggestions.get(k).getTitle(), suggestions.get(k).getTitle()};

                                cursor.addRow(tmp);
                            }
                            suggestionAdapter.swapCursor(cursor);
                        }

                    }  , new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("Volley=>", "Error: " + error.getMessage());
                            // hide the progress dialog
                            // hidepDialog();

                        }

                    }){
                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("search",newText);


                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("Content-Type","application/x-www-form-urlencoded");
                            return params;
                        }
                    };
                    requestQueue.add(jsonArrayRequest);}
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}
