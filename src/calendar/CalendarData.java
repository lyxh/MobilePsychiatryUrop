package calendar;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import main.StartActivity;

import timer.MyTimerClass;

import com.example.mobilepsychiatry.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Instances;
import android.text.format.Time;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
/**
 * The CalendarData activity is launched after "My Schedule" button is clicked.
 * It first shows the current week range with the options of going to previous week and next week.
 * The dates and events are shown as well.
 * @author yixin
 *
 */
public class CalendarData extends Activity  {
    private String googleAccount= "staff.hectorreyes@gmail.com";
    private String userName=StartActivity.username.trim();
    private String calendarName=userName.trim();
    
    private GridView gridView, gridView2, gridView3;
    static final String[] weekNumbers= new String[] { "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat" };
    static String[] weekNumbers2, weekNumbers3;
    ArrayAdapter<String>  adapter, adapter2,  adapter3;
    private int week, year=2013;
    Time today = new Time(Time.getCurrentTimezone());	
    private Uri uri;
    private Uri.Builder builder;
    private  ContentResolver cr;
    float size=20;
    //button1 is the previous week button
	 //button2 is the next week button
    Button button1;
    Button button2 ;
	public int color=0x444444;
    @SuppressLint({ "NewApi", "ResourceAsColor" })
	@Override
    public void onCreate(Bundle savedInstanceState) {	
         super.onCreate(savedInstanceState);       
         setContentView(R.layout.calendardata);
         
      	 uri = Calendars.CONTENT_URI; 
    	 builder = Instances.CONTENT_URI.buildUpon();   	
         ContentResolver contentResolver = super.getContentResolver();
         cr = getContentResolver(); 
         
         today.setToNow();
         week= today.getWeekNumber();
         int day=today.weekDay;
         if (day==0) week+=1;
         //update the year to new year if necessary
         int y= today.year;
         if(y>year){year=y;}
       
         
 		 //next, implement the button clickListeners
 		 //When button is clicked, go to the next or previous week's calendar
         button1 = (Button) findViewById(R.id.previousweek);
         button2 = (Button) findViewById(R.id.nextweek);
         
         button1.setOnClickListener(new View.OnClickListener() {
         @Override
		 public void onClick(View v) {
        	   week--;
        	   if (CalendarClass.hasCalendar(calendarName, googleAccount, cr, week, uri,builder)){
        	   new LongOperation1().execute(""); }}
          });
      
      
         button2.setOnClickListener(new View.OnClickListener() {
             @Override
			public void onClick(View v) {
            	 week++;
            	  if (CalendarClass.hasCalendar(calendarName, googleAccount, cr, week, uri,builder)){
            	 new LongOperation1().execute("");}   }
         });
               
         
         //Added
      if (CalendarClass.hasCalendar(calendarName, googleAccount, cr, week, uri,builder)){    	   
    	    ArrayList<Event> events=null;
    	    events=CalendarClass.getEvents(calendarName, googleAccount, cr, week, uri,builder);
    	    //fetch the events
	        new LongOperation().execute("");
	        weekNumbers3= CalendarClass.sortEvents(events);	       
	        
	        //append the start and end of the week
	     	String dates= CalendarClass.getStartEndOFWeek(week, year);
	        TextView textView = (TextView)findViewById(R.id.tvWeekMonthYear);
	        textView.setText("");
	        textView.append(dates); 
	        textView.setTextSize(20);
	        textView.setTextColor(Color.rgb(44,44,44));
	               
	 	    //append each date of the week
	  	     ArrayList<String> sevendays=CalendarClass.getWeek(week, year);
			 weekNumbers2= new String[] { sevendays.get(0),sevendays.get(1),sevendays.get(2),sevendays.get(3),sevendays.get(4),sevendays.get(5),sevendays.get(6)};    
			
         }
		 else{
			 TextView textView = (TextView)findViewById(R.id.tvWeekMonthYear);
			 textView.append("No calendar available.");
			 textView.setTextSize(20);
		     textView.setTextColor(Color.rgb(44,44,44));	        
		 }
	     
        // on create, reschedule the timer;
     	 MyTimerClass.timer.cancel();
	     MyTimerClass.timer=new Timer();
     	 MyTimerClass.timer.schedule(new TimerTask() {
     	 @Override
		public void run() {
     	    	 runOnUiThread(new Runnable() {
        	   		     @Override
						public void run(){
        	   		    	 Toast toast=Toast.makeText(CalendarData.this, "Logging out automatically in 5 seconds", Toast.LENGTH_LONG);
	     	   		    	LinearLayout toastLayout = (LinearLayout) toast.getView();
	     	   		    	TextView toastTV = (TextView) toastLayout.getChildAt(0);
	     	   		    	toastTV.setTextSize(20);
	     	   		    	toastTV.setBackgroundColor(0x3399ff);
	     	   		    	toast.show();}
     		        });
     	    }
     	 },MyTimerClass.duration);
     	 
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
        	   		    	Toast toast=Toast.makeText(CalendarData.this, "Logging out automatically in 5 seconds", Toast.LENGTH_LONG);
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
    

    
    
    //When different keys are pressed, call the button1 and button2 method.
    public class LongOperation extends AsyncTask<String, Void, String> {
	        @Override
	        protected String doInBackground(String... params) {
	        	 String dates= CalendarClass.getStartEndOFWeek(week, year);
	              return dates;
	        }      
	        @Override
	        protected void onPostExecute(String dates) {
	        	  float size=20;
	   	     TableLayout table = (TableLayout) findViewById(R.id.tableview);   
		     table.setStretchAllColumns(true);  
		     table.setShrinkAllColumns(true);  
		     TableRow rowDayLabels = new TableRow(CalendarData.this);  
		     rowDayLabels.setGravity(Gravity.CENTER);  
		     TableRow rowDate = new TableRow(CalendarData.this);  
		     rowDate.setGravity(Gravity.CENTER);
		     TableRow rowEvent = new TableRow(CalendarData.this);  
		    
		     //adding all the days(Sun,Mon,...Sat)     
		     for (int i=0;i<=6;i++){
		    	  String text=weekNumbers[i];
		    	  TextView day1Label = new TextView(CalendarData.this); 
		    	  day1Label.setText(text);  
		    	  day1Label.setTextColor(Color.rgb(44,44,44));
    	 	      day1Label.setTextSize(size);
    	 	      day1Label.setTypeface(null,Typeface.BOLD);
		 	      rowDayLabels.addView(day1Label); 
		 	    
		     }     
		     // next, add all the dates
		     for (int i=0;i<=6;i++){
		    	  String text=weekNumbers2[i];
		    	  TextView day1Label = new TextView(CalendarData.this); 
		    	  day1Label.setText(text);  
		    	  day1Label.setTextColor(Color.rgb(44,44,44));
	    	 	 day1Label.setTextSize(size);
	    	 	 day1Label.setTypeface(null,Typeface.BOLD);
		 	      rowDate.addView(day1Label); 
		     }     
		     //finally, add in the events
		     for (int i=0;i<=6;i++){
		    	  String text=weekNumbers3[i];
		    	  TextView day1Label = new TextView(CalendarData.this); 
		    	  day1Label.setText(text);  
		    	  day1Label.setTextColor(Color.rgb(44,44,44));
	    	 	  day1Label.setTextSize(size);
		 	      rowEvent.addView(day1Label); 
		     }     
		     
		     table.addView(rowDayLabels);  
		     table.addView(rowDate);  
		     table.addView(rowEvent); 
	        }
	        @Override  protected void onPreExecute() { }
	        @Override protected void onProgressUpdate(Void... values) {}
	  }

    
    /**
     * This operation is called when previous week and next week buttons are clicked.
     * Should be the same as LongOperation
     * @author yixin
     *
     */
    private class LongOperation1 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
        	String dates= CalendarClass.getStartEndOFWeek(week, year);
              return dates;
        }      

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		@SuppressLint("NewApi")
		@Override
        protected void onPostExecute(String dates) {          
        	 //show the week range
            TextView textView = (TextView)findViewById(R.id.tvWeekMonthYear);
            textView.setText("");
        	textView.append(dates);      	
          	uri = Calendars.CONTENT_URI; 
        	builder = Instances.CONTENT_URI.buildUpon();   	
            cr = getContentResolver(); 
         
             // next, fill in the middle column that shows date of the week		
        	 ArrayList<String> sevendays=CalendarClass.getWeek(week, year);
     		 weekNumbers2= new String[] { sevendays.get(0),sevendays.get(1),sevendays.get(2),sevendays.get(3),sevendays.get(4),sevendays.get(5),sevendays.get(6)};	 
    		 if (CalendarClass.hasCalendar(calendarName, googleAccount, cr, week, uri,builder)){
    			 ArrayList<Event> events=CalendarClass.getEvents(calendarName, googleAccount, cr, week, uri,builder);
    		     weekNumbers3= CalendarClass.sortEvents(events);
    			
    	     
    	     TableLayout table = (TableLayout) findViewById(R.id.tableview);        
    	     table.removeAllViews();
    	     
    	     table.setStretchAllColumns(true);  
    	     table.setShrinkAllColumns(true);  
    	     TableRow rowDayLabels = new TableRow(CalendarData.this);  
    	     rowDayLabels.setGravity(Gravity.CENTER);  
    	     TableRow rowDate = new TableRow(CalendarData.this);  
    	     rowDate.setGravity(Gravity.CENTER);
    	     TableRow rowEvent = new TableRow(CalendarData.this);  
    	    
    	     //adding all the days     
    	     for (int i=0;i<=6;i++){
    	    	  String text=weekNumbers[i];
    	    	  TextView day1Label = new TextView(CalendarData.this); 
    	    	  day1Label.setText(text);  
    	    	  day1Label.setTextColor(color);
    	 	      day1Label.setTextSize(size);
    	 	      day1Label.setTypeface(null,Typeface.BOLD);
    	 	      day1Label.setTextColor(Color.rgb(44,44,44));
    	 	      rowDayLabels.addView(day1Label); 
    	     }     
    	     // next, add all the dates
    	     for (int i=0;i<=6;i++){
    	    	  String text=weekNumbers2[i];
    	    	  TextView day1Label = new TextView(CalendarData.this); 
    	    	  day1Label.setText(text);  
    	    	  day1Label.setTextColor(color);
    	    	  day1Label.setTypeface(null,Typeface.BOLD);
    	 	     day1Label.setTextSize(size);
    	 	    day1Label.setTextColor(Color.rgb(44,44,44));
    	 	      rowDate.addView(day1Label); 
    	     }     
    	     //add in the events
    	     for (int i=0;i<=6;i++){
    	    	  String text=weekNumbers3[i];
    	    	  TextView day1Label = new TextView(CalendarData.this); 
    	    	  day1Label.setText(text);  
    	 	      day1Label.setTextSize(size);
    	 	      day1Label.setTextColor(color);
    	 	      day1Label.setTextColor(Color.rgb(44,44,44));
   	 	          rowEvent.addView(day1Label); 
    	     }     
    	     
    	     table.addView(rowDayLabels);  
    	     table.addView(rowDate);  
    	     table.addView(rowEvent);
        }
    	     else{textView.append("No calendar available.");}
    	}
        @Override  protected void onPreExecute() {}
        @Override  protected void onProgressUpdate(Void... values) { }
  }
    
   /** 
    * Keyboard interaction: Press "A" is equivalent to pressing button1.
    * Press "S" is equivalent to pressing button 2.
    */
   @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {   
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
