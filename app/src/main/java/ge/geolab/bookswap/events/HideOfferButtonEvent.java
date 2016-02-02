package ge.geolab.bookswap.events;

/**
 * Created by dalkh on 02-Feb-16.
 */
public class HideOfferButtonEvent {
    public final boolean isMessageSent;

    public HideOfferButtonEvent(boolean isMessageSent) {
        this.isMessageSent = isMessageSent;
    }
}
