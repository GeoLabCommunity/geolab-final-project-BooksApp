package ge.geolab.bookswap.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.User;
import ge.geolab.bookswap.utils.TypeFaceSpan;
import ge.geolab.bookswap.views.adapters.MainActivityPagerAdapter;
import ge.geolab.bookswap.views.customViews.CustomTabLayout;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.view_pager) ViewPager MainActivityViewPager;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tabs) CustomTabLayout tabLayout;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view) NavigationView navigationView;
    @Bind(R.id.fab) FloatingActionButton fab;
    CircleImageView userPicture;
    TextView fbUserNameTextView;
    private MainActivityPagerAdapter ViewPagerAdapter;
    CallbackManager callbackManager;
    AccessToken accessToken;
    AccessTokenTracker accessTokenTracker;
    Context context=this;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //set transition animation
        overridePendingTransition(R.anim.slide_down, R.anim.slide_out_right);
      /* SpannableString exchange= new SpannableString("ვცვლი");
        exchange.setSpan(new TypeFaceSpan(this.context, "bpg_nino_mtavruli_bold.ttf"), 0, exchange.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString finding=new SpannableString("ვეძებ");
        finding.setSpan(new TypeFaceSpan(this.context, "bpg_nino_mtavruli_bold.ttf"), 0, finding.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                accessToken=currentAccessToken;
            }
        };
        accessToken=AccessToken.getCurrentAccessToken();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        createDrawerButton();
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        View header = navigationView.getHeaderView(0);
        userPicture= (CircleImageView) header.findViewById(R.id.profile_image);
        fbUserNameTextView =(TextView) header.findViewById(R.id.username);



        ViewPagerAdapter = new MainActivityPagerAdapter(getSupportFragmentManager(),0);
        MainActivityViewPager.setAdapter(ViewPagerAdapter);

        tabLayout.setupWithViewPager(MainActivityViewPager);
        setNavigationMenuItemListeners();
        setUserUI();

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @OnClick(R.id.fab)
    public void onClick(View view) {

        if(checkUserLoginStatus()){
                Intent intent=new Intent(this,AddBookActivity.class);
            intent.putExtra("userId",Profile.getCurrentProfile().getId());
                startActivity(intent);

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       // int id = item.getItemId();

        //noinspection SimplifiableIfStatement
    /*    if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void setUserUI(){
        if(AccessToken.getCurrentAccessToken()!=null){
            Picasso.with(this).load("https://graph.facebook.com/"+Profile.getCurrentProfile().getId()+"/picture?type=large").into(userPicture);
            fbUserNameTextView.setText(Profile.getCurrentProfile().getName());
            navigationView.getMenu().getItem(6).setTitle(getString(R.string.logout));
        }
    }
    private void clearUserUI(){
        userPicture.setImageResource(android.R.color.transparent);
        fbUserNameTextView.setText("");
        navigationView.getMenu().getItem(6).setTitle(R.string.login);
    }
    private void createDrawerButton(){
        /**
         * create drawer button
         */

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        mDrawerToggle.syncState();
        /**end */
    }

    private void setNavigationMenuItemListeners(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state

                    if (menuItem.isChecked()) menuItem.setChecked(false);
                    else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    case R.id.literature:
                        if(!menuItem.isChecked()){
                            ViewPagerAdapter.setCategoryId(0);
                        }else{
                        ViewPagerAdapter.setCategoryId(1);}
                        MainActivityViewPager.setAdapter(ViewPagerAdapter);
                        return true;
                    case R.id.school_books:
                        if(!menuItem.isChecked()){
                            ViewPagerAdapter.setCategoryId(0);
                        }else{
                        ViewPagerAdapter.setCategoryId(2);}
                        MainActivityViewPager.setAdapter(ViewPagerAdapter);
                        return true;
                    case R.id.university_books:
                        if(!menuItem.isChecked()){
                            ViewPagerAdapter.setCategoryId(0);
                        }else{
                        ViewPagerAdapter.setCategoryId(3);}
                        MainActivityViewPager.setAdapter(ViewPagerAdapter);
                        return true;
                    case R.id.lexicon:
                        if(!menuItem.isChecked()){
                            ViewPagerAdapter.setCategoryId(0);
                        }else{
                        ViewPagerAdapter.setCategoryId(4);}
                        MainActivityViewPager.setAdapter(ViewPagerAdapter);
                        return true;
                    case R.id.study_notes:
                        if(!menuItem.isChecked()){
                            ViewPagerAdapter.setCategoryId(0);
                        }else{
                        ViewPagerAdapter.setCategoryId(5);}
                        MainActivityViewPager.setAdapter(ViewPagerAdapter);
                        return true;
                    case R.id.comics:
                        if(!menuItem.isChecked()){
                            ViewPagerAdapter.setCategoryId(0);
                        }else{
                        ViewPagerAdapter.setCategoryId(6);}
                        MainActivityViewPager.setAdapter(ViewPagerAdapter);
                        return true;

                    case R.id.login:
                        loginInFacebook();
                        return true;
                    case R.id.profile:
                        if(checkUserLoginStatus()) {
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        }
                    default:
                        return true;

                }
            }
        });

    }

    boolean logged[]={false};

    private boolean checkUserLoginStatus(){
        if (AccessToken.getCurrentAccessToken() != null) {
           /* UploadStateFragment uploadStateFragment = new UploadStateFragment();
            uploadStateFragment.show(getFragmentManager(),"uploadFileStateFragment");*/
            logged[0] = true;
            return true;
        }
        else {
            new AlertDialog.Builder(context)
                    .setTitle("გსურთ გაიაროთ ავტორიზაცია ?")
                    .setMessage("განცხადების დასამატებლად და რედაქტირებისთვის საჭიროა ავტორიზაცია.")
                    .setCancelable(false)
                    .setNegativeButton("არა", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("კი", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loginInFacebook();
                        }
                    }).show();
        }
        return false;
    }

    private void loginInFacebook(){

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken!=null) {
            new AlertDialog.Builder(context)
                    .setTitle("")
                    .setMessage("გსურთ დატოვოთ პროფილი ?")
                    .setCancelable(false)
                    .setPositiveButton("კი", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LoginManager.getInstance().logOut();
                            accessToken = null;
                            logged[0] = false;
                            clearUserUI();
                            Snackbar.make(fab, getString(R.string.logout_success), Snackbar.LENGTH_LONG)
                                    .show();

                        }
                    })
                    .setNegativeButton("არა", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            logged[0] = true;
                        }
                    }).show();
        } else {
            callbackManager = CallbackManager.Factory.create();
            List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "user_friends");

            LoginManager.getInstance().logInWithReadPermissions(
                    this,
                    permissionNeeds);
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {

                            GraphRequest request = GraphRequest.newMeRequest
                                    (loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            // Application code
                                            Log.v("LoginActivity", response.toString());
                                            //System.out.println("Check: " + response.toString());
                                            try {
                                                String id = object.getString("id");
                                                String name = object.getString("name");
                                                String email = object.getString("email");
                                                String gender = object.getString("gender");
                                                String birthday = object.getString("birthday");
                                                System.out.println(id + ", " + name + ", " + email + ", " + gender + ", " + birthday);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender, birthday");
                            request.setParameters(parameters);
                            request.executeAsync();
                             logged[0]=true;
                             setUserUI();

                            Snackbar.make(fab, getString(R.string.login_success), Snackbar.LENGTH_LONG)
                                    .show();
                        }

                        @Override
                        public void onCancel() {

                            Log.e("dd", "facebook login canceled");

                        }


                        @Override
                        public void onError(FacebookException e) {

                            Log.e("dd", "facebook login failed error");
                            if(e.getMessage().equals(getString(R.string.facebook_connection_error))) {
                                Snackbar.make(fab, getString(R.string.fb_connectiion_error_snackbar_msg), Snackbar.LENGTH_LONG)
                                        .show();
                            }
                            else {
                                Snackbar.make(fab, getString(R.string.fb_error_snackbar_msg), Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(context)
                .setTitle("")
                .setMessage("გსურთ დატოვოთ აპლიკაცია ?")
                .setCancelable(false)
                .setPositiveButton("კი", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();

                    }
                })
                .setNegativeButton("არა", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
}
