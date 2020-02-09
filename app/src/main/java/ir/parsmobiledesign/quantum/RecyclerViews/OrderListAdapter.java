package ir.parsmobiledesign.quantum.RecyclerViews;

//import androidx.lifecycle.HolderFragment;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.realm.Realm;
import ir.parsmobiledesign.quantum.Interfaces.SwipeListener;
import ir.parsmobiledesign.quantum.Interfaces.OrderPlusMinuslistener;
import ir.parsmobiledesign.quantum.Interfaces.UpdatePriceListener;
import ir.parsmobiledesign.quantum.R;
import ir.parsmobiledesign.quantum.Realm.MemOrderItem;

//import com.bumptech.glide.Glide;


public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.orderListHolder> implements SwipeListener {
    Realm realm;
    private ArrayList<MemOrderItem> itemList;
    private LayoutInflater inflater;
    private OrderPlusMinuslistener itemPlusMinusClickInterface;
    private UpdatePriceListener updatePriceListener;
    private Context context;
    private boolean editable;
    public final String tag = "Pedi";

    public OrderListAdapter(Context icontext, ArrayList<MemOrderItem> iItems, OrderPlusMinuslistener iInterface, UpdatePriceListener iUpdatePriceListener, boolean iEditable) {
        this.itemList = iItems;
        this.realm = Realm.getDefaultInstance();
        this.itemPlusMinusClickInterface = iInterface;
        context = icontext;
        inflater = LayoutInflater.from(context);
        this.updatePriceListener = iUpdatePriceListener;
        this.editable = iEditable;
    }

    @Override
    public orderListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = this.editable ? R.layout.order_list_item : R.layout.order_list_item_view;
        View itemView = inflater.inflate(layout, parent, false);
        return new orderListHolder(itemView, realm, this.itemPlusMinusClickInterface, this.editable);
    }


    @Override
    public void onBindViewHolder(orderListHolder holder, int position) {
        MemOrderItem obj = itemList.get(position);
        Glide.with(context)
                .load(obj.getBitmap())
                .into(holder.image);
        holder.Title.setText(obj.getItemObj().getTitle());
        holder.Count.setText(String.valueOf(obj.getCount()));
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onSwipe(int id) {
        MemOrderItem temp = itemList.get(id);
        if (temp != null) {
            itemList.remove(id);
            int totalDeduction = temp.getCount() * temp.getItemObj().getPrice();
            this.updatePriceListener.onUpdatePrice(totalDeduction);
        }
        notifyItemRemoved(id);
    }

    public static class orderListHolder extends RecyclerView.ViewHolder {
        private ImageView minus, plus, image;
        private TextView Title;
        private TextView Count;
        private Realm realm;
        private OrderPlusMinuslistener clickInterface;
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte buttonType = 0;
                switch (v.getId()) {
                    case R.id.orderItemMinus:
                        buttonType = 1;
                        break;
                    case R.id.orderItemPlus:
                        buttonType = 2;
                        break;
                }
                clickInterface.OnPlusMinusClick(getAdapterPosition(), buttonType);
            }
        };

        public orderListHolder(View view, Realm irealm, OrderPlusMinuslistener iInterface, boolean iEditable) {
            super(view);
            realm = irealm;
            clickInterface = iInterface;
            this.minus = view.findViewById(R.id.orderItemMinus);
            this.plus = view.findViewById(R.id.orderItemPlus);
            this.Title = view.findViewById(R.id.item_title);
            this.Count = view.findViewById(R.id.item_count);
            this.image = view.findViewById(R.id.item_image);
            if (iEditable) {
                // view.setOnClickListener(onClickListener);
                this.minus.setOnClickListener(onClickListener);
                this.plus.setOnClickListener(onClickListener);
            }
        }

    }
}
