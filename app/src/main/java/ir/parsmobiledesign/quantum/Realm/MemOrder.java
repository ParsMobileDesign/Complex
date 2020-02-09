package ir.parsmobiledesign.quantum.Realm;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import ir.parsmobiledesign.quantum.Utility.Util;

public class MemOrder extends RealmObject {

    private String DeviceSrl;
    private int MemSrl;
    private int Srl;
    private int Date;
    private int Time;
    private RealmList<MemOrderItem> memOrderItems;
    private int TotalAmount;


    private byte TableNo;
    //Boolean Fields
    private boolean isNew;
    private boolean Suspended;
    private boolean Payed;
    private boolean PayedSynced;
    private boolean Accepted;
    private boolean AcceptedSynced;
    //Payment Fields
    private String RRN;
    private String TraceNo;
    private String OrderId;
    private String MaskedPan;
    private String Issuer;
    private int Options;

//----------------------Properties------------------------------

    public String getDeviceSrl() {
        return DeviceSrl;
    }

    public void setDeviceSrl(String deviceSrl) {
        DeviceSrl = deviceSrl;
    }

    public int getMemSrl() {
        return MemSrl;
    }

    public void setMemSrl(int memSrl) {
        MemSrl = memSrl;
    }

    public int getSrl() {
        return Srl;
    }

    public void setSrl(int srl) {
        Srl = srl;
    }

    public int getDate() {
        return Date;
    }

    public void setDate(int xDate) {
        this.Date = xDate;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int xTime) {
        this.Time = xTime;
    }

    public RealmList<MemOrderItem> getMemOrderItems() {
        return memOrderItems;
    }

    public void setMemOrderItems(RealmList<MemOrderItem> memOrderItems) {
        this.memOrderItems = memOrderItems;
    }


    public int getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        TotalAmount = totalAmount;
    }

    public int getOptions() {
        return Options;
    }

    public void setOptions(int options) {
        Options = options;
    }

    public byte getTableNo() {
        return TableNo;
    }

    public void setTableNo(byte tableNo) {
        TableNo = tableNo;
    }


    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isSuspended() {
        return Suspended;
    }

    public void setSuspended(boolean suspended) {
        Suspended = suspended;
    }


    public boolean isPayed() {
        return Payed;
    }

    public void setPayed(boolean payed) {
        Payed = payed;
    }

    public boolean isPayedSynced() {
        return PayedSynced;
    }

    public void setPayedSynced(boolean payedSynced) {
        PayedSynced = payedSynced;
    }

    public boolean isAccepted() {
        return Accepted;
    }

    public void setAccepted(boolean accepted) {
        Accepted = accepted;
    }

    public boolean isAcceptedSynced() {
        return AcceptedSynced;
    }

    public void setAcceptedSynced(boolean acceptedSynced) {
        AcceptedSynced = acceptedSynced;
    }


    public String getRRN() {
        return RRN;
    }

    public void setRRN(String RRN) {
        this.RRN = RRN;
    }

    public String getTraceNo() {
        return TraceNo;
    }

    public void setTraceNo(String traceNo) {
        TraceNo = traceNo;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getMaskedPan() {
        return MaskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        MaskedPan = maskedPan;
    }

    public String getIssuer() {
        return Issuer;
    }

    public void setIssuer(String issuer) {
        Issuer = issuer;
    }

    /////////----------Functions------------------------------------
    public MemOrder() {
    }

    public MemOrder(String iDeviceSrl, int memSrl, int srl, int date, int time, RealmList<MemOrderItem> memOrderItems, int totalAmount, int options) {
        DeviceSrl = iDeviceSrl;
        MemSrl = memSrl;
        Srl = srl;
        Date = date;
        Time = time;
        this.memOrderItems = memOrderItems;
        TotalAmount = totalAmount;
        Options = options;
    }

    public MemOrder(Context context) {
        DeviceSrl = Util.DeviceSrl(context);
        MemSrl = 0;
        Srl = 0;
        Date = Util.todayDateInteger();
        Time = Util.todayTimeInteger();
        this.memOrderItems = new RealmList<MemOrderItem>();
        TotalAmount = 0;
        isNew = true;
        Suspended = false;
        Accepted = false;
        Payed = false;
        PayedSynced = false;
        AcceptedSynced = false;
        RRN = "";
        TraceNo = "";
        OrderId = "";
        MaskedPan = "";
        Issuer = "";
        Options = 0;
    }

    public void Save() {
        Realm realm = Realm.getDefaultInstance();
        int lastIdent = 1;
        if (realm != null) {
            RealmResults<MemOrder> tempList = realm.where(MemOrder.class).equalTo("MemSrl", this.getMemSrl()).findAll();
            if ( tempList!=null && tempList.size() > 0)
                lastIdent = tempList.last().getSrl() + 1;
            realm.beginTransaction();
            this.setSrl(lastIdent);
            realm.insert(this);
            realm.commitTransaction();
        }
        realm.close();
    }

    public String getTimeinString() {
        String time = "";
        if (this.getTime() > 0) {
            time = String.valueOf(this.getTime());
            time = time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
        }
        return time;
    }

    public String getDateinString() {
        String date = "";
        if (this.getDate() > 0) {
            date = String.valueOf(this.getDate());
            date = date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
        }
        return date;
    }

    public void updateTotalAmount() {

        int totalPrice = 0;
        for (int i = 0; i < memOrderItems.size(); i++) {
            MemOrderItem temp = memOrderItems.get(i);
            totalPrice += (temp.getItemObj().getPrice() * temp.getCount());
        }
        this.setTotalAmount(totalPrice);
    }


    public JSONObject getJson() throws JSONException {
        JSONObject jObject = new JSONObject();
        JSONObject jItemObject;
        JSONArray jArray = new JSONArray();
        jObject.put("DeviceSrl", this.getDeviceSrl());
        jObject.put("MemSrl", this.getMemSrl());
        jObject.put("Srl", this.getSrl());
        jObject.put("Date", this.getDate());
        jObject.put("Time", this.getTime());
        for (int i = 0; i < this.getMemOrderItems().size(); i++) {
            MemOrderItem temp = this.getMemOrderItems().get(i);
            jItemObject = new JSONObject();
            jItemObject.put("CatSrl", temp.getCatObj().getSrl());
            jItemObject.put("ItemSrl", temp.getItemObj().getSrl());
            jItemObject.put("Count", temp.getCount());
            jItemObject.put("Options", temp.getOptions());
            jArray.put(jItemObject);
        }
        jObject.put("memOrderItems", jArray);
        jObject.put("TotalAmount", this.getTotalAmount());
        jObject.put("TableNo", this.getTableNo());
        jObject.put("Suspended", this.isSuspended());
        jObject.put("Payed", this.isPayed());
        jObject.put("PayedSynced", this.isPayedSynced());
        jObject.put("Accepted", this.isAccepted());
        jObject.put("AcceptedSynced", this.isAcceptedSynced());
        jObject.put("RRN", this.getRRN());
        jObject.put("TraceNo", this.getTraceNo());
        jObject.put("OrderId", this.getOrderId());
        jObject.put("MaskedPan", this.getMaskedPan());
        jObject.put("Issuer", this.getIssuer());
        jObject.put("Options", this.getOptions());
        return jObject;
    }
}
