package ir.parsmobiledesign.quantum.Realm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.loopj.android.http.Base64;

import io.realm.RealmObject;

public class Item extends RealmObject {

    private Category CatObj;
    private byte Srl;
    private String Title;
    private int Price;
    private String Image;
    private int Version;
    private int Options;

    public Category getCatObj() {
        return CatObj;
    }

    public void setCatObj(Category catObj) {
        CatObj = catObj;
    }


    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }

    public byte getSrl() {
        return Srl;
    }

    public void setSrl(byte srl) {
        Srl = srl;
    }

    public Bitmap getThumbnailBitmap() {
        byte[] x = Base64.decode(getImage(), Base64.DEFAULT);  //convert from base64 to byte array
        return BitmapFactory.decodeByteArray(x, 0, x.length);
    }
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }


    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }


    public int getOptions() {
        return Options;
    }

    public void setOptions(int options) {
        Options = options;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
