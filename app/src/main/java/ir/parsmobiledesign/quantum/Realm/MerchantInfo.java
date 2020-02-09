package ir.parsmobiledesign.quantum.Realm;

import io.realm.RealmObject;

public class MerchantInfo extends RealmObject{
    //     ConfigObj.setTerminalId(MerchantInfo.getTerminalId());
//     ConfigObj.setMerchantId(MerchantInfo.getMID());
//     ConfigObj.setMerchantName(MerchantInfo.getName());
//     ConfigObj.setPostalCode(MerchantInfo.getPostalCode());
//     ConfigObj.setAddress(MerchantInfo.getAddress());
    String TerminalId;
    String MerchantId;
    String MerchantName;
    String PostalCode;
    String Address;

    public String getTerminalId() {
        return TerminalId;
    }

    public void setTerminalId(String terminalId) {
        TerminalId = terminalId;
    }

    public String getMerchantId() {
        return MerchantId;
    }

    public void setMerchantId(String merchantId) {
        MerchantId = merchantId;
    }

    public String getMerchantName() {
        return MerchantName;
    }

    public void setMerchantName(String merchantName) {
        MerchantName = merchantName;
    }

    public String getPostalCode() {
        return PostalCode;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }


}
