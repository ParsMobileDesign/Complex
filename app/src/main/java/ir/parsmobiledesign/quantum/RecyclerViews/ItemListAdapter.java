package ir.parsmobiledesign.quantum.RecyclerViews;

//import androidx.lifecycle.HolderFragment;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import com.bumptech.glide.Glide;

//import com.bumptech.glide.Glide;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.realm.Realm;
import ir.parsmobiledesign.quantum.Interfaces.ItemClickInterface;
import ir.parsmobiledesign.quantum.R;
import ir.parsmobiledesign.quantum.Realm.Item;
import ir.parsmobiledesign.quantum.Utility.Util;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.BookListHolder> {

    private ArrayList<Item> itemList;
    private LayoutInflater inflater;
    private ItemClickInterface itemClickInterface;
    private Context context;
    public final String tag = "Pedi";

    public ItemListAdapter(Context icontext, ArrayList<Item> iItems, ItemClickInterface iCatItemInterface) {
        this.itemList = iItems;
        this.itemClickInterface = iCatItemInterface;
        context = icontext;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public BookListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.items_list_item, parent, false);
        return new BookListHolder(itemView,  this.itemClickInterface);
    }


    @Override
    public void onBindViewHolder(BookListHolder holder, int position) {
        Item obj = itemList.get(position);
        Glide.with(context)
                .load(obj.getThumbnailBitmap())
                .into(holder.Image);
        holder.Title.setText(obj.getTitle());
        holder.Price.setText(Util.cnvToCurrency(obj.getPrice()) + " ریال ");
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class BookListHolder extends RecyclerView.ViewHolder {
        private ImageView Image;
        private TextView Title;
        private TextView Price;
        private ItemClickInterface clickInterface;
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickInterface.OnItemClick(getAdapterPosition());
            }
        };

        public BookListHolder(View view, ItemClickInterface iInterface) {
            super(view);
            clickInterface = iInterface;
            view.setOnClickListener(onClickListener);
            this.Image = view.findViewById(R.id.item_image);
            this.Title = view.findViewById(R.id.item_title);
            this.Price = view.findViewById(R.id.item_price);
        }

    }
}
