package ge.geolab.bookswap.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.network.UploadFileToServer;
import ge.geolab.bookswap.utils.BookCamera;
import ge.geolab.bookswap.utils.UnitConverters;
import ge.geolab.bookswap.views.customViews.RecycleBinView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddBookActivity extends AppCompatActivity implements View.OnTouchListener {
    @Bind(R.id.input_book_title) EditText inputTitle;
    @Bind(R.id.input_book_description) EditText inputDescription;
    @Bind(R.id.input_author) EditText inputAuthor;
    @Bind(R.id.exchange_text) TextView exchangeText;
    @Bind(R.id.input_exchange_in) EditText inputExchange;
    @Bind(R.id.input_location) EditText inputLocation;
    @Bind(R.id.input_email) EditText inputEmail;
    @Bind(R.id.input_mobile_number) EditText inputMobileNum;
    @Bind(R.id.ad_type) Spinner adTypeSpinner;
    @Bind(R.id.category_spinner) Spinner categorySpinner;
    @Bind(R.id.book_condition_spinner) Spinner bookConditionSpinner;
    @Bind(R.id.fab) FloatingActionsMenu fab;
    @Bind(R.id.add_from_camera) FloatingActionButton fabCamera;
    @Bind(R.id.add_from_gallery) FloatingActionButton fabGallery;
    @Bind(R.id.submit) Button button;
    @Bind(R.id.pic_container) LinearLayout picContainer;
    @Bind(R.id.recycle_bin) RecycleBinView recycleBin;
    private Uri fileUri;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 100;
    private Book bookAd=new Book();
    private HashMap<Integer,String> pictureMap=new HashMap<>();
    private ArrayList<String> pictureArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_out_top);
        Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        recycleBin.setInAnimation(slide_down);
        recycleBin.setOutAnimation(slide_up);
            recycleBin.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    int action = event.getAction();
                    View view = (View) event.getLocalState();

                    switch (action) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            v.setVisibility(View.VISIBLE);
                            v.invalidate();
                            break;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            v.setBackgroundColor(getResources().getColor(R.color.transparent_red));
                            v.invalidate();
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            v.setBackgroundColor(getResources().getColor(R.color.transparent_gray));
                            v.invalidate();
                            break;
                        case DragEvent.ACTION_DROP:

                            ViewGroup owner = (ViewGroup) view.getParent();
                            view.getId();
                            //removing picture from container and HashMap
                            pictureMap.remove(view.getId());
                            owner.removeView(view);
                                 v.setVisibility(View.GONE);
                            v.setBackgroundColor(getResources().getColor(R.color.transparent_gray));
                                v.invalidate();
                    /*LinearLayout container = (LinearLayout) layoutview;
                    container.setVisibility(View.VISIBLE);*/
                            //view.setVisibility(View.VISIBLE);
                            return true;
                        case DragEvent.ACTION_DRAG_ENDED:
                            Log.d("DRAG", "Drag ended");
                            if (dropEventNotHandled(event)){
                                view.setVisibility(View.VISIBLE);
                                v.setBackgroundColor(getResources().getColor(R.color.transparent_gray));
                                view.invalidate();
                            }
                            v.setVisibility(View.GONE);
                            v.invalidate();
                             break;
                        default:
                            Log.d("DRAG", "unknown drop");
                            return false;

                    }
                    return true;
                }

                private boolean dropEventNotHandled(DragEvent dragEvent) {
                    return !dragEvent.getResult();
                }
            });


        inputDescription.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if(event.getResult()){
                View view = (View) event.getLocalState();
                view.setVisibility(View.VISIBLE);
                view.invalidate();
                }
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getIntent();
        addSpinner(R.array.ad_type_array,adTypeSpinner);
        addSpinner(R.array.category_array,categorySpinner);
        addSpinner(R.array.condition_array,bookConditionSpinner);
        setSpinnerListeners();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.add_from_gallery)
    public void onClickGallery(View view){
        /*if( validateFields() )
            Snackbar.make(view,"validated",Snackbar.LENGTH_LONG).show();*/
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }
    @OnClick(R.id.add_from_camera)
    public void onClickCamera(View view){
             captureImage();

    }


    private void captureImage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = BookCamera.getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
    }
    private void setSpinnerListeners(){
        adTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    exchangeText.setVisibility(View.GONE);
                    inputExchange.setVisibility(View.GONE);
                    bookAd.setAdType(""+position);

                }else {
                    exchangeText.setVisibility(View.VISIBLE);
                    inputExchange.setVisibility(View.VISIBLE);
                    bookAd.setAdType(""+position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bookConditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    bookAd.setCondition(""+position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bookAd.setCategory(parent.getAdapter().getItem(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        //String title="",author="",description="";
        String id= Profile.getCurrentProfile().getId();
        String title=inputTitle.getText().toString();
        String description=String.valueOf(inputDescription.getText());
        String author=String.valueOf(inputAuthor.getText());
        String exchangeItem=String.valueOf(inputExchange.getText());
        String location=String.valueOf(inputLocation.getText());
        String email=String.valueOf(inputEmail.getText());
        String mobileNum=String.valueOf(inputMobileNum.getText());
        pictureArray=new ArrayList<>(pictureMap.values());
        bookAd.setAuthor(author);
        bookAd.setId(id);
        bookAd.setTitle(title);
        bookAd.setDescription(description);
        bookAd.setExchangeItem(exchangeItem);
        bookAd.setLocation(location);
        bookAd.seteMail(email);
        bookAd.setMobileNum(mobileNum);
        bookAd.setPictures(pictureArray);

    }
    private boolean validateFields(){
        ArrayList<Boolean> validChecks=new ArrayList<>();
        if( inputTitle.getText().toString().length() == 0 ) {
            inputTitle.setError(getString(R.string.validate_title));
            validChecks.add(false);
        }
        if(bookAd.getAdType().equals("0") && bookAd.getPictures().isEmpty()){
            Snackbar.make(fab,getString(R.string.validate_pictures),Snackbar.LENGTH_LONG).show();
            validChecks.add(false);
        }

        if(inputExchange.getVisibility()==View.VISIBLE && inputExchange.getText().toString().length() == 0){
            inputExchange.setError(getString(R.string.validate_exchange_item));
            validChecks.add(false);
        }
        for (int i = 0; i <validChecks.size() ; i++) {
            if(!validChecks.get(i)){
                return false;
            }
        }
        return true;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            // get the file url
            fileUri = savedInstanceState.getParcelable("file_uri");
        }
    }
    private int id=0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null){
                Uri selectedImage=data.getData();
                id++;
                pictureMap.put(id,getRealPathFromURI(selectedImage));
                final ImageView imageView=setPicture(selectedImage);
                imageView.setId(id);
                 imageView.setOnTouchListener(this);
                picContainer.addView(imageView);

        }
        if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                    final ImageView imageView=setPicture(fileUri);
                    id++;
                    imageView.setId(id);
                    pictureMap.put(id,fileUri.getPath());
                     imageView.setOnTouchListener(this);
                    picContainer.addView(imageView);


            } else if (resultCode == Activity.RESULT_CANCELED) {


            } else {
                // failed to capture image
                Toast.makeText(this,
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }


        }
    }
    private void removePicture(final ImageView imageView){
        imageView.animate()
                .scaleXBy(-1.0f)
                .scaleYBy(-1.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        imageView.setImageBitmap(null);
                        imageView.setVisibility(View.GONE);
                    }
                });
    }
    private ImageView setPicture(Uri uri){

            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize =1;
            InputStream is = null;
             try {
            is = getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
            e.printStackTrace();
            }
            final Bitmap bitmapImage = BitmapFactory.decodeStream(is, null , options);
                  ;
           // Bitmap bitmapImage=MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
        /*    ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG,60,stream);
            byte[] byteArray=stream.toByteArray();
            Bitmap resBit= BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);*/
            final ImageView imageView=new ImageView(getApplicationContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    UnitConverters.getPx(200,getResources()),
                    UnitConverters.getPx(200,getResources())
            ));
            imageView.setImageBitmap(bitmapImage);
            return imageView;


    }
    @OnClick(R.id.submit)
    public void onSubmit(View view){
        createBook();
        if(validateFields()) {

            new UploadFileToServer(this, bookAd).execute();
            Intent intent=new Intent(this,MainActivity.class);
            finish();
            startActivity(intent);
        }
    }

    public String getRealPathFromURI (Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            recycleBin.setVisibility(View.VISIBLE);
            return true;
        } else {
            return false;
        }
    }

}
