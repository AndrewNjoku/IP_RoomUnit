package com.tunstall.grandstream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tunstall.grandstream.Home_Screen_Activity.MVP.Home_Activity;

public class BootUpReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
            Intent i = new Intent(context, Home_Activity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);  
    }

}