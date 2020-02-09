package ir.parsmobiledesign.quantum;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import ir.parsmobiledesign.quantum.Interfaces.OrderPlusMinuslistener;
import ir.parsmobiledesign.quantum.Interfaces.UpdatePriceListener;
import ir.parsmobiledesign.quantum.Realm.Item;
import ir.parsmobiledesign.quantum.Realm.MemOrder;
import ir.parsmobiledesign.quantum.Realm.MemOrderItem;
import ir.parsmobiledesign.quantum.RecyclerViews.OrderListAdapter;
import ir.parsmobiledesign.quantum.RecyclerViews.SimpleTouchCallback;
import ir.parsmobiledesign.quantum.Utility.Util;

public class OrderFragment extends Fragment implements OrderPlusMinuslistener, UpdatePriceListener {
    Context context;
    Realm realm;
    RecyclerView ordersClerView;
    MemOrder mainOrder;
    ArrayList<MemOrderItem> mainOrderItems;
    TextView orderTotalValue;
    OrderListAdapter adapter;
    int totalPrice, memSrl = 0, orderSrl = 0;
    String orderType;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (totalPrice > 0) {
                realm.beginTransaction();
                mainOrder.getMemOrderItems().clear();
                mainOrder.getMemOrderItems().addAll(mainOrderItems);
                mainOrder.updateTotalAmount();
                realm.commitTransaction();
                YesNoSuspendDialog dialog = new YesNoSuspendDialog();
                Bundle bundle = new Bundle();
                bundle.putString("totalAmount", String.valueOf(mainOrder.getTotalAmount()));
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "Tag");
            } else
                Util.showInfoDialog(context, R.string.totalAmountIsZero, R.string.error);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Everyone who enters this page should already created and object of MemOrder...either suspended or new Order...
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Variable Initialization
        context = getActivity();
        realm = Realm.getDefaultInstance();
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.sharedPrefName), Context.MODE_PRIVATE);
        ordersClerView = view.findViewById(R.id.OrderclerView);
        orderTotalValue = view.findViewById(R.id.orderTotalValue);
        orderTotalValue.setOnClickListener(onClickListener);
        //RecyclerView
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainOrderItems = new ArrayList<>();
        memSrl = sharedPreferences.getInt("MemSrl", 0);
        orderSrl = sharedPreferences.getInt("OrderSrl", 0);

        mainOrder = realm.where(MemOrder.class).equalTo("MemSrl", memSrl).and().equalTo("Srl", orderSrl).findFirst();
        int size = mainOrder.getMemOrderItems().size();
        if (mainOrder != null && size > 0) {
            MemOrderItem temp;
            MemOrderItem unManagedtemp;
            for (int i = 0; i < size; i++) {  // because of the managed and unmanaged REALM object, forced to do such !!
                temp = mainOrder.getMemOrderItems().get(i);
                unManagedtemp = new MemOrderItem();
                unManagedtemp.setCatObj(temp.getCatObj());
                unManagedtemp.setItemObj(temp.getItemObj());
                unManagedtemp.setImage(temp.getImage());
                unManagedtemp.setCount(temp.getCount());
                unManagedtemp.setOptions(temp.getOptions());
                unManagedtemp.setOrderObj(temp.getOrderObj());
                mainOrderItems.add(unManagedtemp);
            }
            this.calulateTotalPrice();
        }
        adapter = new OrderListAdapter(context, mainOrderItems, this, this, true);
        ordersClerView.setLayoutManager(new LinearLayoutManager(context));
        ordersClerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        SimpleTouchCallback touchCallback = new SimpleTouchCallback(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(touchCallback);
        helper.attachToRecyclerView(ordersClerView);
    }

    public void AddToOrder(Item item) {
        boolean found = false;
        for (int i = 0; !found && i < mainOrderItems.size(); i++) {
            MemOrderItem memOrderItem = mainOrderItems.get(i);
            if (memOrderItem.getCatObj().getSrl() == item.getCatObj().getSrl() && memOrderItem.getItemObj().getSrl() == item.getSrl())
                found = true;
        }
        if (!found) {
            realm.beginTransaction();
            MemOrderItem memOrderItem = new MemOrderItem();
            memOrderItem.setCatObj(item.getCatObj());
            memOrderItem.setItemObj(item);
            memOrderItem.setImage(item.getImage());
            memOrderItem.setCount((byte) 0);
            memOrderItem.setOptions((byte) 0);
            mainOrderItems.add(memOrderItem);
            realm.commitTransaction();
            adapter.notifyItemInserted(mainOrderItems.size());
        } else
            Util.toast(getActivity(), R.string.already_existed_error);
    }


    @Override
    public void OnPlusMinusClick(int id, byte buttonType) {
        MemOrderItem temp = mainOrderItems.get(id);
        byte count = temp.getCount();
        switch (buttonType) {
            case 1: //Remove Item from Order
            {
                if (count > 0) {
                    temp.setCount(--count);
                    totalPrice -= temp.getItemObj().getPrice();
                }
            }
            break;
            case 2://Add Item to Order
            {
                temp.setCount(++count);
                totalPrice += temp.getItemObj().getPrice();
            }
            break;
        }
        adapter.notifyItemChanged(id);
        orderTotalValue.setText(Util.cnvToCurrency(totalPrice) + getString(R.string.currency_unit));
    }

    public void calulateTotalPrice() { // it is run On Swipe
        int total = 0;
        for (int i = 0; i < mainOrderItems.size(); i++) {
            MemOrderItem temp = mainOrderItems.get(i);
            total += temp.getCount() * temp.getItemObj().getPrice();
        }
        totalPrice = total;
        orderTotalValue.setText(Util.cnvToCurrency(totalPrice) + getString(R.string.currency_unit));
    }

    @Override
    public void onUpdatePrice(int Price) { // it is run On Swipe
        if (Price > 0) {
            totalPrice -= Price;
            orderTotalValue.setText(Util.cnvToCurrency(totalPrice) + getString(R.string.currency_unit));
        }

    }

}
