package org.voimala.votingapp.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.voimala.votingapp.R;
import org.voimala.votingapp.activities.MainActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class NotificationService extends Service {
    
    private final IBinder iBinder = new LocalBinder();
    private final Logger logger = Logger.getLogger(getClass().getName());
    
    public class LocalBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return iBinder;
    }
    
    /**Shows a notification about a new open voting only if the setting has been
     * turned on in the preferences screen.*/
    public void showNotificationNewOpenVoting() {
        logger.log(Level.INFO, "Got a new open vote!");
        
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showNotifications = sharedPreferences.getBoolean("show_notifications", true);
        
        if (showNotifications) {
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("New open poll!")
                    .setContentText("A new open poll has been added.")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            
            NotificationManager notificationManager = 
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        }
    }
    
}
