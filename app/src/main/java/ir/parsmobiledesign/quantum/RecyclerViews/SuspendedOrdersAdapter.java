package ir.parsmobiledesign.quantum.RecyclerViews;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import ir.parsmobiledesign.quantum.Interfaces.ItemClickInterface;
import ir.parsmobiledesign.quantum.R;
import ir.parsmobiledesign.quantum.Realm.MemOrder;
import ir.parsmobiledesign.quantum.Utility.Util;


public class SuspendedOrdersAdapter extends RecyclerView.Adapter<SuspendedOrdersAdapter.SusorderHolder> {

    private ArrayList<MemOrder> itemList;
    private LayoutInflater inflater;
    private ItemClickInterface itemClickInterface;
    private Context context;
    public final String tag = "Pedi";
    Realm realm;

    public SuspendedOrdersAdapter(Context icontext, ArrayList<MemOrder> iItems, ItemClickInterface iCatItemInterface) {
        this.itemList = iItems;
        this.realm = Realm.getDefaultInstance();
        this.itemClickInterface = iCatItemInterface;
        context = icontext;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public SusorderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.sus_orders_list_item, parent, false);
        return new SusorderHolder(itemView, this.itemClickInterface);
    }


    @Override
    public void onBindViewHolder(SusorderHolder holder, int position) {
        MemOrder obj = itemList.get(position);
        holder.date.setText(obj.getDateinString());
        holder.time.setText(obj.getTimeinString());
        holder.totalPrice.setText(Util.cnvToCurrency(obj.getTotalAmount()) + " ریال ");
        holder.tableNo.setText("شماره میز : "+ obj.getTableNo());
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class SusorderHolder extends RecyclerView.ViewHolder {
        private TextView date,tableNo,time,totalPrice;
        private ImageView reload;
        private ItemClickInterface clickInterface;
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickInterface.OnItemClick(getAdapterPosition());
            }
        };

        public SusorderHolder(View view, ItemClickInterface iInterface) {
            super(view);
            clickInterface = iInterface;
            view.setOnClickListener(onClickListener);
            this.date = view.findViewById(R.id.order_Date);
            this.time = view.findViewById(R.id.order_Time);
            this.totalPrice = view.findViewById(R.id.order_TotalAmount);
            this.reload = view.findViewById(R.id.orderReload);
            this.tableNo = view.findViewById(R.id.order_tableNo);
        }

    }
}
