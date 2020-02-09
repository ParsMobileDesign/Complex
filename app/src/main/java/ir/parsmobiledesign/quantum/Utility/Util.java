package ir.parsmobiledesign.quantum.Utility;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.RemoteException;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import ir.parsmobiledesign.quantum.Interfaces.AcceptSyncInterface;
import ir.parsmobiledesign.quantum.Interfaces.YesNoDialogInterface;
import ir.parsmobiledesign.quantum.R;
import ir.parsmobiledesign.quantum.Realm.Category;
import ir.parsmobiledesign.quantum.Realm.Configuration;
import ir.parsmobiledesign.quantum.Realm.Item;
import ir.parsmobiledesign.quantum.Realm.MemOrder;
import ir.parsmobiledesign.quantum.Realm.MerchantInfo;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

import cz.msebera.android.httpclient.client.HttpResponseException;
import io.realm.Realm;
import io.realm.RealmResults;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class Util {
    private static Context context;

    public static String ProviderArr = "http://192.168.1.101/Complex/api/default/"; //soroush azizz router local ip
    public static String GetCategory = ProviderArr + "GetCategory";
    public static String SyncAcceptOrder = ProviderArr + "SyncAcceptOrder";
    public static String UpdateCategory = ProviderArr + "UpdateCategory";
    public static String Verify = ProviderArr + "Verify";

    //public static String providerArr = "http://192.168.1.101/Complex/api/"; //pedi mongol camel -> router local ip
    //public static String providerArr = "http://www.quantumgroup.ir/Complex/api/";

    private static Util instance = new Util();

    public static Util getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    //    public static String getBaseAddr() {
//        Configuration temp = GetConfiguration();
//        if (temp != null)
//            return temp.getProviderAddr().toLowerCase().split("default/")[0];
//        else
//            return "";
//
//    }
    public static void syncAcceptOrder(final AcceptSyncInterface syncInterface, final MemOrder iOrder) throws JSONException {
        final Realm realm = Realm.getDefaultInstance();
        Configuration configObj = Util.GetConfiguration();
        AsyncHttpClient client = new AsyncHttpClient();
        final JSONObject json = new JSONObject();
        json.put("Action", "syncAcceptOrder");
        json.put("MemOrder", iOrder.getJson());
        StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
        client.setMaxRetriesAndTimeout(0, 8000);
        client.post(context, Util.SyncAcceptOrder, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // MemOrder temp = realm.where(MemOrder.class).equalTo("MemSrl", iOrder.getMemSrl()).and().equalTo("Srl", iOrder.getSrl()).findFirst();
                //if (temp != null) {
                realm.beginTransaction();
                iOrder.setAcceptedSynced(true);
                realm.commitTransaction();
                syncInterface.onAccept();
                //}
                // else
                //  syncInterface.onFail("خطا در ارسال سفارش، لطفا دوباره تلاش کنید !");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                syncInterface.onFail("خطا در ارسال سفارش، لطفا دوباره تلاش کنید !");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                try {
//                    Util.showInfoDialog(activity, responseString, "Error");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                syncInterface.onFail("خطا در ارسال سفارش، لطفا دوباره تلاش کنید !");
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                syncInterface.onFail("خطا در ارسال سفارش، لطفا دوباره تلاش کنید !");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            protected Object parseResponse(byte[] responseBody) throws JSONException {
//               Object ParsResponse = super.parseResponse(responseBody);
//                if (ParsResponse != null || ParsResponse != "") {
//                    if (ParsResponse instanceof JSONObject) {
//                        JSONObject JDataObject = (JSONObject) ParsResponse;
//                        return JDataObject;
//                    } else if (ParsResponse instanceof JSONArray) {
//                        JSONArray JDataArray = (JSONArray) ParsResponse;
//                        return JDataArray;
//                    }
//                }
                return new JSONArray();

            }
        });
    }

    public static void prepareCategoryList(Activity iactivity) throws JSONException {
        final Activity activity = iactivity;
        final Realm realm = Realm.getDefaultInstance();
        Configuration configObj = Util.GetConfiguration();
        AsyncHttpClient client = new AsyncHttpClient();
        final JSONObject json = new JSONObject();
        json.put("UserName", configObj.getUserName());
        json.put("Password", configObj.getPassword()); //Get From UserId Control //Get From UserId Control
        StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
        client.setMaxRetriesAndTimeout(1, 10000);
        client.post(context, Util.GetCategory, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                JSONArray itemsJson;
                JSONObject item;
                // progressDialog.dismiss();
                if (headers != null || response != null) {
                    realm.beginTransaction();
                    for (int i = 0; i < response.length(); i++) {
                        Category cat = null;
                        try {
                            JSONObject json = response.getJSONObject(i);
                            cat = realm.createObject(Category.class, Util.cnvToByte(json.getString("Srl")));
                            cat.setTitle(json.getString("Title"));
                            cat.setVersion(json.getInt("Version"));
                            cat.setOptions(json.getInt("Options"));
                            Object ChkUnitsObj = (JSONArray) json.get("Items");
                            if (ChkUnitsObj != null) {
                                itemsJson = json.getJSONArray("Items");
                                for (int j = 0; j < itemsJson.length(); j++) {
                                    item = itemsJson.getJSONObject(j);
                                    Item tempItem = null;
                                    try {
                                        tempItem = realm.createObject(Item.class); //,srl
                                        tempItem.setCatObj(cat);
                                        tempItem.setSrl(Util.cnvToByte(item.getString("Srl")));
                                        tempItem.setTitle(item.getString("Title"));
                                        tempItem.setPrice(Util.cnvToInteger(item.getString("Price")));
                                        tempItem.setImage(item.getString("Image"));
                                        tempItem.setVersion(item.getInt("Version"));
                                        tempItem.setOptions(item.getInt("Options"));
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
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    if (throwable instanceof Exception) {
                        Util.showInfoDialog(activity, throwable.getMessage(), "Error");
                        return;
                    } else
                        Util.showInfoDialog(activity, errorResponse.getString("Msg"), "Error");
                    return;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                try {
                    Util.showInfoDialog(activity, responseString, "Error");
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

    public static ArrayList<Category> getCategory() {
        ArrayList<Category> temp = new ArrayList<Category>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Category> tempCatList = realm.where(Category.class).findAll();
        if (tempCatList.size() > 0)
            for (int i = 0; i < tempCatList.size(); i++)
                temp.add(tempCatList.get(i));
        return temp;
    }

    private static Configuration configuration;

    public static Configuration GetConfiguration() {
        Realm realm = Realm.getDefaultInstance();
        Configuration configuration = null;
        RealmResults<Configuration> tempObj = realm.where(Configuration.class).equalTo("isDisabled", 0).findAll(); //Changed
        if (tempObj != null && tempObj.size() > 0)
            configuration = tempObj.last();
        return configuration;
    }

    public static Realm getRealmInstance() {
        // if (realmInstance == null || realmInstance.isClosed())
        Realm realmInstance = Realm.getDefaultInstance();
        return realmInstance;
    }

    private static String appVersionName = "";

    public static String GetAppVersionName(Context iContext) {
        if (appVersionName.isEmpty()) {
            try {
                appVersionName = iContext.getPackageManager().getPackageInfo(iContext.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        return appVersionName;
    }

    public static int ConfigxRowIndentity() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Configuration> ConfigList = realm.where(Configuration.class).findAll();
        if (ConfigList == null || ConfigList.size() == 0)
            return 1;
        else
            return ConfigList.last().getSrl() + 1;
    }

    public static void DeviceReceiverInfo(Context icontext, Configuration iConfig, MerchantInfo infos) throws Pahpat.PahpatException, RemoteException {
        Realm realm = getRealmInstance();
        if (iConfig != null) {
            realm.beginTransaction();
            iConfig.setTerminalId(infos.getTerminalId());
            iConfig.setMerchantId(infos.getMerchantId());
            iConfig.setMerchantName(infos.getMerchantName());
            realm.insertOrUpdate(iConfig);
            realm.commitTransaction();
            try {
                AsyncHttpClient client = new AsyncHttpClient();
                final JSONObject json = new JSONObject();
                try {
                    json.put("iUserId", iConfig.getUserName());  //Get from UserId control
                    json.put("iPass", iConfig.getPassword()); //Get From UserId Control
                    json.put("iDeviceSrl", iConfig.getDeviceSrl());
                    json.put("TerminalId", iConfig.getTerminalId());
                    json.put("MerchantId", iConfig.getMerchantId());
                    json.put("MerchantName", iConfig.getMerchantName());
                    json.put("AppVersion", Util.GetAppVersionName(icontext));
                    json.put("AppVersionUpdateDate", Util.today());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
                client.setMaxRetriesAndTimeout(1, 4000);
                //  client.post(context, Util.getBaseAddr() + Util.ManualAddr, entity, "application/json", new JsonHttpResponseHandler());
            } catch (Exception ex) {
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static void saveMerchantInfo(Pahpat.EMerchantInfoResult merchantInfoState) {
        Realm realm = Realm.getDefaultInstance();
        MerchantInfo info = realm.where(MerchantInfo.class).findFirst();
        if (info == null && merchantInfoState != null && merchantInfoState.getTerminalId() != null) {
            realm.beginTransaction();
            MerchantInfo merchantInfo = realm.createObject(MerchantInfo.class);
            merchantInfo.setTerminalId(merchantInfoState.getTerminalId());
            merchantInfo.setMerchantId(merchantInfoState.getMID());
            merchantInfo.setMerchantName(merchantInfoState.getName());
            merchantInfo.setPostalCode(merchantInfoState.getPostalCode());
            merchantInfo.setAddress(merchantInfoState.getAddress());
            realm.copyToRealm(merchantInfo);
            realm.commitTransaction();
        }
        realm.close();
    }

    public static boolean isOnline(Context iContext) {
        boolean connected = false;
        ConnectivityManager connectivityManager;
        try {
            connectivityManager = (ConnectivityManager) iContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Util.toast(context, e.getMessage());
        }
        return connected;
    }


    public static void toast(Context context, String Msg) {
        if (Msg != null)
            Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, int Msg) {
        try {
            Toast.makeText(context, context.getResources().getString(Msg), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }

    public static void toast(Context icontext, int Msg, int iGravity) {
        try {
            Toast t = Toast.makeText(icontext, icontext.getResources().getString(Msg), Toast.LENGTH_SHORT);
            t.setGravity(iGravity, 0, 0);
            t.show();
        } catch (Exception e) {
        }
    }

    public static void toast(Context icontext, int Msg, int iGravity, int iLength) {
        try {
            Toast t = Toast.makeText(icontext, icontext.getResources().getString(Msg), iLength);
            t.setGravity(iGravity, 0, 0);
            t.show();
        } catch (Exception e) {
        }
    }

    private static String deviceSrl = "";

    public static String DeviceSrl(Context context) {
        if (deviceSrl.isEmpty()) {
            String szztSrl = getSZZTSerialNumber(context);
            if (szztSrl.contains("G"))
                deviceSrl = szztSrl;
            else
                try {
                    TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                        deviceSrl = Build.SERIAL;
                } catch (Exception e) {
                }
        }
        return deviceSrl;
    }

    public static String today() {
        PersianDate today = new PersianDate();
        PersianDateFormat formatter = new PersianDateFormat();
        return formatter.format(today, "Ymd-His");
    }

    public static String todayDate() {
        return today("Ymd");
    }

    public static int todayDateInteger() {
        return Util.cnvToInteger(today("Ymd"));
    }

    public static String todayTime() {

        return today("His");
    }

    public static void openKeyboard(final Activity activity, final EditText editText) {
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static int todayTimeInteger() {

        return Util.cnvToInteger(today("His"));
    }

    public static String today(String format) {
        PersianDate today = new PersianDate();
        PersianDateFormat formatter = new PersianDateFormat();
        return formatter.format(today, format);
    }

    // Convert Functions ...
    public static Integer cnvToInteger(String iStr) {
        try {
            return Integer.parseInt(iStr);
        } catch (Exception e) {
            return 0;
        }
    }

    public static short cnvToShort(String iStr) {
        try {
            return Short.parseShort(iStr);
        } catch (Exception e) {
            return 0;
        }
    }

    public static Byte cnvToByte(String iStr) {
        try {
            return Byte.parseByte(iStr);
        } catch (Exception e) {
            return 0;
        }
    }

    public static Long cnvToLong(String iStr) {
        try {
            return Long.parseLong(iStr);
        } catch (Exception e) {
            return 0L;
        }
    }

    public static String cnvToCurrency(String iPrice) {
        Long parsed = Long.parseLong(iPrice);
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(0);
        Currency currency = Currency.getInstance("IRR");
        format.setCurrency(currency);
        return format.format(parsed);
    }

    public static String cnvToCurrency(int iPrice) {
        String output = "";
        try {
            long parsed = iPrice;
            NumberFormat format = NumberFormat.getInstance();
            format.setMaximumFractionDigits(0);
            Currency currency = Currency.getInstance("IRR");
            format.setCurrency(currency);
            output = format.format(parsed);
        } catch (Exception e) {
        }
        return output;


    }

    //Indentify Customer(code-86)


    public static void DisabledHomeButton(Context context, AppCompatActivity Activity) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext()// getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(Activity.getTaskId(), 0);
    }

    public static void setActionBarProperties(Context context, AppCompatActivity iActivity, androidx.appcompat.widget.Toolbar itoolBar, Configuration configObj) {
        iActivity.setSupportActionBar(itoolBar);
        ActionBar ActBar = iActivity.getSupportActionBar();
        ActBar.setDisplayHomeAsUpEnabled(true);
        ActBar.setDisplayShowTitleEnabled(false);
        TextView tv = (TextView) itoolBar.findViewById(R.id.toolbar_edTextView);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
        tv.setLayoutParams(lp);
        String appTitle = "";
        tv.setText(appTitle); // ActionBar title text
        tv.setTextSize(22);
        tv.setSingleLine(true);
        tv.setTextColor(context.getResources().getColor(R.color.white));
        ActBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((ViewGroup) tv.getParent()).removeView(tv);
        ActBar.setDisplayHomeAsUpEnabled(true);
        ActBar.setCustomView(tv);
    }

    public static void showYesNoDialog(Context context, String msgText, String msgTitle, int icon, final YesNoDialogInterface iInterface) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle(msgTitle)
                .setMessage(msgText)
                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        iInterface.YesClicked();
                    }
                }).setNegativeButton("نه", null)
                .setIcon(icon)
                .show();
    }

    public static void showInfoDialog(Context context, String msgText, String msgTitle, String buttonTitle, int icon) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle(msgTitle)
                .setMessage(msgText)
                .setNeutralButton(buttonTitle, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(icon)
                .show();
    }

    public static void showInfoDialog(Context context, String msgText, String msgTitle) {
        Util.showInfoDialog(context, msgText, msgTitle, "خب", R.drawable.info);
    }

    public static void showInfoDialog(Context context, int msgText, int msgTitle) {
        Util.showInfoDialog(context, context.getString(msgText), context.getString(msgTitle));
    }

    public static void showInfoDialog(Context context, int msgText, int msgTitle, int icon) {
        Util.showInfoDialog(context, context.getString(msgText), context.getString(msgTitle), "خب", icon);
    }

    public static void showInfoDialog(Context context, String msgText, String msgTitle, int icon) {
        Util.showInfoDialog(context, msgText, msgTitle, "خب", icon);
    }

    public static boolean isNumber(String iParam) {
        try {
            Long temp = Long.parseLong(iParam);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //add to Util Class


    public static void NetworkErrorHandle(Context iContext, Throwable iThrow) {
        if (iContext != null) {
            if (iThrow instanceof HttpResponseException) {
                String msg = iThrow.getMessage() != null ? iThrow.getMessage() : "--";
                HttpResponseException hre = (HttpResponseException) iThrow;
                if (hre != null) {
                    switch (hre.getStatusCode()) {
                        case 301: {
                            Util.showInfoDialog(iContext, "حجم بسته اینترنت  به پایان رسیده است یا شبکه در دسترس نیست !" + msg, "خطا", R.drawable.exclamation_mark);
                            return;
                        }
                        case 404: {
                            Util.showInfoDialog(iContext, "منبع مورد نظر یافت نشد. با مسئول سیستم تماس بگیرید." + msg, "خطا", R.drawable.exclamation_mark);
                            return;
                        }
                        case 500: {
                            Util.showInfoDialog(iContext, "سرور در دسترس نیست، لطفا چند لحظه دیگر تلاش کنید !" + msg, "خطا", R.drawable.exclamation_mark);
                            return;
                        }
                    }
                } else
                    Util.showInfoDialog(iContext, "خطا در برقراری ارتباط  با شبکه!" + iThrow.getMessage(), "خطا", R.drawable.exclamation_mark);
            }
        }
    }

    public static Boolean getBit(int source, byte offset) {
        int temp = 1 << offset;
        return (source & temp) == temp;
    }

    public static int setBit(Boolean iVal, int iSource, byte iOffset) {
        int temp = 1 << iOffset;
        if (iVal)
            iSource = iSource | temp;
        else {
            temp = ~temp;
            iSource = iSource & temp;
        }
        return iSource;
    }

    public static String getSZZTSerialNumber(Context context) {
        String serialNumber;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serialNumber = (String) get.invoke(c, "persist.zt.sn");
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }
        return serialNumber;
    }

//    public static void ResolveHTTPSAddr() {
//        Configuration ConfigObj;
//        ConfigObj = GetConfiguration();
//        if (ConfigObj == null)
//            ConfigObj = new Configuration();
//        String ProviderAddr = ConfigObj.getProviderAddr();
//        try {
//            if (ProviderAddr != "" && !ProviderAddr.contains("https")) {
//                Realm realm = getRealmInstance();
//                realm.beginTransaction();
//                ConfigObj.setProviderAddr("https://rapidnet.ir/api/default/verify");
//                //ConfigObj.setProviderAddr("http://192.168.1.59/rapidnet/api/default/verify");
//                realm.copyToRealmOrUpdate(ConfigObj);
//                realm.commitTransaction();
//            }
//        } catch (Exception e) {
//            Log.d("err", e.getMessage());
//        }
//    }
}
