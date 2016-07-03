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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

import com.money.manager.ex.R;
import com.money.manager.ex.dropbox.events.DbFileDownloadedEvent;
import com.money.manager.ex.home.RecentDatabaseEntry;
import com.money.manager.ex.home.RecentDatabasesProvider;
import com.money.manager.ex.sync.SyncManager;
import com.money.manager.ex.sync.SyncService;
import com.money.manager.ex.utils.DialogUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Creates a Messenger
 */
public class SyncMessengerFactory {
    public SyncMessengerFactory(Context context) {
        this.context = context;
    }

    private Context context;

    public Context getContext() {
        return this.context;
    }

    public Messenger createMessenger(final ProgressDialog progressDialog, final String remoteFile) {
        // Messenger handles received messages from the Dropbox service.
        Messenger messenger = new Messenger(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == SyncService.INTENT_EXTRA_MESSENGER_NOT_ON_WIFI) {
                    //showMessage();
                    closeDialog(progressDialog);

                } else if (msg.what == SyncService.INTENT_EXTRA_MESSENGER_NOT_CHANGE) {
                    // close dialog
                    closeDialog(progressDialog);
                    showMessage(R.string.database_is_synchronized, Toast.LENGTH_LONG);

                } else if (msg.what == SyncService.INTENT_EXTRA_MESSENGER_START_DOWNLOAD) {
                    showMessage(R.string.sync_downloading, Toast.LENGTH_LONG);

                } else if (msg.what == SyncService.INTENT_EXTRA_MESSENGER_DOWNLOAD) {
                    // Download from Dropbox completed.
                    storeRecentDb(remoteFile);
                    // close dialog
                    closeDialog(progressDialog);
                    // Notify whoever is interested.
                    EventBus.getDefault().post(new DbFileDownloadedEvent());

                } else if (msg.what == SyncService.INTENT_EXTRA_MESSENGER_START_UPLOAD) {
                    showMessage(R.string.sync_uploading, Toast.LENGTH_LONG);

                } else if (msg.what == SyncService.INTENT_EXTRA_MESSENGER_UPLOAD) {
                    // close dialog
                    closeDialog(progressDialog);
                    showMessage(R.string.upload_file_complete, Toast.LENGTH_LONG);
                }
            }
        });
        return messenger;
    }

    private void closeDialog(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            DialogUtils.closeProgressDialog(progressDialog);
        }
    }

    private void showMessage(final int message, final int length) {
        final Activity parent = (Activity) getContext();

        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, length).show();
            }
        });
    }

    private void storeRecentDb(String remoteFile) {
        RecentDatabasesProvider recents = new RecentDatabasesProvider(getContext());

        String localPath = new SyncManager(getContext()).getLocalPath();
        RecentDatabaseEntry entry = RecentDatabaseEntry.getInstance(localPath, true, remoteFile);

        recents.add(entry);
    }

}
