package ir.parsmobiledesign.quantum;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;



import ir.parsmobiledesign.quantum.Realm.Category;
import ir.parsmobiledesign.quantum.Realm.Configuration;
import ir.parsmobiledesign.quantum.Realm.Item;
import ir.parsmobiledesign.quantum.Utility.ProgressDialog;
import ir.parsmobiledesign.quantum.Utility.Util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.realm.Realm;

public class ActSelection extends AppCompatActivity {
    private static final String TAG = "ActSelection";
    Button btnCafe, btnEvent, btnEscapeRoom, btnBoardGame;
    Context context;
    Configuration configObj;
    Realm realm;
    ProgressDialog progressDialog;
    Toolbar mToolbar;
    byte xType;
    Object jj, ParsResponse;
    String[] JsonRet = new String[2];
    ArrayList<Category> CatList;
    FragmentManager fragmentManager;
    JSONArray itemsJson;
    JSONObject item;
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
               // if (Util.isOnline(context)) {
                    switch (v.getId()) {
                        case R.id.btnBoardGame: {
                          Util.toast(context, "در حال ساخت");
                        }
                        break;
                        case R.id.btnCafe: {
                            Intent intent = new Intent(context, OrderTypeSelection.class);
                            startActivity(intent);

                        }
                        break;

                        case R.id.btnEscapeRoom: {
                            Util.toast(context, "در حال ساخت");

                        }

                        break;
                        case R.id.btnEvent: {
                            Util.toast(context, "در حال ساخت");

                        }
                        break;
                    }
//                } else {
//                    //progressDialog.dismiss();
//                    Util.showInfoDialog(ActSelection.this, R.string.WiFiDisconnected, R.string.wiFiDisconnected_title);
//                }
            } catch (Exception e) {// (IOException | CertificateException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                Util.showInfoDialog(ActSelection.this, e.getMessage(), "اخطار");
                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_selection);
        IdSetter();
        //CatList = Util.getCategory();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return !(keyCode == KeyEvent.KEYCODE_BACK);
    }

    private void IdSetter() {
        context = this;
        btnCafe = findViewById(R.id.btnCafe);
        btnBoardGame = findViewById(R.id.btnBoardGame);
        btnEscapeRoom = findViewById(R.id.btnEscapeRoom);
        btnEvent = findViewById(R.id.btnEvent);
        btnCafe.setOnClickListener(onClickListener);
        btnBoardGame.setOnClickListener(onClickListener);
        btnEscapeRoom.setOnClickListener(onClickListener);
        btnEvent.setOnClickListener(onClickListener);
       // fragmentManager = getSupportFragmentManager();
    }

    private void prepareCategoryListOnline() {
//        Bundle bundle = new Bundle();
//        bundle.putString("Message", "Loading Contents for the first time. Please wait...");
//        progressDialog.setArguments(bundle);
//        progressDialog.show(fragmentManager, TAG);
        AsyncHttpClient client = new AsyncHttpClient();
        final JSONObject json = new JSONObject();
//        try {
//            json.put("PhoneNumber", configObj.getPhoneNumber());
//            json.put("Password", configObj.getPassword()); //Get From UserId Control
//        } catch (JSONException e1) {
//            e1.printStackTrace();
//        }
        StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
        client.setMaxRetriesAndTimeout(1, 10000);
        client.post(context, "Util.ProviderArr + Util.GetCategory", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (headers != null || response != null) {
                    realm.beginTransaction();
                    for (int i = 0; i < response.length(); i++) {
                        Category cat = new Category();
                        try {
                            JSONObject json = response.getJSONObject(i);
                            cat.setSrl(Util.cnvToByte(json.getString("Srl")));
                            cat.setTitle(json.getString("Title"));
                            cat.setVersion(json.getInt("Version"));
                            cat.setOptions(json.getInt("Options"));
                            Object ChkUnitsObj = (JSONArray) json.get("Items");
                            if (!ChkUnitsObj.equals(null)) {
                                itemsJson = json.getJSONArray("Items");
                                for (int j = 0; j < itemsJson.length(); j++) {
                                    item = itemsJson.getJSONObject(j);
                                    Item tempItem = realm.createObject(Item.class);
                                    try {
                                        byte srl = Util.cnvToByte(item.getString("Srl"));
                                        String title = item.getString("Title");
                                        int price = Util.cnvToInteger(item.getString("Price"));
                                        String image = item.getString("Image");
                                        int version = item.getInt("Version");
                                        int options = item.getInt("Options");
                                        tempItem.setSrl(srl);
                                        tempItem.setTitle(title);
                                        tempItem.setPrice(price);
                                        tempItem.setImage(image);
                                        tempItem.setVersion(version);
                                        tempItem.setOptions(options);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    cat.getItems().add(tempItem);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        realm.insert(cat);
                    }
                    realm.commitTransaction();
                    // mAdapter.notifyDataSetChanged();
                    // progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    // progressDialog.dismiss();
                    if (throwable instanceof Exception) {
                        Util.showInfoDialog(ActSelection.this, throwable.getMessage(), "Error");
                        return;
                    } else
                        Util.showInfoDialog(ActSelection.this, errorResponse.getString("Msg"), "Error");
                    return;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                try {
                    //progressDialog.dismiss();
                    Util.showInfoDialog(ActSelection.this, responseString, "Error");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onFailure(statusCode, headers, responseString, throwable);
            }

//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                progressDialog.dismiss();
//                if (!response.equals("") && response != null) {
//                    JDataObject = response;
//                }
//                super.onSuccess(statusCode, headers, response);
//            }

            @Override
            protected Object parseResponse(byte[] responseBody) throws JSONException {
                ParsResponse = super.parseResponse(responseBody);
                if (ParsResponse != null || ParsResponse != "") {
                    if (ParsResponse instanceof JSONObject) {
                        JSONObject JDataObject = (JSONObject) ParsResponse;
                        return JDataObject;
                    } else if (ParsResponse instanceof JSONArray) {
                        JSONArray JDataArray = (JSONArray) ParsResponse;
                        return JDataArray;
                    }
                }
                return null;

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


}
