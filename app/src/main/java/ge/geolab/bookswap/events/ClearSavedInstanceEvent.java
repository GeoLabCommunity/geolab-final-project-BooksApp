package ge.geolab.bookswap.events;

/**
 * Created by dalkh on 08-Feb-16.
 */
public class ClearSavedInstanceEvent {
    public final boolean isCategorySelected;
    public final int categoryId;
    public ClearSavedInstanceEvent(boolean isCategorySelected,int categoryId){
        this.isCategorySelected=isCategorySelected;
        this.categoryId=categoryId;
    }
}
