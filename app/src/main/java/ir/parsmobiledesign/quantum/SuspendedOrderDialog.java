package ir.parsmobiledesign.quantum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import ir.parsmobiledesign.quantum.Realm.MemOrder;
import ir.parsmobiledesign.quantum.Realm.MemOrderItem;
import ir.parsmobiledesign.quantum.RecyclerViews.OrderListAdapter;
import ir.parsmobiledesign.quantum.Utility.Util;

public class SuspendedOrderDialog extends DialogFragment {
    private Realm realm;
    TextView tableNo, totalAmount;
    private SharedPreferences.Editor editor;
    private MemOrder order;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnReload: {
                    Intent intent = new Intent(getActivity(), ItemSelection.class);
                    startActivity(intent);
                }
                break;
                case R.id.btnDelete: {
                    int memSrl = order.getMemSrl(), orderSrl = order.getSrl();
                    realm.beginTransaction();
                    order.deleteFromRealm();
                    realm.commitTransaction();
                    SuspendedOrders temp = (SuspendedOrders) getActivity();
                    temp.notifyDatasetChanged(memSrl, orderSrl);
                    dismiss();
                }
                break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_suspended_order, container, false);
    }

    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout((int) (size.x * 0.9), (int) (size.y * 0.7));
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Context context = getActivity();
        realm = Realm.getDefaultInstance();
        tableNo = view.findViewById(R.id.dialogTableNo);
        totalAmount = view.findViewById(R.id.dialogTotalPrice);
        Button btnReload = view.findViewById(R.id.btnReload);
        Button btnDelete = view.findViewById(R.id.btnDelete);
        btnReload.setOnClickListener(clickListener);
        btnDelete.setOnClickListener(clickListener);
        RecyclerView susOrderList = view.findViewById(R.id.susOrderItemList);
        ArrayList<MemOrderItem> orderItems = new ArrayList<>();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.sharedPrefName), Context.MODE_PRIVATE);
        // Bundle bundle = getArguments();
        int memSrl = sharedPreferences.getInt("MemSrl", 0);
        int orderSrl = sharedPreferences.getInt("OrderSrl", 0);
        order = realm.where(MemOrder.class).equalTo("MemSrl", memSrl).and().equalTo("Srl", orderSrl).findFirst();
        if (order == null) {
            Util.toast(context, "این سفارش یافت نشد !");
            dismiss();
        } else {
            tableNo.setText("شماره میز :" + order.getTableNo());
            totalAmount.setText(Util.cnvToCurrency(order.getTotalAmount()) + getString(R.string.currency_unit));
            orderItems.addAll(order.getMemOrderItems());
            OrderListAdapter adapter = new OrderListAdapter(context, orderItems, null, null, false);
            susOrderList.setAdapter(adapter);
            susOrderList.setLayoutManager(new LinearLayoutManager(context));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
