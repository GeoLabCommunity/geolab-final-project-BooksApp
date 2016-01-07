package ge.geolab.bookswap.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.utils.CategoryArrays;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Book book= (Book) getIntent().getSerializableExtra("book");
           imageSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
        setData(book);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
