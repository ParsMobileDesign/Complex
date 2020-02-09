package ir.parsmobiledesign.quantum;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ConfigApp extends Application {
    private static ConfigApp instance;
    private Handler mHandler;
    private IDAL idal;
    private static String TAG = "Config";

    public static ConfigApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        Realm.init(this);
        try {
            //RealmConfiguration config = new RealmConfiguration.Builder().name("RapidNet.realm").schemaVersion(1).migration(new RealmMigration()).build();
            RealmConfiguration config = new RealmConfiguration.Builder().name("Qunatom.realm").schemaVersion(1).build();

            Realm.setDefaultConfiguration(config);
            mHandler = new Handler();
            idal = NeptuneLiteUser.getInstance().getDal(this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "err", Toast.LENGTH_LONG).show();
        }
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent intent = new Intent(this, UpdateReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT); //PendingIntent.FLAG_UPDATE_CURRENT
//        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, 300000, pendingIntent);    // Every 5 minute
//
//        AlarmManager managerGather = (AlarmManager) getSystemService(ALARM_SERVICE);
//        SetAlarmTimeForGathering(23,00,1,managerGather);
//        SetAlarmTimeForGathering(01,00,2,managerGather);
//        SetAlarmTimeForGathering(03,00,3,managerGather);
    }

    public IDAL getIdal() {
        return idal;
    }
}
