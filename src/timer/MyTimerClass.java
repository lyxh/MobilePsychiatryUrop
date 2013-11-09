package timer;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyTimerClass extends Activity{
	public static Timer timer=new Timer();
	public static int duration=600000;
	public static int reminder=5000;
	//reshedule a new task
	public void schedule(final Context context){
        // on create, reschedule the timer;
     	 MyTimerClass.timer.cancel();
	     MyTimerClass.timer=new Timer();
     	 //The first task is to show the user that he "is going to be logged out in 5 secs", which will happen in 20 secs.
     	 MyTimerClass.timer.schedule(new TimerTask() {
     	 @Override
		public void run() {
     	    	 runOnUiThread(new Runnable() {
        	   		     @Override
						public void run(){
        	   		    	 Toast toast=Toast.makeText(context, "Logging out automatically in 5 seconds", Toast.LENGTH_LONG);
	     	   		    	LinearLayout toastLayout = (LinearLayout) toast.getView();
	     	   		    	TextView toastTV = (TextView) toastLayout.getChildAt(0);
	     	   		    	toastTV.setTextSize(20);
	     	   		    	toastTV.setBackgroundColor(0x3399ff);
	     	   		    	toast.show();}
     		        });
     	    }
     	},duration);    	 
      	// The second is to start the StartActivity, which will happen in duration+reminder_duration milisecs.
	}
	
}
