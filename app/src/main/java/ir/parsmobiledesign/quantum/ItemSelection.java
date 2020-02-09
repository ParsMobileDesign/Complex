package ir.parsmobiledesign.quantum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.realm.RealmList;
import ir.parsmobiledesign.quantum.Interfaces.ItemClickInterface;
import ir.parsmobiledesign.quantum.Realm.Category;
import ir.parsmobiledesign.quantum.Realm.Configuration;
import ir.parsmobiledesign.quantum.Realm.Item;
import ir.parsmobiledesign.quantum.RecyclerViews.ItemListAdapter;
import ir.parsmobiledesign.quantum.Utility.CategoryInfo;
import ir.parsmobiledesign.quantum.Utility.Pahpat;
import ir.parsmobiledesign.quantum.Utility.ProgressDialog;
import ir.parsmobiledesign.quantum.Utility.Util;
import io.realm.Realm;

public class ItemSelection extends AppCompatActivity implements ItemClickInterface, Pahpat.Receiver {
    Context context;
    Realm realm;
    Configuration configObj;
    Spinner spinner;
    List<Category> cats;
    Category selectedCategory;
    ArrayList<Item> selectedItemList = new ArrayList<>();
    RecyclerView itemclerView;
    ItemListAdapter itemsAdapter;
    OrderFragment ordersFragment;
    Handler receiverHandler;
    SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeContainer;
    JSONArray CategoryJson;
    JSONArray itemsJson;
    JSONObject item;
    ProgressDialog progressDialog;
    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedCategory = cats.get(position);
            selectedItemList.clear();
            selectedItemList.addAll(selectedCategory.getItems());
            itemsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_selection);
        context = getApplicationContext();
        IdSetter();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                UpdateCategoryInfo();

            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    private void IdSetter() {
        //Variable Initialization
        realm = Util.getRealmInstance();
        configObj = Util.GetConfiguration();
        spinner = findViewById(R.id.itemSpinner);
        sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefName), Context.MODE_PRIVATE);
        swipeContainer = findViewById(R.id.swipeContainer);
        // Spinner and ItemSelection Recycler fill
        cats = realm.where(Category.class).findAll();
        ir.parsmobiledesign.quantum.Adapters.SpinnerAdapter arrayAdapter = new ir.parsmobiledesign.quantum.Adapters.SpinnerAdapter(context, android.R.layout.simple_spinner_item, cats);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(onItemSelectedListener);

        itemclerView = findViewById(R.id.itemRecyclerView);
        selectedCategory = (Category) spinner.getSelectedItem();
        selectedItemList.addAll(selectedCategory.getItems());
        progressDialog = new ProgressDialog();
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        itemclerView.setLayoutManager(linearManager);
        //itemclerView.setItemAnimator(new DefaultItemAnimator());
        itemsAdapter = new ItemListAdapter(context, selectedItemList, this);
        itemclerView.setAdapter(itemsAdapter);

        // Fragment Initialization
        ordersFragment = (OrderFragment) getSupportFragmentManager().findFragmentById(R.id.ItemSelection_orders);
        receiverHandler = new Handler();

        // Toolbar Initialization
//        Toolbar mToolbar = findViewById(R.id.Toolbar);
//        Util.setActionBarProperties(context, this, mToolbar, configObj);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(context, OrderTypeSelection.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void OnItemClick(int id) {
        if (ordersFragment != null)
            ordersFragment.AddToOrder(selectedItemList.get(id));
    }


    @Override
    protected void onPause() {
        super.onPause();
        Util.DisabledHomeButton(context, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onReceiveResult(int serviceId, int resultCode, Bundle resultData) throws Pahpat.PahpatException, RemoteException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        if (serviceId == Pahpat.GET_STATUS) {
            if (resultData == null) {
                Util.showInfoDialog(ItemSelection.this, "پهپات در دسترس نیست !", "خطا");
                return;
            }
        }
        if (serviceId == Pahpat.GOOD_PAYMENT) {
            if (resultCode == Pahpat.TXN_SUCCESS) {
                try {
                    String approvalCode = resultData.getString("ApprovalCode", "");
                    Pahpat.confirmTxn(ItemSelection.this, receiverHandler, approvalCode, true);
                } catch (Pahpat.PahpatException e) {
                    Util.showInfoDialog(ItemSelection.this, resultData.getString("Reason"), "خطا", R.drawable.exclamation_mark);
                }
            } else
                Util.showInfoDialog(ItemSelection.this, "error", "error");
        } else if (serviceId == Pahpat.GOOD_CONFIRM) {
            boolean mustFinish = resultData.getBoolean("MustFinish");
            if (resultCode == Pahpat.TXN_SUCCESS && mustFinish) {
                // LogData(resultData);
                // LogData(bundle);
            } else {
                //Post Error Log to Server
//                if (bundle != null)
//                    LogErrorResultData(bundle);
                Util.showInfoDialog(ItemSelection.this, resultData.getString("Reason", ""), "خطا");
            }
        } else if (serviceId == Pahpat.TXN_FAILED) {
//            if (bundle != null)
//                LogErrorResultData(bundle);
            Util.showInfoDialog(ItemSelection.this, "error", "error");
        }
    }

    private void UpdateCategoryInfo() {
        AsyncHttpClient client = new AsyncHttpClient();
        final JSONObject json = new JSONObject();
        try {
            json.put("UserName", configObj.getUserName());
            json.put("Password", configObj.getPassword()); //Get From UserId Control
            json.put("CategoryInfo", CategoryInfo.GetCategoryInfo(realm));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
        client.setMaxRetriesAndTimeout(1, 4000);
        client.post(context, Util.UpdateCategory, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // progressDialog.dismiss();
                swipeContainer.setRefreshing(false);
                if (headers != null || response != null) {
                    JSONObject JSON = null;
                    try {
                        Object ChkCategoryObj = response.get("CatJson");
                        Object ChkItemObj = response.get("Itemjson");
                        Object ChkDeleteCategoryitem = response.get("CategoryitemDeleted");
                        if (ChkCategoryObj != null && !ChkCategoryObj.toString().equals("[]")) {
                            CategoryJson = response.getJSONArray("CatJson");
                            for (int i = 0; i < CategoryJson.length(); i++) {
                                final JSONObject PJsonOj = CategoryJson.getJSONObject(i);

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        try {
                                            Category CatObj = realm.where(Category.class).equalTo("Srl", Util.cnvToByte(PJsonOj.getString("Srl"))).findFirst();
                                            if (CatObj == null) {
                                                CatObj = realm.createObject(Category.class);
                                                CatObj.setSrl(Util.cnvToByte(PJsonOj.getString("Srl")));
                                            }
                                            CatObj.setTitle(PJsonOj.getString("Title"));
                                            CatObj.setVersion(Util.cnvToInteger(PJsonOj.getString("Version")));
                                            //CatObj.SetIcon(PJsonOj.getString("Icon"));
                                           realm.insertOrUpdate(CatObj);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            }
                        }//Update Category

                        if (ChkItemObj != null && !ChkItemObj.toString().equals("[]")) {

                            itemsJson = response.getJSONArray("Itemjson");
                            if (itemsJson != null) {
                                for (int i = 0; i < itemsJson.length(); i++) {

                                    final JSONObject Obj = itemsJson.getJSONObject(i);
                                    byte CatSrl = Util.cnvToByte(Obj.getString("CatSrl"));
                                    final byte Srl = Util.cnvToByte(Obj.getString("Srl"));
                                    final Category catobj = realm.where(Category.class).equalTo("Srl", CatSrl).findFirst();
                                    if (catobj != null) {
                                        RealmList<Item> checl = catobj.getItems();
                                        if (catobj.getItems().size() > 0) {
                                            final Item ItemObj = catobj.getItems().where().equalTo("Srl", Srl).findFirst();
                                            if (ItemObj == null) {
                                                //cause error execute write transaction
                                                realm.executeTransaction(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        try {
                                                            Item itm = realm.createObject(Item.class);
                                                            itm.setSrl(Srl);
                                                            itm.setCatObj(catobj);
                                                            itm.setTitle(Obj.getString("Title"));
                                                            itm.setPrice(Util.cnvToInteger(Obj.getString("Price")));
                                                            itm.setVersion(Util.cnvToInteger(Obj.getString("Version")));
                                                            itm.setImage(Obj.getString("Image"));
                                                            //realm.insertOrUpdate(itm);
                                                            catobj.getItems().add(itm);

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }

                                            else { //Just Update Object
                                                realm.beginTransaction();
                                                ItemObj.setTitle(Obj.getString("Title"));
                                                ItemObj.setPrice(Util.cnvToInteger(Obj.getString("Price")));
                                                ItemObj.setVersion(Util.cnvToInteger(Obj.getString("Version")));
                                                ItemObj.setImage(Obj.getString("Image"));
                                                realm.commitTransaction();
                                            }
                                        } else
                                            Toast.makeText(context, "بروزرسانی بعلت تناقض اطلاعات صورت نپذیرفت", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        } // Update CategoryItem

                        if (ChkDeleteCategoryitem != null && !ChkDeleteCategoryitem.toString().equals("[]")) { //delete CategoryItem
                            CategoryJson = null;
                            CategoryJson = response.getJSONArray("CategoryitemDeleted");
                            for (int i = 0; i < CategoryJson.length(); i++) {
                                JSONObject PObj = CategoryJson.getJSONObject(i);
                                byte CatSrl = Util.cnvToByte(PObj.getString("CatSrl"));
                                final byte Srl = Util.cnvToByte(PObj.getString("Srl"));
                                final Category catobj = realm.where(Category.class).equalTo("Srl", CatSrl).findFirst();
                                if (catobj != null) {
//                                    realm.executeTransaction(new Realm.Transaction() {
//                                        @Override
//                                        public void execute(Realm realm) {
//
//                                        }
//                                    });
                                    realm.beginTransaction();
                                    Item ItemObj = catobj.getItems().where().equalTo("Srl", Srl).findFirst();
                                   // Item ItemObj = realm.where(Item.class).equalTo("Srl", Srl).findFirst();
                                    if (ItemObj != null) {

                                        catobj.getItems().remove(ItemObj);
                                        ItemObj.deleteFromRealm();
                                        // ItemObj.deleteFromRealm();
                                    }
                                    realm.commitTransaction();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    NotifyAdapters();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    // progressDialog.dismiss();
                    swipeContainer.setRefreshing(false);
                    if (throwable instanceof Exception) {
                        if (errorResponse != null && !errorResponse.getString("Msg").isEmpty())
                            Util.showInfoDialog(ItemSelection.this, errorResponse.getString("Msg"), "Error");
                        else
                            Util.showInfoDialog(ItemSelection.this, "Error in Connecting to Website... Please try again later.", "Error");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                try {
                    //   progressDialog.dismiss();
                    swipeContainer.setRefreshing(false);
                    Util.showInfoDialog(ItemSelection.this, responseString, "Error");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            protected Object parseResponse(byte[] responseBody) throws JSONException {
                return super.parseResponse(responseBody);
            }
        });

    }

    private void NotifyAdapters() {
        swipeContainer.setRefreshing(false);

        cats = realm.where(Category.class).findAll();

        ir.parsmobiledesign.quantum.Adapters.SpinnerAdapter arrayAdapter = new ir.parsmobiledesign.quantum.Adapters.SpinnerAdapter(context, android.R.layout.simple_spinner_item, cats);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(onItemSelectedListener);
        selectedCategory = (Category) spinner.getSelectedItem();
        selectedItemList.addAll(selectedCategory.getItems());
        //itemsAdapter.notifyDataSetChanged();
        itemsAdapter = new ItemListAdapter(context, selectedItemList, this);
        itemclerView.setAdapter(itemsAdapter);
    }

}
