package ge.geolab.bookswap.activities;

import android.content.Context;
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


import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.utils.CategoryArrays;
import ge.geolab.bookswap.views.customViews.ExpandableTextView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailsActivity extends AppCompatActivity {
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
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            textSliderView.image("http://192.168.1.100/geolabclass/uploads/"+imgArray.get(i)).setScaleType(BaseSliderView.ScaleType.CenterInside);
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

    @Override
    protected void onStop() {
        imageSlider.stopAutoCycle();
        super.onStop();
    }
}
