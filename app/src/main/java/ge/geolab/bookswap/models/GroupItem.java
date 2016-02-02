package ge.geolab.bookswap.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalkh on 31-Jan-16.
 */
public class GroupItem {
    String title;
    List<Book> items = new ArrayList<Book>();
    String categoryId;
    public GroupItem() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Book> getItems() {
        return items;
    }

    public void setItems(List<Book> items) {
        this.items = items;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public GroupItem(String title, String categoryId, List<Book> items){
        this.title=title;
        this.items=items;
        this.categoryId=categoryId;


    }
}
