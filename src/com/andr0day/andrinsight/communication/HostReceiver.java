package com.andr0day.andrinsight.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HostReceiver extends BroadcastReceiver {
    ReceivedCallback receivedCallback;


    public HostReceiver(ReceivedCallback receivedCallback) {
        this.receivedCallback = receivedCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        receivedCallback.onReceived(intent);
    }
}
