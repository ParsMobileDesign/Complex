package ir.parsmobiledesign.quantum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import io.realm.Realm;
import ir.parsmobiledesign.quantum.Interfaces.ItemClickInterface;
import ir.parsmobiledesign.quantum.Realm.Configuration;
import ir.parsmobiledesign.quantum.Realm.MemOrder;
import ir.parsmobiledesign.quantum.RecyclerViews.SuspendedOrdersAdapter;
import ir.parsmobiledesign.quantum.Utility.Util;
import saman.zamani.persiandate.PersianDate;

public class SuspendedOrders extends AppCompatActivity implements ItemClickInterface {

    Context context;
    Realm realm;
    Configuration configObj;
    ArrayList<MemOrder> MemOrderList = new ArrayList<>();
    RecyclerView susOrderList;
    SuspendedOrdersAdapter susAdapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String suspendType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suspended_orders);
        context = getApplicationContext();
        suspendType=getIntent().getStringExtra("SuspendType");
        IdSetter();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void notifyDatasetChanged(int memSrl, int orderSrl) {
        MemOrder temp;
        boolean found = false;
        for (int i = 0; !found && i < MemOrderList.size(); i++) {
            temp = MemOrderList.get(i);
            if (!temp.isValid() || (temp.getMemSrl() == memSrl && temp.getSrl() == orderSrl)) {
                MemOrderList.remove(i);
                found = true;
            }
        }
        if (susAdapter != null)
            susAdapter.notifyDataSetChanged();
        if (MemOrderList.size() == 0) {
            Intent intent = new Intent(SuspendedOrders.this, OrderTypeSelection.class);
            startActivity(intent);
        }
    }

    private void IdSetter() {
        //Variable Initialization
        realm = Util.getRealmInstance();
        configObj = Util.GetConfiguration();
        susOrderList = findViewById(R.id.SuspendOrders);
        sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefName), MODE_PRIVATE);
        PersianDate date = new PersianDate();

        //  Recycler fill
        MemOrderList.addAll(realm.where(MemOrder.class).equalTo(suspendType, true).and().equalTo("Payed",false).findAll());
        if (MemOrderList.size() > 0) {
            LinearLayoutManager linearManager = new LinearLayoutManager(this);
            susOrderList.setLayoutManager(linearManager);
            susOrderList.setItemAnimator(new DefaultItemAnimator());
            susAdapter = new SuspendedOrdersAdapter(context, MemOrderList, this);
            susOrderList.setAdapter(susAdapter);

            // Toolbar Initialization
            Toolbar mToolbar = findViewById(R.id.Toolbar);
            Util.setActionBarProperties(context, this, mToolbar, configObj);
        } else {
            Util.toast(this, "هیچ سفارش جاری وجود ندارد !");
            Intent intent = new Intent(SuspendedOrders.this, OrderTypeSelection.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, OrderTypeSelection.class);
        startActivity(intent);
    }

    @Override
    public void OnItemClick(int id) {
        MemOrder temp = MemOrderList.get(id);
        if (temp != null && temp.isValid()) {
            editor = sharedPreferences.edit();
            editor.putInt("MemSrl", temp.getMemSrl());
            editor.putInt("OrderSrl", temp.getSrl());
            editor.commit();
            SuspendedOrderDialog dialog = new SuspendedOrderDialog();
            dialog.show(getSupportFragmentManager(), "Tag");
        } else
            Util.toast(context, "سفارش یافت نشد !");

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }
}
