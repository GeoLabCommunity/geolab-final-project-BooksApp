package ge.geolab.bookswap.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailsActivity extends AppCompatActivity {
    @Bind(R.id.slider) SliderLayout imageSlider;
    @Bind(R.id.title) TextView titleView;
    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Book book= (Book) getIntent().getSerializableExtra("book");
        TextSliderView textSliderView=new TextSliderView(this);
        textSliderView.image("http://192.168.1.100/geolabclass/uploads/"+book.getFrontImageUrl()).setScaleType(BaseSliderView.ScaleType.CenterCrop);
        imageSlider.addSlider(textSliderView);
        titleView.setText(book.getTitle());

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
