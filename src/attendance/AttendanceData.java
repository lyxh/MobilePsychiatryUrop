package attendance;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import main.StartActivity;

import timer.MyTimerClass;

import com.example.mobilepsychiatry.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
 
/**
 * Attendance Data activity is launched after clicking on "My Attendance" button.
 * 
 * @author yixin
 *
 */
public class AttendanceData extends Activity {
  
	RelativeLayout pane;
	
	private DrawGraph dg1;
	private DrawGraph dg2;
	ArrayList<Integer> aLIst = new ArrayList<Integer>();
	ArrayList<Integer> bLIst = new ArrayList<Integer>();
	String atten=StartActivity.attendance;
			
	/**
	 * return
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.attendancedata);
	    TextView main_text = (TextView) findViewById(R.id.attendance);
	    String msg="";
	    //StartActivity.attendance=MONTHDATA field indicates the residences data in terms of percent from 0.0-1.0. 
	    if (atten.equals("No such resident")){
       	    msg="No attendance data available.";
        }
	    else if (Integer.valueOf(atten)==0){
	    	msg = "You haven't attend any session yet.";
	    }
	    else{
	    	msg = "You attendance for the past is "+Integer.valueOf(atten)*100+"%.";
	    }
	    main_text.setText(msg);
        //at the start, schedule a new task to timer
    	 MyTimerClass.timer.cancel();
	     MyTimerClass.timer=new Timer();
     	//The first task is to show the user that he "is going to be logged out in 5 secs", which will happen in 20 secs.
     	MyTimerClass.timer.schedule(new TimerTask() {
     	    @Override
			public void run() {
     	    	 runOnUiThread(new Runnable() {
        	   		     @Override
						public void run(){
        	   		    	Toast toast=Toast.makeText(AttendanceData.this, "Logging out automatically in 5 seconds", Toast.LENGTH_LONG);
        	   		    	LinearLayout toastLayout = (LinearLayout) toast.getView();
        	   		    	TextView toastTV = (TextView) toastLayout.getChildAt(0);
        	   		    	toastTV.setTextSize(20);
        	   		    	toast.show();}
        	   		    	 
     		        });
     	    }
     	}, MyTimerClass.duration);
     	
     	// The second is to start the StartActivity, which will happen in 25 secs.
     	 MyTimerClass.timer.schedule(new TimerTask() {
     		 
        	      @Override
				public void run() {
        	    	  Intent i = new Intent(getApplicationContext(), StartActivity.class);  
   	    	          startActivity(i);	 
        	      }
         }, MyTimerClass.duration+MyTimerClass.reminder);
	  } 
	
	
	
    //on user interaction, reset the timer
    @Override
    public synchronized void onUserInteraction(){
    	MyTimerClass.timer.cancel();
	    MyTimerClass.timer=new Timer();
     	//The first task is to show the user that he "is going to be logged out in 5 secs", which will happen in 20 secs.
     	MyTimerClass.timer.schedule(new TimerTask() {
     	    @Override
			public void run() {
     	    	 runOnUiThread(new Runnable() {
     	    		@Override
					public void run(){
    	   		    	Toast toast=Toast.makeText(AttendanceData.this, "Logging out automatically in 5 seconds", Toast.LENGTH_LONG);
    	   		    	LinearLayout toastLayout = (LinearLayout) toast.getView();
    	   		    	TextView toastTV = (TextView) toastLayout.getChildAt(0);
    	   		    	toastTV.setTextSize(20);
    	   		    	toast.show();} });
     	    }
     	}, MyTimerClass.duration);
     	
     	// The second is to start the StartActivity, which will happen in 25 secs.
     	 MyTimerClass.timer.schedule(new TimerTask() {
     		 
        	      @Override
				public void run() {
        	    	  Intent i = new Intent(getApplicationContext(), StartActivity.class);  
   	    	          startActivity(i);	 
        	      }
         },  MyTimerClass.duration+MyTimerClass.reminder);
     	 MyTimerClass.timer.cancel();
     }
    
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {   
        Button button1 = (Button) findViewById(R.id.previousweek);
        Button button2 = (Button) findViewById(R.id.nextweek);
        switch (keyCode) {
            case KeyEvent.KEYCODE_A:            	
            	button1.performClick();
                 return true;
            case KeyEvent.KEYCODE_S:
            	button2.performClick();
            	return true;        
            default:return super.onKeyUp(keyCode, event);
        }
    }
  }
    
    
    