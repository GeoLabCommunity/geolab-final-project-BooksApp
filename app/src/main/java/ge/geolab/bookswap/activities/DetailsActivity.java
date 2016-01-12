package ge.geolab.bookswap.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.utils.CategoryArrays;
import ge.geolab.bookswap.views.customViews.ExpandableTextView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailsActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener{
    @Bind(R.id.slider) SliderLayout imageSlider;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.title) TextView titleView;
    @Bind(R.id.author) TextView authorView;
    @Bind(R.id.category) TextView categoryView;
    @Bind(R.id.condition) TextView conditionView;
    @Bind(R.id.location) TextView locationView;
    @Bind(R.id.exchange_item) TextView exchangeItemView;
    @Bind(R.id.email) TextView emailView;
    @Bind(R.id.mobile_number) TextView mobileNumView;
    @Bind(R.id.user_image) CircleImageView profilePicView;
    @Bind(R.id.user_name) TextView usernameView;
    @Bind(R.id.description_box) ExpandableTextView descriptionView;
    @Bind(R.id.arrow) ImageView arrowView;
    @Bind(R.id.custom_indicator) PagerIndicator pagerIndicator;
    @Bind(R.id.suggestions_slider) SliderLayout suggestionsSlider;
    @BindString(R.string.list_array_url) String jsonUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Animation fade_in = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        descriptionView.setInAnimation(fade_in);
        Book book= (Book) getIntent().getSerializableExtra("book");
           imageSlider.setCustomIndicator(pagerIndicator);
        setData(book);
        if(book.getPictures().size()==1){
            imageSlider.stopAutoCycle();
            pagerIndicator.setVisibility(View.GONE);
            //stop sliding when there is one image
            imageSlider.setPagerTransformer(false, new BaseTransformer() {
                @Override
                protected void onTransform(View view, float position) {

                }
            });

        }
        if(book.getPictures().isEmpty()){
            imageSlider.stopAutoCycle();
            pagerIndicator.setVisibility(View.GONE);
            imageSlider.setVisibility(View.GONE);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        suggestionsSlider.stopAutoCycle();
        getSuggestionsData(jsonUrl,book.getId());


    }
    @OnClick(R.id.description_container)
    public void onClick(View view){

        switch (descriptionView.getVisibility()){
            case View.GONE:
                arrowView.setRotation(0);
                descriptionView.setVisibility(View.VISIBLE);
                rotate(90,arrowView);
                break;
            case View.VISIBLE:
                arrowView.setRotation(90);
                descriptionView.setVisibility(View.GONE);
                rotate(-90,arrowView);
                default:
                    break;
        }

    }
    private void rotate(float degree, ImageView imgview) {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(300);
        rotateAnim.setFillAfter(true);
        imgview.startAnimation(rotateAnim);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void setData(Book book){
        ArrayList<String> imgArray=book.getPictures();
        for (int i = 0; i <imgArray.size() ; i++) {
            DefaultSliderView textSliderView=new DefaultSliderView(this);
            textSliderView.image(getResources().getString(R.string.picture_url)+imgArray.get(i)).setScaleType(BaseSliderView.ScaleType.CenterInside);
            imageSlider.addSlider(textSliderView);

        }
        titleView.setText(book.getTitle());
        authorView.setText(book.getAuthor());
        descriptionView.setText(book.getDescription());
        conditionView.setText(CategoryArrays.conditions[Integer.parseInt(book.getCondition())]);
        categoryView.setText(CategoryArrays.categories[Integer.parseInt(book.getCategory())]);
        locationView.setText(book.getLocation());
        exchangeItemView.setText(book.getExchangeItem());
        emailView.setText(book.geteMail());
        mobileNumView.setText(book.getMobileNum());
        final String[] id = {""};
        Picasso.with(this).load("https://graph.facebook.com/"+ book.getId()+"/picture?type=large").into(profilePicView);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                book.getId(),
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        Log.v("LoginActivity", response.toString());
                        try {
                            if(response.getJSONObject()!=null) {
                                id[0] = response.getJSONObject().getString("name");
                                usernameView.setText(id[0]);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }
    private void getSuggestionsData(String url, String id){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url+"/0/user_id/"+id, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray jsonArray) {
                ArrayList<Book> list=new ArrayList<>();
                if(jsonArray.length()>1) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = null;

                        if(i>5) break;
                        //skip first Ad
                      if(i!=0) {
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
                    }
                    createSuggestionSlider(list);
                    if(list.size()==1){
                        //stop sliding when there is one image
                        suggestionsSlider.setPagerTransformer(false, new BaseTransformer() {
                            @Override
                            protected void onTransform(View view, float position) {

                            }
                        });

                    }

                }
                if(list.isEmpty()){
                    suggestionsSlider.setVisibility(View.GONE);
                }
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
    private void createSuggestionSlider(final ArrayList<Book> list){
        for (int i = 0; i <list.size() ; i++) {
            TextSliderView textSliderView=new TextSliderView(this);
            textSliderView.image(getResources().getString(R.string.picture_url)+list.get(i).getFrontImageUrl()).setScaleType(BaseSliderView.ScaleType.CenterCrop);
            textSliderView.description(list.get(i).getTitle());
            final int finalI = i;
            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                    intent.putExtra("book", list.get(finalI));
                    startActivity(intent);
                }
            });
            suggestionsSlider.addSlider(textSliderView);
        }
    }
    @Override
    protected void onStop() {
        imageSlider.stopAutoCycle();
        suggestionsSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }
}
