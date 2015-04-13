package com.contextnotifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.os.Bundle;

public class StartupReceiver extends BroadcastReceiver {

	  @Override
	    public void onReceive(Context context, Intent intent) {
		  try{
			    Intent service = new Intent(context, Monitor.class);
			    context.startService(service);
		  }catch(Exception e){
				  e.printStackTrace();
		  }
	    }
}
