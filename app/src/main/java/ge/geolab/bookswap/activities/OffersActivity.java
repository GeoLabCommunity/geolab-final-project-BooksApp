package ge.geolab.bookswap.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import com.facebook.Profile;
import com.unnamed.b.atv.model.TreeNode;

import java.util.ArrayList;
import java.util.List;

import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.models.GroupItem;
import ge.geolab.bookswap.views.adapters.ExpandableListAdapter;
import ge.geolab.bookswap.views.customViews.AnimatedExpandableListView;

public class OffersActivity extends AppCompatActivity {
    private AnimatedExpandableListView listView;
    private ExpandableListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawList();

    }
     private void drawList(){
         List<GroupItem> headerItemsList = new ArrayList<GroupItem>();

         // Populate our list with groups and it's children
         for(int i = 1; i < 100; i++) {
             GroupItem parentItem = new GroupItem();

             parentItem.setTitle("Group " + i);
             ArrayList<Book> childList=new ArrayList<>();
             for(int j = 0; j < i; j++) {
                 Book childItem = new Book();
                 childItem.setTitle("Awesome item " + j);
                 childItem.setId(Profile.getCurrentProfile().getName());

                 childList.add(childItem);
             }
             parentItem.setItems(childList);
             headerItemsList.add(parentItem);
         }

         adapter = new ExpandableListAdapter(this);
         adapter.setData(headerItemsList);

         listView = (AnimatedExpandableListView) findViewById(R.id.expandable_list);
         listView.setAdapter(adapter);

         // In order to show animations, we need to use a custom click handler
         // for our ExpandableListView.
         listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

             @Override
             public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                 // We call collapseGroupWithAnimation(int) and
                 // expandGroupWithAnimation(int) to animate group
                 // expansion/collapse.
                 if (listView.isGroupExpanded(groupPosition)) {
                     listView.collapseGroupWithAnimation(groupPosition);
                 } else {
                     listView.expandGroupWithAnimation(groupPosition);
                 }
                 return true;

             }


         });
     }
}
