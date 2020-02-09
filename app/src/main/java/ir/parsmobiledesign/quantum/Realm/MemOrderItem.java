package ir.parsmobiledesign.quantum.Realm;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.loopj.android.http.Base64;

import io.realm.RealmObject;

public class MemOrderItem extends RealmObject {

    private Category CatObj;
    private Item ItemObj;
    private byte Count;
    private String Image;
    private int Options;
    private MemOrder OrderObj;

    public Category getCatObj() {
        return CatObj;
    }

    public void setCatObj(Category catObj) {
        CatObj = catObj;
    }

    public Item getItemObj() {
        return ItemObj;
    }

    public void setItemObj(Item item) {
        ItemObj = item;
    }

    public byte getCount() {
        return Count;
    }

    public void setCount(byte count) {
        Count = count;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public Bitmap getBitmap() {
        byte[] x = Base64.decode(getImage(), Base64.DEFAULT);  //convert from base64 to byte array
       // if (x != null)
            return BitmapFactory.decodeByteArray(x, 0, x.length);
    }

    public int getOptions() {
        return Options;
    }

    public void setOptions(int options) {
        Options = options;
    }

    public MemOrder getOrderObj() {
        return OrderObj;
    }

    public void setOrderObj(MemOrder orderObj) {
        OrderObj = orderObj;
    }


}
