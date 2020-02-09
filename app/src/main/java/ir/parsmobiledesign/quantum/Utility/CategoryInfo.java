package ir.parsmobiledesign.quantum.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import io.realm.Realm;
import io.realm.RealmResults;
import ir.parsmobiledesign.quantum.Realm.Category;
import ir.parsmobiledesign.quantum.Realm.Item;

public class CategoryInfo {
   static JSONArray jArray;
   static JSONObject jObject;

    public static JSONObject GetCategoryInfo(Realm irealm) {

        if (irealm != null) {
            RealmResults<Category> Realmbooks = irealm.where(Category.class).findAll();
            try
            {
                jArray = new JSONArray();
                jObject = new JSONObject();
                for (Category jb : Realmbooks)
                {
                    JSONObject BookJSON = new JSONObject();
                    BookJSON.put("Srl", jb.getSrl());
                    BookJSON.put("Version", jb.getVersion());
                    jArray.put(BookJSON);
                }
                jObject.put("CatJson", jArray);
            } catch (JSONException jse) {
            }

            RealmResults<Item> RealmUnits = irealm.where(Item.class).findAll();
            try
            {
                jArray = new JSONArray();
                for (Item un : RealmUnits)
                {
                    JSONObject UnitJSON = new JSONObject();
                    UnitJSON.put("CatSrl", un.getCatObj().getSrl());
                    UnitJSON.put("Srl", un.getSrl());
                    UnitJSON.put("Version",un.getVersion());
                    jArray.put(UnitJSON);
                }
                jObject.put("ItemJson", jArray);
            } catch (JSONException jse)
            {
            }
        }
        return jObject;
    }
}
