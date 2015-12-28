package ge.geolab.bookswap.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Profile;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;

public class AddBookActivity extends AppCompatActivity {
    @Bind(R.id.input_book_title) EditText inputTitle;
    @Bind(R.id.input_book_description) EditText inputDescription;
    @Bind(R.id.input_author) EditText inputAuthor;
    @Bind(R.id.exchange_text) TextView exchangeText;
    @Bind(R.id.input_exchange_in) EditText inputExchange;
    @Bind(R.id.input_location) EditText inputLocation;
    @Bind(R.id.ad_type) Spinner adTypeSpinner;
    @Bind(R.id.category_spinner) Spinner categorySpinner;
    @Bind(R.id.book_condition_spinner) Spinner bookConditionSpinner;
    @Bind(R.id.add_from_camera) FloatingActionButton fabCamera;
    @Bind(R.id.add_from_gallery) FloatingActionButton fabGallery;
    @Bind(R.id.submit) Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getIntent();
        addSpinner(R.array.ad_type_array,adTypeSpinner);
        addSpinner(R.array.category_array,categorySpinner);
        addSpinner(R.array.condition_array,bookConditionSpinner);
        adTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    exchangeText.setVisibility(View.GONE);
                    inputExchange.setVisibility(View.GONE);
                }else {
                    exchangeText.setVisibility(View.VISIBLE);
                    inputExchange.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @OnClick(R.id.submit)
    public void onClick(View view){
        if( validateFields() )
            Snackbar.make(view,"validated",Snackbar.LENGTH_LONG).show();
    }

    private void addSpinner(int arrayId,Spinner spinner){

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayId, R.layout.spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
    private void createBook(){
        String title="",author="",description="";
        title=String.valueOf(inputTitle.getText());
        description=String.valueOf(inputDescription.getText());
        Book book=new Book(title,author,description);
        System.out.println(book.getTitle());
    }
    private boolean validateFields(){
        ArrayList<Boolean> validChecks=new ArrayList<>();
        if( inputTitle.getText().toString().length() == 0 ) {
            inputTitle.setError("First name is required!");
            validChecks.add(false);
        }
        if( inputAuthor.getText().toString().length() == 0 ) {
            inputAuthor.setError("First name is required!");
            validChecks.add(false);
        }
        if(inputExchange.getVisibility()==View.VISIBLE && inputExchange.getText().toString().length() == 0){
            inputExchange.setError("First name is required!");
            validChecks.add(false);
        }
        for (int i = 0; i <validChecks.size() ; i++) {
            if(!validChecks.get(i)){
                return false;
            }
        }
        return true;
    }
}
