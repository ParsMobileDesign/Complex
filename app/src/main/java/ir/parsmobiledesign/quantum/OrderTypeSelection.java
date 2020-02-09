package ir.parsmobiledesign.quantum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ir.parsmobiledesign.quantum.Realm.MemOrder;
import ir.parsmobiledesign.quantum.Utility.Util;

public class OrderTypeSelection extends AppCompatActivity {
    Context context;
    private static final String TAG = "OrderTypeSelection";
    Button btnNewOrder, btnSuspendOrder, btnAcceptOrder;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ViewGroup.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnNewOrder: {
                    MemOrder temp = new MemOrder(context);
                    temp.Save();
                    editor = sharedPreferences.edit();
                    editor.putString("OrderType", "new");
                    editor.putInt("MemSrl", temp.getMemSrl());
                    editor.putInt("OrderSrl", temp.getSrl());
                    editor.apply();
                    Intent intent = new Intent(context, ItemSelection.class);
                    startActivity(intent);
                }
                break;
                case R.id.btnSuspendOrder: {
                    Intent intent = new Intent(context, SuspendedOrders.class);
                    intent.putExtra("SuspendType","Suspended");
                    startActivity(intent);
                }
                break;
                case R.id.btnAcceptOrder: {
                    Intent intent = new Intent(context, SuspendedOrders.class);
                    intent.putExtra("SuspendType","Accepted");
                    startActivity(intent);
                }
                break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(context, ActSelection.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_type_selection);
        context = this;
        IdSetter();
    }

    private void IdSetter() {
        sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefName), MODE_PRIVATE);
        btnNewOrder = findViewById(R.id.btnNewOrder);
        btnSuspendOrder = findViewById(R.id.btnSuspendOrder);
        btnAcceptOrder = findViewById(R.id.btnAcceptOrder);

        btnNewOrder.setOnClickListener(onClickListener);
        btnSuspendOrder.setOnClickListener(onClickListener);
        btnAcceptOrder.setOnClickListener(onClickListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Util.DisabledHomeButton(context, this);
    }
}
