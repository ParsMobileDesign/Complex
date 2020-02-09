package ir.parsmobiledesign.quantum;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.RemoteException;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import ir.parsmobiledesign.quantum.Realm.Configuration;
import ir.parsmobiledesign.quantum.Utility.Pahpat;
import ir.parsmobiledesign.quantum.Utility.Util;

import io.realm.Realm;

public class Splash extends AppCompatActivity {
    Realm realm;
    TextView versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        versionName = findViewById(R.id.versionName);
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionName.setText(!version.equals("") ? version : "1.0");
        realm = Util.getRealmInstance();
        Handler handler = new Handler();
        String deviceSrl = Util.DeviceSrl(getApplicationContext());
        if (!deviceSrl.contains("G")) {
            try {
                Pahpat.getMerchantInfo(getApplicationContext());   //It gets infos and after that save them in MerchantInfo table
            } catch (Pahpat.PahpatException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (!ProviderAddrExist())
                    intent = new Intent(Splash.this, Login.class);
                else
                    intent = new Intent(Splash.this, ActSelection.class);

                startActivity(intent);
            }
        }, 1500L);
    }

    private boolean ProviderAddrExist()  //Check If ProviderAddress Exist or Not
    {
        Configuration ConfigObj = Util.GetConfiguration();
        if (ConfigObj != null && ConfigObj.getUserName() != null && ConfigObj.getPassword() != null) {
            return true;
        } else
            return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Util.DisabledHomeButton(getApplicationContext(), this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            finish();
        if (keyCode == KeyEvent.KEYCODE_HOME)
            Util.DisabledHomeButton(getApplicationContext(), this);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
