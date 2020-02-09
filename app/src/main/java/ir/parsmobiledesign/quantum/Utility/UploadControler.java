package ir.parsmobiledesign.quantum.Utility;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ir.parsmobiledesign.quantum.Splash;

public class UploadControler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, Splash.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

}
