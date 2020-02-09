package ir.parsmobiledesign.quantum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;

import io.realm.Realm;
import ir.parsmobiledesign.quantum.Interfaces.AcceptSyncInterface;
import ir.parsmobiledesign.quantum.Realm.MemOrder;
import ir.parsmobiledesign.quantum.Utility.Pahpat;
import ir.parsmobiledesign.quantum.Utility.ProgressDialog;
import ir.parsmobiledesign.quantum.Utility.Util;


public class YesNoSuspendDialog extends DialogFragment implements AcceptSyncInterface {
    Button okBtn, acceptBtn, suspendBtn;
    Context context;
    int memSrl, orderSrl;
    Realm realm;
    Handler receiverHandler;
    Bundle bundle;
    //TextView cancelBtn;
    EditText tableNo;
    MemOrder order;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    FragmentManager fragmentManager;
    SharedPreferences.Editor editor;
    String TAG = "YesNoDialog";
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int tableNumber = Util.cnvToInteger(tableNo.getText().toString());
            if (tableNumber < 255) {
                switch (v.getId()) {
                    case R.id.dialogOk: {
                        if (Util.isOnline(context)) {
                            if (bundle != null) {
                                realm.beginTransaction();
                                order.setTableNo((byte)tableNumber);
                                realm.commitTransaction();
                                String totalAmount = bundle.getString("totalAmount");
                                PahpatPay(totalAmount);
                            }
                            dismiss();
                        } else
                            Util.showInfoDialog(context, R.string.WiFiDisconnected, R.string.wiFiDisconnected_title);
                    }
                    break;
                    case R.id.dialogAccept: {
                        progressDialog.show(fragmentManager, TAG);
                        if (!order.isAccepted()) {
                            realm.beginTransaction();
                            order.setAccepted(true);
                            order.setSuspended(false);
                            order.setTableNo((byte)tableNumber);
                            realm.commitTransaction();
                        }
                        try {
                            Util.syncAcceptOrder(YesNoSuspendDialog.this, order);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case R.id.dialogSuspend: {
                        if (order != null) {
                            realm.beginTransaction();
                            order.setTableNo((byte)tableNumber);
                            order.setSuspended(true);
                            realm.commitTransaction();
                        }
                        dismiss();
                        Intent intent = new Intent(getActivity(), OrderTypeSelection.class);
                        startActivity(intent);

                    }
                    break;
                }
            }
            else
                Util.toast(context, R.string.wrongTableNumber );
        }
    };

    private void PahpatPay(String iAmount) {
        try {
            Pahpat.purchaseTxn(getActivity(), receiverHandler, iAmount, "09353391873", "123", "IRR", "FA");
        } catch (Pahpat.PahpatException e) {
            Util.showInfoDialog(getActivity(), e.getMessage(), "خطا", R.drawable.exclamation_mark);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_yes_no_suspend, container, false);
    }

    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout((int) (size.x * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        receiverHandler = new Handler();
        realm = Realm.getDefaultInstance();
        okBtn = view.findViewById(R.id.dialogOk);
        acceptBtn = view.findViewById(R.id.dialogAccept);
        fragmentManager = getActivity().getSupportFragmentManager();
        //cancelBtn = view.findViewById(R.id.dialogCancel);
        suspendBtn = view.findViewById(R.id.dialogSuspend);
        tableNo = view.findViewById(R.id.tableNo);
        acceptBtn = view.findViewById(R.id.dialogAccept);

        okBtn.setOnClickListener(clickListener);
        suspendBtn.setOnClickListener(clickListener);
        acceptBtn.setOnClickListener(clickListener);

        // cancelBtn.setOnClickListener(clickListener);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.sharedPrefName), Context.MODE_PRIVATE);
        //orderType = sharedPreferences.getString("OrderType", "");
        bundle = getArguments();
        memSrl = sharedPreferences.getInt("MemSrl", 0);
        orderSrl = sharedPreferences.getInt("OrderSrl", 0);
        order = realm.where(MemOrder.class).equalTo("MemSrl", memSrl).and().equalTo("Srl", orderSrl).findFirst();
        if (order.isAccepted()) {
            acceptBtn.setVisibility(View.GONE);
            suspendBtn.setVisibility(View.GONE);
        } else {
            acceptBtn.setVisibility(View.VISIBLE);
            suspendBtn.setVisibility(View.VISIBLE);

        }
        tableNo.setText(String.valueOf(order.getTableNo()));
        tableNo.selectAll();
        tableNo.setRawInputType(android.content.res.Configuration.KEYBOARD_12KEY);
        progressDialog = new ProgressDialog();
    }

    @Override
    public void onAccept() {
        progressDialog.dismiss();
        Intent intent = new Intent(getActivity(), OrderTypeSelection.class);
        startActivity(intent);
    }

    @Override
    public void onFail(String message) {
        progressDialog.dismiss();
        Util.showInfoDialog(context, message, "خطا");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
