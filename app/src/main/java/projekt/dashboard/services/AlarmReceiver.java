package projekt.dashboard.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * This broadcast receiver is awoken after boot and registers the service that
 * checks for new photos from all the known contacts.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.example.action.ALARM")) {
            Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();
        }

    }
}