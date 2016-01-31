package ge.geolab.bookswap.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.utils.CategoryArrays;

/**
 * Created by dalkh on 29-Jan-16.
 */
public class BookOfferListAdapter extends RecyclerView.Adapter<BookOfferListAdapter.MyViewHolder> {
    private ArrayList<Book> bookArray = new ArrayList<>();
    Context context;
    public BookOfferListAdapter(Context context, ArrayList<Book> bookArray) {
        this.context = context;
        this.bookArray = bookArray;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_book_list_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Book item =bookArray.get(position);
        holder.bookTitle.setText(item.getTitle());
        holder.photo.setImageResource(CategoryArrays.categoryIcons[Integer.parseInt(item.getCategory())]);


    }

    @Override
    public int getItemCount() {
        return bookArray == null ? 0 : bookArray.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView bookTitle;

        public MyViewHolder(final View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.category_icon);
            bookTitle = (TextView) itemView.findViewById(R.id.book_title);

        }
    }
}
