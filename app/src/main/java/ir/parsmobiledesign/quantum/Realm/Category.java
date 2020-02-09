package ir.parsmobiledesign.quantum.Realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Category extends RealmObject {

    @PrimaryKey
    private byte Srl;
    private String Title;
    private int Version;
    private int Options;
    private RealmList<Item> items;
    public Category() {
    }

    public Boolean CheckItemExist(byte ItmSrl)
    {
        if(items != null && items.size() > 0)
        {
            Item ItmObj = getItems().where().equalTo("Srl",ItmSrl).findFirst();
            if(ItmObj != null)
                return true;
        }
        return false;
    }
    public byte getSrl() {
        return Srl;
    }

    public void setSrl(byte srl) {
        Srl = srl;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }


    public int getVersion() {
        return Version;
    }

    public void setVersion(int iVersion) {
        Version = iVersion;
    }

    public int getOptions() {
        return Options;
    }

    public void setOptions(int options) {
        Options = options;
    }
    public RealmList<Item> getItems() {
        return items;
    }

    public void setItems(RealmList<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return this.getTitle();
    }
}
