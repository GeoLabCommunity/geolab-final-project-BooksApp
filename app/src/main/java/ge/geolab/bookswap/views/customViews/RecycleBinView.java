package ge.geolab.bookswap.views.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.LinearLayout;

/**
 * Created by dalkh on 01-Jan-16.
 */
public class RecycleBinView extends LinearLayout {
    private Animation inAnimation;
    private Animation outAnimation;

    public RecycleBinView(Context context)
    {
        super(context);
    }
    public RecycleBinView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleBinView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public void setInAnimation(Animation inAnimation)
    {
        this.inAnimation = inAnimation;
    }

    public void setOutAnimation(Animation outAnimation)
    {
        this.outAnimation = outAnimation;
    }

    @Override
    public void setVisibility(int visibility)
    {
        if (getVisibility() != visibility)
        {
            if (visibility == VISIBLE)
            {
                if (inAnimation != null) startAnimation(inAnimation);
            }
            else if ((visibility == INVISIBLE) || (visibility == GONE))
            {
                if (outAnimation != null) startAnimation(outAnimation);
            }
        }

        super.setVisibility(visibility);
    }
}
