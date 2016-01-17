package ge.geolab.bookswap.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import co.dift.ui.SwipeToAction;
import de.hdodenhof.circleimageview.CircleImageView;
import ge.geolab.bookswap.R;
import ge.geolab.bookswap.models.Book;
import ge.geolab.bookswap.utils.CategoryArrays;

/**
 * Created by dalkh on 15-Jan-16.
 */
public class ProfileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Book> bookList=new ArrayList<>();
    Context context;
    public ProfileListAdapter(ArrayList<Book> bookList,Context context){
        this.context=context;
        this.bookList=bookList;
    }

    @Override
    public ProfileListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_list_item, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Book item =bookList.get(position);
        final MyViewHolder vh = (MyViewHolder) holder;
        vh.titleView.setText(item.getTitle());
       // Picasso.with(context).load(R.drawable.ic_quill).resize(70,70).centerInside().into(vh.profileImageView);

        vh.profileImageView.setImageResource(CategoryArrays.categoryIcons[Integer.parseInt(item.getCategory())]);
        vh.authorView.setText(item.getAuthor());
  /*      vh.clickView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float lastY=0;
                switch(event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        vh.clickView.setBackgroundResource(R.color.white_pressed);
                        //Toast.makeText(c, "DOWN", Toast.LENGTH_SHORT).show();
                        lastY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        vh.clickView.setBackgroundResource(R.color.background);
                        //Toast.makeText(c, "UP", Toast.LENGTH_SHORT).show();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float abs = Math.abs(lastY - event.getY());

                        if(abs > 2) // TIME_DELAY=2
                            v.setBackgroundResource(R.color.background);
                        break;
                    default:
                        vh.clickView.setBackgroundResource(R.color.background);
                        break;
                }

                return true;
            }
        });*/
        vh.data = item;

    }

    @Override
    public int getItemCount() {
        return bookList== null ? 0 : bookList.size();
    }
    public class MyViewHolder extends SwipeToAction.ViewHolder<Book> {
        ImageView profileImageView;
        TextView titleView;
        LinearLayout clickView;
        TextView authorView;

        public MyViewHolder(final View itemView) {
            super(itemView);
            profileImageView = (ImageView) itemView.findViewById(R.id.book_image);
            titleView = (TextView) itemView.findViewById(R.id.book_title);
                clickView= (LinearLayout) itemView.findViewById(R.id.list_click);
            authorView= (TextView) itemView.findViewById(R.id.book_author);
        }
    }
   /* @Override
    public int getCount() {
        return this.bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;
        ViewHolder holder = null;
        if (convertView == null) {
            itemView = View.inflate(context, R.layout.profile_list_item, null);
            ImageView profileImageView = (ImageView) itemView.findViewById(R.id.book_image);
            TextView titleView = (TextView) itemView.findViewById(R.id.book_title);


            holder = new ViewHolder();
            holder.profileImageView = profileImageView;
            holder.titleView = titleView;

            itemView.setTag(holder);
        } else {
            itemView = convertView;
            holder = (ViewHolder) itemView.getTag();
        }


        Book book = (Book) getItem(position);

        Picasso.with(context).load(book.getFrontImageUrl()).into(holder.profileImageView);
        holder.titleView.setText(book.getTitle());



        return itemView;
    }

    private class ViewHolder {
        ImageView profileImageView;
        TextView titleView;
    }*/
}
