package ir.parsmobiledesign.quantum;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ir.parsmobiledesign.quantum.Realm.Configuration;

import ir.parsmobiledesign.quantum.Utility.CryptoHandler;

import ir.parsmobiledesign.quantum.Utility.ProgressDialog;
import ir.parsmobiledesign.quantum.Utility.Util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.realm.Realm;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    Button Loginbtn;
    EditText edUsername, edPassword, edProviderAddr;
    Context context;
    Realm realm;
    ProgressDialog progressDialog;
    FragmentManager fragmentManager;
    Configuration configObj;
    Handler receiverHandler;
    Object JsonRes;
    String deviceSrl;
    String UserName;
    String Password;
    JSONArray itemsJson;
    JSONObject item;
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnLogin: {
                    if (Util.isOnline(context)) {
                        if (!edUsername.getText().toString().isEmpty() && !edPassword.getText().toString().isEmpty()) {
                            progressDialog.show(fragmentManager, TAG);
                            try {
                                LoginProcess();
                            } catch (Exception ex) {
                                progressDialog.dismiss();
                                Util.showInfoDialog(Login.this, ex.getMessage(), "خطا");
                            }
                        } else
                            Util.showInfoDialog(Login.this, R.string.UserPassRequired, R.string.error);
                        //  } else
                        //        Util.showInfoDialog(Login.this, "آدرس سرور را به https تغيير دهيد!", "اخطار");
                    } else
                        Util.showInfoDialog(Login.this, R.string.WiFiDisconnected, R.string.error);
                }
                break;
            }
        }
    };




    private void LoginProcess() {
        AsyncHttpClient client = new AsyncHttpClient();
        final JSONObject json = new JSONObject();
        UserName = CryptoHandler.encryptAndEncode(edUsername.getText().toString());
        Password = CryptoHandler.encryptAndEncode(edPassword.getText().toString());
        try {
            json.put("UserName", UserName);
            json.put("Password", Password);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
        client.setMaxRetriesAndTimeout(0, 4000);
        client.post(context, Util.Verify, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();
                if (headers != null || response != null) {

                    if (Util.GetConfiguration() == null) {
                        SaveConfigData(UserName, Password);
                        try {
                            Util.prepareCategoryList(Login.this);
                            Intent intent = new Intent(Login.this, ActSelection.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Bundle bundle = new Bundle();
                            bundle.putString("پیغام", "خطا در دریافت پیکربندی از سرور... پشتیبان را آگاه سازید!");
                            progressDialog.setArguments(bundle);
                            progressDialog.show(fragmentManager, TAG);
                        }
                    }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    progressDialog.dismiss();
                    if (throwable instanceof Exception) {
                        if (errorResponse != null && !errorResponse.getString("Msg").isEmpty())
                            Util.showInfoDialog(Login.this, errorResponse.getString("Msg"), "Error");
                        else
                            Util.showInfoDialog(Login.this, "Error in Connecting to Website... Please try again later.", "Error");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                try {
                    progressDialog.dismiss();
                    Util.showInfoDialog(Login.this, responseString, "Error");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            protected Object parseResponse(byte[] responseBody) throws JSONException {
                try {
                    JsonRes = super.parseResponse(responseBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (JsonRes == "") {
                    Util.showInfoDialog(Login.this, "خطا در بازیابی اطلاعات  ", "خطا");
                }
                return JsonRes;
            }
        });
    }

//    private void prepareCategoryList() throws JSONException {
//        Bundle bundle = new Bundle();
//        bundle.putString("پیغام", "در حال دریافت اطلاعات محصول ، لطفا شکیبا باشید..");
//        progressDialog.setArguments(bundle);
//        progressDialog.show(fragmentManager, TAG);
//        AsyncHttpClient client = new AsyncHttpClient();
//        final JSONObject json = new JSONObject();
//        json.put("UserName", configObj.getUserName());
//        json.put("Password",  configObj.getPassword()); //Get From UserId Control //Get From UserId Control
//        StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
//        client.setMaxRetriesAndTimeout(1, 10000);
//        client.post(context, Util.GetCategory, entity, "application/json", new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                progressDialog.dismiss();
//                if (headers != null || response != null) {
//                    realm.beginTransaction();
//                    for (int i = 0; i < response.length(); i++) {
//                        Category  cat = null;
//                        try {
//                            JSONObject json = response.getJSONObject(i);
//                            cat = realm.createObject(Category.class,Util.cnvToByte(json.getString("Srl")));
//                            cat.setTitle(json.getString("Title"));
//                            cat.setVersion(json.getInt("Version"));
//                            cat.setOptions(json.getInt("Options"));
//                            Object ChkUnitsObj = (JSONArray) json.get("Items");
//                            if (!ChkUnitsObj.equals(null)) {
//                                itemsJson = json.getJSONArray("Items");
//                                for (int j = 0; j < itemsJson.length(); j++) {
//                                    item = itemsJson.getJSONObject(j);
//                                    Item tempItem = null;
//                                    try {
//                                        tempItem = realm.createObject(Item.class); //,srl
//                                        tempItem.setCatObj(cat);
//                                        tempItem.setSrl( Util.cnvToByte(item.getString("Srl")));
//                                        tempItem.setTitle( item.getString("Title"));
//                                        tempItem.setPrice( Util.cnvToInteger(item.getString("Price")());
//                                        tempItem.setImage( item.getString("Image"));
//                                        tempItem.setVersion(item.getInt("Version"));
//                                        tempItem.setOptions( item.getInt("Options"));
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                    cat.getItems().add(tempItem);
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        realm.insert(cat);
//                    }
//                    realm.commitTransaction();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                try {
//                     progressDialog.dismiss();
//                    if (throwable instanceof Exception) {
//                        Util.showInfoDialog(Login.this, throwable.getMessage(), "Error");
//                        return;
//                    } else
//                        Util.showInfoDialog(Login.this, errorResponse.getString("Msg"), "Error");
//                    return;
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                try {
//                    progressDialog.dismiss();
//                    Util.showInfoDialog(Login.this, responseString, "Error");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                super.onFailure(statusCode, headers, responseString, throwable);
//            }
//
//
//            @Override
//            protected Object parseResponse(byte[] responseBody) throws JSONException {
//                return super.parseResponse(responseBody);
//            }
//        });
//    }

    private void SaveConfigData(String iUserName, String iPassword) {
        configObj = Util.GetConfiguration();
        if (configObj == null)
            configObj = new Configuration();
        try {
            realm.beginTransaction();
            configObj.setUserName(iUserName);
            configObj.setPassword(iPassword);
            configObj.setDeviceSrl(deviceSrl);
            realm.copyToRealmOrUpdate(configObj);
            realm.commitTransaction();
        } catch (Exception e) {
            Util.toast(context, "Erorr installing Application!");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        IdSetter();
        receiverHandler = new Handler();
    }

    private void IdSetter() {
        progressDialog = new ProgressDialog();
        fragmentManager = getSupportFragmentManager();
        context = getApplicationContext();
        Loginbtn = findViewById(R.id.btnLogin);
        edUsername = findViewById(R.id.edUserName);
        edPassword = findViewById(R.id.edPass);
        realm = Util.getRealmInstance();
        Loginbtn.setOnClickListener(onClickListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return !(keyCode == KeyEvent.KEYCODE_BACK);
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


}
