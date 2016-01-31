package ge.geolab.bookswap.fragments;



import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.facebook.Profile;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.views.adapters.BookOfferListAdapter;
import ge.geolab.bookswap.views.customListeners.ItemClickSupport;

/**
 * Created by dalkh on 29-Jan-16.
 */
public class MyBookListDialog extends DialogFragment {

private String TAG="Offer Dialog Volley";

   public static MyBookListDialog newInstance(String receiverId,String bookId)
    {
        MyBookListDialog fragment = new MyBookListDialog();
        Bundle args = new Bundle();
        args.putString("receiverId",receiverId);
        args.putString("receiver_book_id",bookId);
        fragment.setArguments(args);

        return fragment;
    }


    private ArrayList<Book> bookList;
    private BookOfferListAdapter adapter;
    private RecyclerView mRecyclerView;
    private static AVLoadingIndicatorView spinnerView;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v=inflater.inflate(R.layout.fragment_offer_book, null);
        spinnerView= (AVLoadingIndicatorView) v.findViewById(R.id.avloadingIndicatorView);
        bookList=new ArrayList<>();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView= (RecyclerView) v.findViewById(R.id.offer_book_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new BookOfferListAdapter(getActivity(), bookList);
        mRecyclerView.setAdapter(adapter);
        fetchMyBookData(getString(R.string.list_array_url), Profile.getCurrentProfile().getId(),bookList,adapter,spinnerView);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout




        builder.setView(v)
                .setTitle("აირჩიეთ წიგნი")
                // Add action buttons
                .setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                });

        final Dialog dialog=builder.create();

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                sendData(getString(R.string.book_change_POST_url),
                        Profile.getCurrentProfile().getId(),
                        bookList.get(position).getServer_id(),
                        getArguments().getString("receiverId"),
                        getArguments().getString("receiver_book_id"));
                dialog.dismiss();
            }
        });

        return dialog;

    }


    private Dialog showList() {
       AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setTitle("აირჩიეთ წიგნი")
                .setCancelable(false);
        builder.setView(mRecyclerView);

        return builder.create();
    }

    private void fetchMyBookData(String url, String id,
                                 final ArrayList<Book> list,
                                 final BookOfferListAdapter adapter,
                                 final AVLoadingIndicatorView spinner){
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

           spinner.setVisibility(View.VISIBLE);

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url+"0/user_id/"+id, new Response.Listener<JSONArray>() {



            @Override
            public void onResponse(JSONArray jsonArray) {
                list.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = null;



                    try {

                        obj = jsonArray.getJSONObject(i);
                        Book bookObject = new Book();
                        bookObject.setCategory(obj.getString("category_id"));
                        bookObject.setTitle(obj.getString("title"));
                        bookObject.setServer_id(obj.getString("id"));
                        list.add(bookObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                 spinner.setVisibility(View.GONE);
                 mRecyclerView.setVisibility(View.VISIBLE);
                 adapter.notifyDataSetChanged();

            }



        },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "GET Error: " + error.getMessage());


                    }
                });
      requestQueue.add(jsonArrayRequest);

    }

    private void sendData(String url, final String myId, final String myBookId, final String receiverId, final String receiverBookId){
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        StringRequest jsonArrayRequest = new StringRequest(Request.Method.POST,
                url + "u_id/"+myId+"/book_id/"+myBookId+"/r_id/"+receiverId+"/change_id/"+receiverBookId, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
               // Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "POST Error: " + error.getMessage());

            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("u_id", myId);
                params.put("book_id", myBookId);
                params.put("r_id", receiverId);
                params.put("change_id", receiverBookId);
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
