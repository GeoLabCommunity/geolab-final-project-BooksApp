package ge.geolab.bookswap.views.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.utils.BitmapTransform;

/**
 * Created by dalkh on 02-Jan-16.
 */
public class BookAdListAdapter extends RecyclerView.Adapter<BookAdListAdapter.MyViewHolder> {
    private ArrayList<Book> bookArray = new ArrayList<>();
    Context context;
    private int lastPosition = -1;
    public BookAdListAdapter(Context context, ArrayList<Book> bookArray) {
        this.context = context;
        this.bookArray = bookArray;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        setAnimation(holder.container, position);
        holder.bookTitle.setText(bookArray.get(position).getTitle());
        int MAX_WIDTH = 443;
        int MAX_HEIGHT = 590;

       // int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));

        if(bookArray.get(position).getPictures().get(0).equals("null")){
            Picasso.with(context)
                    .load(R.drawable.book_cover)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .into(holder.photo);
        }else {
            Picasso.with(context)
                    .load(context.getString(R.string.picture_url) + bookArray.get(position).getFrontImageUrl())
                    .placeholder(R.drawable.progress_animation)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .into(holder.photo);
        }
    }

    @Override
    public int getItemCount() {
        return bookArray == null ? 0 : bookArray.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView bookTitle;
        CardView container;

        public MyViewHolder(final View itemView) {
            super(itemView);
            container = (CardView) itemView.findViewById(R.id.card_view);
            photo = (ImageView) itemView.findViewById(R.id.list_photo);
            bookTitle = (TextView) itemView.findViewById(R.id.list_title);

        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        viewToAnimate.startAnimation(animation);
        lastPosition = position;

    }


}