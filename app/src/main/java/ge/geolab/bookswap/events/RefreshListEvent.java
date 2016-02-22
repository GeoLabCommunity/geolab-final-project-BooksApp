package ge.geolab.bookswap.events;

/**
 * Created by dalkh on 22-Feb-16.
 */
public class RefreshListEvent {
    public final boolean isEntryAdded;

    public RefreshListEvent(boolean isEntryAdded) {
        this.isEntryAdded = isEntryAdded;
    }
}
