/*
 * Copyright (C) 2012-2016 The Android Money Manager Ex Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.money.manager.ex.dropbox;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.money.manager.ex.BuildConfig;
import com.money.manager.ex.settings.PreferenceConstants;
import com.money.manager.ex.sync.SyncBroadcastReceiver;
import com.money.manager.ex.sync.SyncManager;

import org.apache.commons.lang3.math.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Schedules the periodic synchronization.
 * Run from the settings, when the synchronization interval changes.
 * Also receives a notification on BOOT_COMPLETED.
 */
public class SyncSchedulerBroadcastReceiver
    extends BroadcastReceiver {

    // action intents
    public static final String ACTION_START = "com.money.manager.ex.custom.intent.action.START_SYNC_SERVICE";
    public static final String ACTION_CANCEL = "com.money.manager.ex.custom.intent.action.STOP_SYNC_SERVICE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = "";
        //Log actions
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            action = intent.getAction();
            if (BuildConfig.DEBUG) Log.d(this.getClass().getSimpleName(), "Action request: " + action);
        }

        // compose intent
        Intent syncIntent = new Intent(context, SyncBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, syncIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (action.equals(ACTION_CANCEL)) {
            alarmManager.cancel(pendingIntent);
            return;
        }

        // by default, the action is ACTION_START. This is assumed on device boot.
        startHeartbeat(context, alarmManager, pendingIntent);
    }

    private void startHeartbeat(Context context, AlarmManager alarmManager, PendingIntent pendingIntent) {
        SyncManager sync = new SyncManager(context);
        if (!sync.isActive()) return;

        // get repeat time.
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferenceManager == null) return;

        // get minute
        String preferenceMinute = preferenceManager.getString(context.getString(PreferenceConstants.PREF_DROPBOX_TIMES_REPEAT), "30");
        if (!NumberUtils.isNumber(preferenceMinute)) return;

        int minute = Integer.parseInt(preferenceMinute);
        if (minute <= 0) return;

        Calendar cal = Calendar.getInstance();

        if (BuildConfig.DEBUG) {
            Log.d(this.getClass().getSimpleName(),
                    "Start at: " + new SimpleDateFormat().format(cal.getTime())
                            + " and repeats every: " + preferenceMinute + " minutes");
        }

        // Schedule alarm for synchronization
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), minute * 60 * 1000, pendingIntent);
    }
}
