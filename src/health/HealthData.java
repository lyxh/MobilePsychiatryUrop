package health;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import main.StartActivity;

import timer.MyTimerClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import calendar.CalendarClass;

import com.androidplot.Plot;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.*;
import com.example.mobilepsychiatry.R;
import android.graphics.*;


/**
 * @author yixin
 *
 */
public class HealthData extends Activity{
	private String[] weeknumbers;
    private XYPlot mySimpleXYPlot;
    private XYPlot mySimpleXYPlot2;
    public String start_time, now;
    private int week, year=2013;
    Time today = new Time(Time.getCurrentTimezone());	
    private boolean debug=true;
	private String user_id=StartActivity.tag_id;
    private String health_id=StartActivity.health_id;
    Button button1;
    Button button2 ;
	public int color=0x444444;
    public static final String name1= "patient_ids";
    public static final String name2= "patientIdDateToScore";
    public static final String name3= "idToLatestDate";
    public static boolean drawDailyScore=true;
    public static boolean drawRandom=false;
    public static boolean demo=true;
    public static int day=0;
    public static int hour=0;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState); 
	        setContentView(R.layout.healthdata);
	       
	        //get the week number
	        today= new Time(Time.getCurrentTimezone());//
	        today.setToNow();
	        week= today.getWeekNumber();
	        day=today.weekDay;
	       if (day==0) week+=1;
	        
	        hour=today.hour;
	        Log.i("HealthData", "right now, the day is "+day);
	        Log.i("HealthData", "right now, the hour is "+hour);
	        Log.i("HealthData", "right now, the time is "+today.toString());
	         //update the year to new year if necessary
	         int y= today.year;
	         if(y>year){year=y;}
	        // next,get the seven dates of the this week
	     	String dates= CalendarClass.getStartEndOFWeek(week, year);
	        TextView textView = (TextView)findViewById(R.id.tvWeekMonthYear);
	        textView.setText("");
	    	textView.append(dates);  
	   /*      if (health_id.equals("No such resident")){
	        	 ((TextView)findViewById(R.id.title)).append("No sensor data available");
	         }
	         else{
	         */
	           //  new DownloadFilesTask().execute();
	        	 DataManage.logName(HealthData.this, name1);
		    	 final ArrayList<String> sevendays=CalendarClass.getWeek(week, year);
		    	 
		    	 //get the seven days for this week, fetch relevant scores and draw the scores.
		    	 weeknumbers= new String[] { sevendays.get(0),sevendays.get(1),sevendays.get(2),sevendays.get(3),sevendays.get(4),sevendays.get(5),sevendays.get(6)};	 
		    	 ArrayList<String> sevenDays=CalendarClass.getSevenDatesForScore(week, year);
    			 ArrayList<String> eda_scores= new ArrayList<String>();//DataManage.fetchEdaScoreForWeekYear(HealthData.this, sevenDays, health_id);
    			 ArrayList<String> acce_scores=new ArrayList<String>();//DataManage.fetchAcceScoreForWeekYear(HealthData.this, sevenDays, health_id);
        	    draw(weeknumbers,eda_scores,acce_scores);
		    	
		          //next, implement the button clickListeners
		  		  //When button is clicked, go to the next or previous week's calendar
		          button1 = (Button) findViewById(R.id.previousweek);
		          button2 = (Button) findViewById(R.id.nextweek);
		          
		          button1.setOnClickListener(new View.OnClickListener() {
		                   @Override  public void onClick(View v) {
		               	        week--;buttonClick();
		               	   }
		           });     
		          button2.setOnClickListener(new View.OnClickListener() {
			              @Override public void onClick(View v) {
			             	    week++; buttonClick();
		            	  }               
		          });

	       //  }//end of else
 
   
	     	MyTimerClass.timer.cancel();
		    MyTimerClass.timer=new Timer();
	     	//The first task is to show the user that he "is going to be logged out in 5 secs", which will happen in 20 secs.
	     	MyTimerClass.timer.schedule(new TimerTask() {
	     	    @Override
				public void run() {
	     	    	 runOnUiThread(new Runnable() {
	     	    		@Override
						public void run(){
        	   		    	Toast toast=Toast.makeText(HealthData.this, "Logging out automatically in 5 seconds", Toast.LENGTH_LONG);
        	   		    	LinearLayout toastLayout = (LinearLayout) toast.getView();
        	   		    	TextView toastTV = (TextView) toastLayout.getChildAt(0);
        	   		    	toastTV.setTextSize(20);
        	   		    	toast.show();}   });
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
	 
    
			private void buttonClick(){
			 	String dates= CalendarClass.getStartEndOFWeek(week, year);
			    TextView textView = (TextView)findViewById(R.id.tvWeekMonthYear);
			    textView.setText(dates);  
				//seven days
			    ArrayList<String> sevendays=CalendarClass.getWeek(week, year);     
			 	weeknumbers= new String[] { sevendays.get(0),sevendays.get(1),sevendays.get(2),sevendays.get(3),sevendays.get(4),sevendays.get(5),sevendays.get(6)};	 
			 	ArrayList<String> sevenDays=CalendarClass.getSevenDatesForScore(week, year);
					ArrayList<String> eda_scores=DataManage.fetchEdaScoreForWeekYear(HealthData.this, sevenDays, health_id);
					ArrayList<String> acce_scores=DataManage.fetchAcceScoreForWeekYear(HealthData.this, sevenDays, health_id);
			    draw(weeknumbers,eda_scores,acce_scores);  	
			}   
    
	    /**
	     * Given dates and scores, draw the plot. 
	     * @param dates: of length 7, each in terms MMM dd, ex: Aug 27
	     * @param scores: an ArrayList of strings, each string is the hourly score. Has lenth 24*7
	     */
	    private void draw(String[] dates, ArrayList<String> eda_scores, ArrayList<String> acce_scores){       	
	        assert eda_scores.size()==24*7;assert acce_scores.size()==24*7;
	        double prob=0.1;
	        
	        Number[] series1Numbers = new Number[24*7], series2Numbers = new Number[24*7],years = new Number[24*7];
	        for (int i1=0;i1<24*7;i1++){
	        	if(drawRandom){
	        		 series1Numbers[i1]=Math.random()*5;
	        		 series2Numbers[i1]=Math.random()*1;
	        	}     
	        	else if (demo){
	        		     int current=day*24+hour;
	        			 if (Math.random()<0.1)
	  	        		   series1Numbers[i1]=0.5+Math.random()*3;
	  	        		 else{series1Numbers[i1]=0.5+Math.random();}
	        			 series2Numbers[i1]=Math.random()*1;	 	        		       		 
	        	}
	        	else{ 
	        		String score= eda_scores.get(i1);
		        	series1Numbers[i1]=Double.valueOf(score);
		        	series2Numbers[i1]=Double.valueOf(acce_scores.get(i1));
	        	}        	
	        	 years[i1]=i1;
	        }
	        Log.i("HealthData.draw","eda_score.size="+ eda_scores.size());
	        Log.i("HealthData.draw","series1Numbers.length="+ series1Numbers.length);
	        Log.i("HealthData.draw","years.length="+ years.length);
	        Log.i("HealthData.draw","acce_score.size="+ acce_scores.size());
	        Log.i("HealthData.draw","series2Numbers.length="+ series2Numbers.length);	        
	        
	        SimpleXYSeries series1 =  new SimpleXYSeries(Arrays.asList(years),Arrays.asList(series1Numbers),"EDA score");
	        BarFormatter series1Format = new BarFormatter(Color.argb(100, 0, 200, 0), Color.rgb(0, 50, 0));
	        SimpleXYSeries series2 =  new SimpleXYSeries(Arrays.asList(years),Arrays.asList(series2Numbers),"Accelerometer score");
	        LineAndPointFormatter series2Format =  new LineAndPointFormatter(Color.rgb(255,147,0), /*line color*/ null/* point color*/,null,null);           
	        // add new series to the xyplot:
	        mySimpleXYPlot= (XYPlot) findViewById(R.id.mySimpleXYPlot1);
	        mySimpleXYPlot2= (XYPlot) findViewById(R.id.mySimpleXYPlot2);
	        mySimpleXYPlot.clear();
	        mySimpleXYPlot2.clear();
	        mySimpleXYPlot.addSeries(series1, series1Format);
	        mySimpleXYPlot2.addSeries(series2, series2Format);
	        
	        //set the x axis
	        String message="";
	        String[] x=new String[24*7];
	        for (int i=0;i<24;i++){
	        	int mode=i%24;
	        	if (mode==0) {x[i]=dates[i/24];}
	        	else{
	        		if(mode<12){x[i]=String.valueOf(mode)+"am";}
	        		else if(mode==12){x[i]="12pm";}
	        		else{x[i]=String.valueOf(mode-12)+"pm";}       		
	        		}
	        	message+=x[i];
	        }
	        for (int i=24;i<24*7;i++){
	        	int mode=i%24;
	        	if (mode==0) {x[i]=dates[i/24];}
	        	else{x[i]="";}
	        	message+=x[i];
	        }
	        
	        setUpPlot(mySimpleXYPlot);
	        MyIndexFormat mif = new MyIndexFormat (); mif.Labels=x; 
            mySimpleXYPlot.getGraphWidget().setDomainValueFormat(mif); 
	        mySimpleXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL,6);	
	        mySimpleXYPlot.getGraphWidget().getDomainLabelPaint().setTextSize((float) 0.01);
	        mySimpleXYPlot.getGraphWidget().getDomainOriginLabelPaint().setTextSize((float) 0.01);
	        mySimpleXYPlot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.TRANSPARENT);
	        mySimpleXYPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.TRANSPARENT);
	        mySimpleXYPlot.setPlotMarginBottom(0);
	        mySimpleXYPlot.setPlotPaddingBottom(0);
	       // mySimpleXYPlot.getLayoutManager().remove(mySimpleXYPlot.getLegendWidget());
	        mySimpleXYPlot.getLayoutManager().remove(mySimpleXYPlot.getDomainLabelWidget());
	        //set range 
	        mySimpleXYPlot.setRangeBoundaries(0, 5, BoundaryMode.FIXED );
	        mySimpleXYPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL,1);
	       
	        mySimpleXYPlot.redraw();        
	 
	        setUpPlot(mySimpleXYPlot2);
	        mySimpleXYPlot2.setPlotMarginTop(0);
	        mySimpleXYPlot2.setPlotPaddingTop(0);
            mySimpleXYPlot2.getGraphWidget().setDomainValueFormat(mif); 
	        mySimpleXYPlot2.setDomainStep(XYStepMode.INCREMENT_BY_VAL,6);	        
	        //set range 
	        mySimpleXYPlot2.setRangeBoundaries(0, 1, BoundaryMode.FIXED );
	        mySimpleXYPlot2.setRangeStep(XYStepMode.INCREMENT_BY_VAL,1);
	        mySimpleXYPlot2.getGraphWidget().setPaddingBottom(30);
	        
	       
	        mySimpleXYPlot2.redraw(); 
	        }

	    
	    private void setUpPlot(XYPlot mySimpleXYPlot){
	        // setup our line fill paint to be a slightly transparent gradient:
	        Paint lineFill = new Paint();
	        lineFill.setAlpha(200);
	        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));
	        mySimpleXYPlot.getGraphWidget().setPadding(0,10,0,10);
	        mySimpleXYPlot.getBackgroundPaint().setColor(Color.TRANSPARENT);
	        mySimpleXYPlot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
	        mySimpleXYPlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
	        mySimpleXYPlot.setPlotMargins(0, 0, 0, 0);
	        mySimpleXYPlot.setPlotPadding(0, 0, 0, 0); 
	        mySimpleXYPlot.setGridPadding(0, 10, 5, 0);
	        mySimpleXYPlot.setBackgroundColor(Color.WHITE);
	        mySimpleXYPlot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
	        mySimpleXYPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
	        mySimpleXYPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
	        mySimpleXYPlot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);
	        mySimpleXYPlot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
	        mySimpleXYPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
	        mySimpleXYPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
	        mySimpleXYPlot.getLegendWidget().setSize(new SizeMetrics(30, SizeLayoutType.ABSOLUTE, 500, SizeLayoutType.ABSOLUTE));
		    mySimpleXYPlot.getLegendWidget().getTextPaint().setColor(Color.BLACK); 
		    //mySimpleXYPlot.getLegendWidget().set
	        //mySimpleXYPlot.getLegendWidget().setPositionMetrics(new PositionMetrics((float)10, XLayoutStyle.ABSOLUTE_FROM_RIGHT, (float)10, YLayoutStyle.ABSOLUTE_FROM_BOTTOM,null));
	       //  mySimpleXYPlot.position(mySimpleXYPlot.getLegendWidget(), 20, XLayoutStyle.ABSOLUTE_FROM_RIGHT,35,YLayoutStyle.ABSOLUTE_FROM_BOTTOM, AnchorPosition.RIGHT_BOTTOM);
	       //TODO: SET THE LENGEND COLOR NOT TO GRAY....getGraphWidget().get.getRangeOriginLinePaint().setColor(Color.BLACK);	        
	        //Remove legend
	      //mySimpleXYPlot..getLayoutManager().remove(mySimpleXYPlot.getLegendWidget());
	      //  mySimpleXYPlot.getLegendWidget().setSize(new SizeMetrics(11, SizeLayoutType.ABSOLUTE, 170, SizeLayoutType.ABSOLUTE));
	       //mySimpleXYPlot.disableAllMarkup(); 
	        Widget gw =  mySimpleXYPlot.getGraphWidget();
		    // FILL mode with values of 0 means fill 100% of container:
		    SizeMetrics sm = new SizeMetrics(0,SizeLayoutType.FILL, 0,SizeLayoutType.FILL);
		    gw.setSize(sm);
	        mySimpleXYPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
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
	        	   		    	Toast toast=Toast.makeText(HealthData.this, "Logging out automatically in 5 seconds", Toast.LENGTH_LONG);
	        	   		    	LinearLayout toastLayout = (LinearLayout) toast.getView();
	        	   		    	TextView toastTV = (TextView) toastLayout.getChildAt(0);
	        	   		    	toastTV.setTextSize(20);
	        	   		    	toast.show();}});}
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
		    
		    
		    
		    private class DownloadFilesTask extends AsyncTask<String, Integer, String> {
		        // Do the long-running work in here
	        	ArrayList<String> fileIds = new ArrayList<String>();
		        @Override
				protected void onProgressUpdate(Integer... progress) {/*  setProgressPercent(progress[0]);*/ }
                @Override protected String doInBackground(String... arg0) {	        	
		        	
                	Calendar calendar1 = Calendar.getInstance();
					calendar1.set(year, today.month, today.monthDay);
					long millisec = calendar1.getTimeInMillis();
					Date end_date = new Date(millisec);
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					now= formatter.format(end_date);
					
					calendar1.add(Calendar.DATE,-489);
		   		    long millisec2 = calendar1.getTimeInMillis();
					Date start_date = new Date(millisec2);
				    start_time= formatter.format(start_date);
				    user_id="00123456789";//TODO： change back to StartActivity.tag_id;
				    health_id="13";//StartActivity.health_id;
		        	
				    //if (debug) DataManage.deleteResidentPatientId(HealthData.this, user_id);
					if (debug) {				     
						DataManage.logName(HealthData.this,name1);	
						DataManage.logName(HealthData.this,name2);	
						DataManage.logName(HealthData.this,name3);	
				    }
				    
		        	if ( DataManage.patientExists(HealthData.this, user_id) ){ 		
			              if (debug) Log.i("HealthData","Resident already exists.");
			               //old time: read from preference3 to now.
			              if (!DataManage.patientLatestDateExists(HealthData.this, health_id) ) {
			        	      if(debug)  Log.i("HealthData.Should not be here"," name3 preference does not exist?");
			        	   }
			              else {
			            	  String start_time_from_preferenece=DataManage.getLatestDate(HealthData.this, health_id);
			            	  if (start_time_from_preferenece.length()==13){
			            	  //change the start_time format from 2012-12-22-01 to 2012-12-22 01:00:00
			            	  start_time= start_time.substring(0, 10)+start_time.substring(11, 13)+":00:00";
			            	  }
			              }
				           try {fileIds = DataManage.getCreatedDataFromId(13,"2012-04-27 00:00:00", "2013-07-21 23:59:59");}// start_time, now);
						   catch (Exception e) {Log.i("HealthData(Line getCreatedDataFromId)","Exception");}
				           if (debug) Log.i("HealthData","Fetching data file ids list from "+ start_time+" to "+now+". Getting "+ fileIds.size() + " files.");
			        }
		            else{
		            	if (debug) Log.i("HealthData","Adding new resident.");
		            	DataManage.addResidentPatientId(HealthData.this,user_id, health_id);		
		          	    try {fileIds =DataManage.getCreatedDataFromId(13, start_time,now);}
					    catch (Exception e)  {Log.i("HealthData(Line getCreatedDataFromId)","Exception");}
			   		    if (debug) Log.i("HealthData","Fetching data file ids list from "+ start_time+" to "+now+". Getting "+ fileIds.size() + " files.");
			   		 }	
		        	
		        	//for testing!
		        	fileIds.clear();
		        	for (int i=1;i<=1;i++){fileIds.add(i+"");}
		        	
		        	//download file
		   			 ConnectivityManager connMgr = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
		        	 NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		        	 ArrayList<String> times=new ArrayList<String>();
		        	 if (networkInfo != null && networkInfo.isConnected()) {	        		    
		        		 for (String file_id:fileIds){
		   					   if (debug)  Log.i("HealthData","Download new file of id "+file_id+ ". ");
		   					   String[] item=DataManage.downloadFile(health_id,file_id, HealthData.this);
		   					   if (!item[0].equals("")) times.add(item[0]);
				   		 }		
		        	 }
		        	 else { if (debug) Log.i("HealthData","No network connection available."); }
		        	 
		        	 //finally, update the newest time and modify the name3 preferences.	        	 
				     if(times.size()!=0){
			        	 DataManage.updateLatest(HealthData.this,times,health_id);   
				     }
				     DataManage.logName(HealthData.this,name1);	
				     DataManage.logName(HealthData.this,name2);	
				     DataManage.logName(HealthData.this,name3);	
				     return "";
				}
				
		        // This is called when doInBackground() is finished
		        @Override
				protected void onPostExecute(String result) {
		        	 if (debug) Log.i("HealthData", "Asyntask finished.");
		        }
		    }
   
		 
		    
}






