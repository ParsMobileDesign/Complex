package ir.parsmobiledesign.quantum.Realm;

import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class RealmMigration implements io.realm.RealmMigration
{
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion)
    {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 1)
        {
            RealmObjectSchema objectSchema = schema.get("Configuration");
            objectSchema.addPrimaryKey("DeviceSrl");
            oldVersion++;
        }
    }
}
