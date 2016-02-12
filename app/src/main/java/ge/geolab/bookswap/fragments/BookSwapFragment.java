package ge.geolab.bookswap.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import de.greenrobot.event.EventBus;
import ge.geolab.bookswap.R;
import ge.geolab.bookswap.activities.DetailsActivity;
import ge.geolab.bookswap.events.ClearSavedInstanceEvent;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.utils.ConnectionDetector;
import ge.geolab.bookswap.views.adapters.BookAdListAdapter;
import ge.geolab.bookswap.views.customListeners.ItemClickSupport;
import ge.geolab.bookswap.views.customViews.RecycleBinView;
import icepick.Icepick;
import icepick.State;
import icepick.processor.IcepickProcessor;

/**
 * Created by dalkh on 25-Dec-15.
 */
public class BookSwapFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String CATEGORY_ID = "category_id";

    public BookSwapFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BookSwapFragment newInstance(int sectionNumber, int categoryId) {
        BookSwapFragment fragment = new BookSwapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putInt(CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Bind(R.id.list_of_ads)
    RecyclerView bookAdListView;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindString(R.string.list_array_url)
    String jsonArrayUrl;
    @BindString(R.string.data_is_loading)
    String dataIsLoadingMessage;
    @BindString(R.string.fb_error_snackbar_msg)
    String connectionErrorMessage;
    @State
    ArrayList<Book> bookAdList = new ArrayList<>();
    private RequestQueue requestQueue;
    private BookAdListAdapter adapter;
    @State
    String lastItemIdInJson = "0";
    Context context;
    Snackbar pagingSnackbar;
    Snackbar errorSnackbar;
    int adTypeIndex = 1;
    @State
    int categoryId = 0;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Icepick.restoreInstanceState(this, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_swap, container, false);
        ButterKnife.bind(this, rootView);
        context = getActivity().getApplicationContext();
        adTypeIndex = getArguments().getInt(ARG_SECTION_NUMBER);
        final int columns = getResources().getInteger(R.integer.gallery_columns);
        final StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(columns, 1);
        requestQueue = Volley.newRequestQueue(context);

        bookAdListView.setLayoutManager(gridLayoutManager);
        adapter = new BookAdListAdapter(context, bookAdList);
        bookAdListView.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                fetchJsonData(requestQueue, parseUrl(jsonArrayUrl, lastItemIdInJson, categoryId, adTypeIndex), bookAdList, adapter, refreshLayout);

                pagingSnackbar.setActionTextColor(Color.RED).setAction("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestQueue.cancelAll("REQUEST");
                        refreshLayout.setRefreshing(false);
                    }
                });
                pagingSnackbar.show();
            }
        };
        bookAdListView.addOnScrollListener(scrollListener);
        if (categoryId == 0 && savedInstanceState == null) {
            cacheJson();
        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                lastItemIdInJson = "0";
                fetchJsonData(requestQueue, parseUrl(jsonArrayUrl, lastItemIdInJson, categoryId, adTypeIndex), bookAdList, adapter, refreshLayout);
                scrollListener.reset();
            }
        });
        // cacheJson();

        ItemClickSupport.addTo(bookAdListView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // do it
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("book", bookAdList.get(position));
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pagingSnackbar = Snackbar.make(bookAdListView, dataIsLoadingMessage, Snackbar.LENGTH_INDEFINITE);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    public void onEvent(ClearSavedInstanceEvent event) {
        if (event.isCategorySelected) {
            lastItemIdInJson = "0";
            bookAdList.clear();
            categoryId = event.categoryId;
            cacheJson();
            scrollListener.reset();
        }
    }

    private String parseUrl(String url, String lastId, int categoryId, int typeId) {

        return url + lastId + "/category_id/" + categoryId + "/type/" + typeId;
    }

    private void cacheJson() {

        String url = parseUrl(jsonArrayUrl, lastItemIdInJson, categoryId, adTypeIndex);
        Cache cache = requestQueue.getCache();
        Cache.Entry entry = cache.get(url);

        if (entry != null && !ConnectionDetector.isConnectingToInternet(context)) {
            try {
                String data = new String(entry.data, "UTF-8");
                Log.d("CACHE DATA", data);

                JSONArray jsonArray = new JSONArray(data);

                setData(jsonArray, bookAdList, adapter, refreshLayout);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            fetchJsonData(requestQueue, url, bookAdList, adapter, refreshLayout);
        }
    }

    private void fetchJsonData(final RequestQueue requestQueue, String url,
                               final ArrayList<Book> list,
                               final BookAdListAdapter adapter,
                               final SwipeRefreshLayout refreshLayout) {

        refreshLayout.setRefreshing(true);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray jsonArray) {

                setData(jsonArray, list, adapter, refreshLayout);
                pagingSnackbar.dismiss();
            }
        },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Volley=>", "Error: " + error.getMessage());
                        refreshLayout.setRefreshing(false);
                        pagingSnackbar.dismiss();
                        /*errorSnackbar.setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fetchJsonData(requestQueue, parseUrl(jsonArrayUrl, lastItemIdInJson, categoryId, adTypeIndex), bookAdList, adapter, refreshLayout);
                            }
                        });
                        errorSnackbar.show();
*/
                        displaySnackbar(connectionErrorMessage, "retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fetchJsonData(requestQueue, parseUrl(jsonArrayUrl, lastItemIdInJson, categoryId, adTypeIndex), bookAdList, adapter, refreshLayout);
                            }
                        });
                    }
                });
        jsonArrayRequest.setTag("REQUEST");
        requestQueue.add(jsonArrayRequest);


    }
    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(bookAdListView, text, Snackbar.LENGTH_INDEFINITE)
                .setAction(actionName, action);

        snack.show();
    }

    private void setData(JSONArray jsonArray,
                         final ArrayList<Book> list,
                         final BookAdListAdapter adapter,
                         final SwipeRefreshLayout refreshLayout) {
        //this is called if we want to refresh list
        if (lastItemIdInJson.equals("0")) {
            list.clear();
            adapter.notifyDataSetChanged();
        }
        int curSize = adapter.getItemCount();
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
                bookObject.setServer_id(obj.getString("id"));
                lastItemIdInJson = obj.getString("id");
                list.add(bookObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        // For efficiency purposes, notify the adapter of only the elements that got changed
        // curSize will equal to the index of the first element inserted because the list is 0-indexed

        adapter.notifyItemRangeInserted(curSize, list.size() - 1);


        refreshLayout.setRefreshing(false);
    }

    public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        int visibleThreshold = 1;
        // The current offset index of data you have loaded

        int currentPage = 0;
        // The total number of items in the dataset after the last load

        int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.

        boolean loading = true;
        // Sets the starting page index

        int startingPageIndex = 0;
        StaggeredGridLayoutManager mStaggeredGridLayoutManager;
        int totalItemCount;
        int lastVisibleItemPosition;

        public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
            this.mStaggeredGridLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold * mStaggeredGridLayoutManager.getSpanCount();
        }

        public int getLastVisibleItem(int[] lastVisibleItemPositions) {
            int maxSize = 0;
            for (int i = 0; i < lastVisibleItemPositions.length; i++) {
                if (i == 0) {
                    maxSize = lastVisibleItemPositions[i];
                } else if (lastVisibleItemPositions[i] > maxSize) {
                    maxSize = lastVisibleItemPositions[i];
                }
            }
            return maxSize;
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        @Override
        public void onScrolled(RecyclerView view, int dx, int dy) {
            int[] lastVisibleItemPositions = mStaggeredGridLayoutManager.findLastVisibleItemPositions(null);
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
            int visibleItemCount = view.getChildCount();
            totalItemCount = mStaggeredGridLayoutManager.getItemCount();

            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }
            // If it’s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }

            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            if (!loading && (lastVisibleItemPosition + visibleThreshold) >= totalItemCount) {
                currentPage++;
                onLoadMore(currentPage, totalItemCount);
                loading = true;
            }
        }

        public void reset() {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = 0;

            this.loading = true;

        }

        public abstract void onLoadMore(int page, int totalItemsCount);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
