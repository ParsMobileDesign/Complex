package ir.parsmobiledesign.quantum.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Configuration extends RealmObject {
    @PrimaryKey
    private int Srl;
    private String UserName;
    private String Password;
    private String DeviceSrl;
    private String TerminalId;
    private String MerchantId;
    private String MerchantName;
    private byte isDisabled;

    public int getSrl() {
        return Srl;
    }

    public void setSrl(int srl) {
        Srl = srl;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getDeviceSrl() {
        return DeviceSrl;
    }

    public void setDeviceSrl(String deviceSrl) {
        DeviceSrl = deviceSrl;
    }

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

    public byte getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(byte isDisabled) {
        this.isDisabled = isDisabled;
    }





    public Configuration() {
    }
}
