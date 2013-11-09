package main;

import android.app.Application;
import android.content.Context;

public class MobilePsychiatry extends Application{

	    private static Context context;

	    @Override
		public void onCreate(){
	        super.onCreate();
	        MobilePsychiatry.context = getApplicationContext();
	    }

	    public static Context getAppContext() {
	        return  MobilePsychiatry.context;
	    }
	}