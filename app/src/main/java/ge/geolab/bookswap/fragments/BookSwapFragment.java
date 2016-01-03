package ge.geolab.bookswap.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.views.adapters.BookAdListAdapter;
import ge.geolab.bookswap.views.customViews.RecycleBinView;

/**
 * Created by dalkh on 25-Dec-15.
 */
public class BookSwapFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public BookSwapFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BookSwapFragment newInstance(int sectionNumber) {
        BookSwapFragment fragment = new BookSwapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    @Bind(R.id.list_of_ads) RecyclerView bookAdListView;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout refreshLayout;
    @BindString(R.string.list_array_url) String jsonArrayUrl;
    private ArrayList<Book> bookAdList;
    private RequestQueue requestQueue;
    private BookAdListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_swap, container, false);
        ButterKnife.bind(this,rootView);
        final int columns = getResources().getInteger(R.integer.gallery_columns);
        StaggeredGridLayoutManager gridLayoutManager=new StaggeredGridLayoutManager(columns,1);
        Context context=getActivity().getApplicationContext();
        requestQueue= Volley.newRequestQueue(context);
        bookAdListView.setLayoutManager(gridLayoutManager);
          bookAdList=new ArrayList<>();
        adapter=new BookAdListAdapter(context,bookAdList);
        bookAdListView.setAdapter(adapter);
        cacheJson();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                fetchJsonData(requestQueue, jsonArrayUrl, bookAdList, adapter, refreshLayout);

            }
        });
        /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
        return rootView;
    }
    private void cacheJson(){
        Cache cache = requestQueue.getCache();
        Cache.Entry entry = cache.get(jsonArrayUrl);

        if (entry != null) {
            try {
                String data = new String(entry.data, "UTF-8");
                Log.d("CACHE DATA", data);

                JSONArray jsonArray = new JSONArray(data);

                setData(jsonArray,bookAdList, adapter, refreshLayout);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            refreshLayout.setRefreshing(true);
            fetchJsonData(requestQueue, jsonArrayUrl, bookAdList, adapter, refreshLayout);
        }
    }
    private void fetchJsonData(RequestQueue requestQueue, String url,
                               final ArrayList<Book> list,
                               final BookAdListAdapter adapter,
                               final SwipeRefreshLayout refreshLayout){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(url,new Response.Listener<JSONArray>() {


            @Override
            public void onResponse(JSONArray jsonArray) {
                setData(jsonArray,list,adapter,refreshLayout);
            }
        },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Volley=>", "Error: " + error.getMessage());
                        Snackbar.make(bookAdListView,
                                "საჭიროა ინტერნეტთან კავშირი", Snackbar.LENGTH_LONG).show();
                        // hide the progress dialog
                        // hidepDialog();
                        refreshLayout.setRefreshing(false);
                    }
                });

        requestQueue.add(jsonArrayRequest);


    }
    private void setData(JSONArray jsonArray,
                         final ArrayList<Book> list,
                         final BookAdListAdapter adapter,
                         final SwipeRefreshLayout refreshLayout){
        list.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = null;
            try {
                obj = jsonArray.getJSONObject(i);

                Book bookObject=new Book();
                bookObject.setTitle(obj.getString("title"));
                bookObject.setFrontImageUrl(obj.getString("imgname"));
                list.add(bookObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

}
