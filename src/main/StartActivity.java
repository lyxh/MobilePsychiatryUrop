package main;
import health.DataManage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import main.UserClass.FetchEachUID;
import main.UserClass.FetchUIDList;
import com.example.mobilepsychiatry.R;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import rfid.Reader;
import timer.MyAlarmService;
import timer.MyTimerClass;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * StartActivity is the activity launched after the app is launched. 
 * It shows the welcome message and the log in button. 
 * After clicking on the log in button, if the user id is found, then the MainActivity is launched.
 * If not, the app will query the server for all users.
 * @author yixin
 *
 */

public class StartActivity extends Activity {
	 public static String uid;
	 public static String username;
	 public static String tag_id;
	 public static String health_id;
	 public static String attendance;
     public static final String account_name="tablet_access";
     public static final String account_password="yixin*Service1";
	 public static ArrayList<String> keys= new ArrayList<String>();
	 TextView feedback;
	 public static final String url="http://hectorreyeshouse.org/hectorre/resident.json";
	 public static final String intermediate_url="http://hectorreyeshouse.org/hectorre/resident/";
	 public static boolean get=false;
	 public static boolean keySet=false, oneUidSet=false;
	 WebView myWebView,EachWebView;
	 private PendingIntent pendingIntent;
	 
	 //The reader
	 public static D2xxManager ftD2xx;
	FT_Device ftDev = null;
	int DevCount = -1;
	int currentIndex = -1;
	int openIndex = 0;	    
	/*local variables*/
	int baudRate; /*baud rate*/
	byte stopBit; /*1:1stop bits, 2:2 stop bits*/
	byte dataBit; /*8:8bit, 7: 7bit*/
	byte parity;  /* 0: none, 1: odd, 2: even, 3: mark, 4: space*/
	byte flowControl; /*0:none, 1: flow control(CTS,RTS)*/
	int portNumber; /*port number*/	 
	public static final int readLength = 512;
	public int readcount = 0;
	public int iavailable = 0;
	byte[] readData;
	public boolean bReadThreadGoing = false;	 
	boolean uart_configured =false;

     @Override
	 protected void onCreate(Bundle savedInstanceState) {
	         super.onCreate(savedInstanceState);
	         setContentView(R.layout.activity_start);	        	         
		     EachWebView = (WebView)StartActivity.this.findViewById(R.id.eachWebView);
		     EachWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		     myWebView = (WebView)StartActivity.this.findViewById(R.id.myWebView);  
		     feedback=(TextView)StartActivity.this.findViewById(R.id.feedback); 
	         MyTimerClass.timer.cancel();
		     MyTimerClass.timer=new Timer();
		     DataManage.addResidentPatientId(getApplicationContext(), "test_context", "12");///what is this about?
		
		     //lauch the new calendar so that the app will query the server every night.
		     Long interval=(long) (24*60*60*1000);//every day //test:10*1000
		     Intent myIntent = new Intent(StartActivity.this, MyAlarmService.class);
		     pendingIntent = PendingIntent.getService(StartActivity.this, 0, myIntent, 0);
		     AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
             Calendar calendar = Calendar.getInstance();
             //TODO: change this
			 //calendar.setTimeInMillis(System.currentTimeMillis());
             //run a task at every day 1am.
             calendar.set(Calendar.HOUR_OF_DAY, 1);
             calendar.set(Calendar.MINUTE, 0);
             calendar.set(Calendar.SECOND, 0);
			 alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),interval, pendingIntent);
			 //Toast.makeText(StartActivity.this, "Start Alarm", Toast.LENGTH_LONG).show(); 
			 
			 
			 //below the reader code:
			//Intialize the ftD2xx(the manager)
		    	try {
		    		ftD2xx = D2xxManager.getInstance(this);
		    	} catch (D2xxManager.D2xxException ex) {
		    		ex.printStackTrace();
		    		feedback.append("\n-exception");
		    	}
		       
			    //initialize the variables for configuration and reading
				readData = new byte[readLength];
				baudRate = 57600;
				byte dataBits = D2xxManager.FT_DATA_BITS_8;
				byte stopBits = D2xxManager.FT_STOP_BITS_1;
				parity = D2xxManager.FT_PARITY_NONE;
				short flowCtrlSetting= D2xxManager.FT_FLOW_NONE;	
				portNumber = 1; 
				//check if the library is set up.       
		        SetupD2xxLibrary();		    	
		        //Not sure if this is used. May be could not delete.		
				IntentFilter filter = new IntentFilter();
		        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		        filter.setPriority(500);        
		        feedback.append("\n-mUsbReceiver registered");
		        
				//the handler when the reader reads a message
		        final Handler handler =  new Handler(){
		        	@Override
		        	public void handleMessage(Message msg){
		        		String tag_id="";
		        		if(iavailable > 0){
		        			for (int i = 0; i < iavailable; i++) {
		        				tag_id+=(char)readData[i];
		        			}		        			
		        			feedback.append(tag_id.trim());		        			
		        			if (UserClass.patientExists(StartActivity.this, tag_id)){setUserInfoLogIn(tag_id);} 
		        	    	else{ new DownloadUidTask().execute();}
		        		}
		        	}
		        }; 
				
		        //get the number of connected device
		        int tempDevCount = ftD2xx.createDeviceInfoList(StartActivity.this);
				feedback.append("\n-tempDevCount is " + tempDevCount);
				if (tempDevCount > 0){DevCount = tempDevCount;}	
		        
				//if there is device connected, then open that device and start the reading thread
		        if (tempDevCount > 0){
					int tmpProtNumber = openIndex + 1;
					if( currentIndex != openIndex ){
						if(null == ftDev){
							ftDev = ftD2xx.openByIndex(StartActivity.this, openIndex);
							feedback.append("\n-Device port " + openIndex + "opened");
						}
						else{
							synchronized(ftDev){ftDev = ftD2xx.openByIndex(StartActivity.this, openIndex);}
						}
					}
					else{return;}
					if(ftDev == null){
						feedback.append("\n-open device port("+tmpProtNumber+") NG, ftDev == null");
						return;
					}	
					
					//now that the device is open, we could first set the configuration and then start the reading thread
					if (ftDev.isOpen()){			
						ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);
						ftDev.setBaudRate(baudRate);
						ftDev.setDataCharacteristics(dataBits, stopBits, parity);								
						ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);							
						uart_configured =true;						
						currentIndex = openIndex;
						feedback.append("\n-open device port(" + tmpProtNumber + ") OK");							
						if(!bReadThreadGoing){
							bReadThreadGoing = true;					
							//start the new read thread
						    new Thread(new Runnable() {
						        public void run() {
						        	int i;
									while(bReadThreadGoing){
										try {
											Thread.sleep(50);
										} catch (InterruptedException e) {}
										synchronized(ftDev){
											iavailable = ftDev.getQueueStatus();				
											if (iavailable > 0) {							
												iavailable = Math.min(iavailable,readLength);							
												ftDev.read(readData, iavailable);
												Message msg = handler.obtainMessage();
												handler.sendMessage(msg);
											}
										}
									}
						        }
						    }).start();
							bReadThreadGoing = true;
						}
					}
					else {feedback.append("open device port(" + tmpProtNumber + ") NG");}
		    	}	
	    }
     //end of onCreate method
     
     
	     private void SetupD2xxLibrary () {
	     	if(!ftD2xx.setVIDPID(0x0403, 0xada1))
	     		feedback.append("\n-SetupD2xxLibrary ()  ftd2xx-java:setVIDPID Error");
	     }   

	    /** Called when the user clicks the Log in button. 
	     * first checks in preference if a corresponding uid exists.
	     * If so, fetch other information such as health id and so on and log the user in.
	     * If not, start a new DownloadUidTask to query all user ids.
	     * @throws IOException 
	     * @throws IllegalStateException */
	    public void start(View view) throws IllegalStateException, IOException { 
	    	EditText edit_text2 = (EditText)findViewById(R.id.password);   
            String tag_id=edit_text2.getText().toString();
            // UserClass.addResidentPatientId(StartActivity.this,tag_id, String uid);
	    	//if (UserClass.patientExists(StartActivity.this, tag_id)){	   
	    		//setUserInfoLogIn(tag_id);
	    //	} 
	    	//else{  
               new DownloadUidTask().execute();
	    	//}
	    }
	    

	    @Override
	    public boolean onKeyUp(int keyCode, KeyEvent event) {
	          Button button1 = (Button) findViewById(R.id.login);
	          switch (keyCode) {
	               case KeyEvent.KEYCODE_L:            	
	                      button1.performClick();
	                      return true;
	               default:
	                      return super.onKeyUp(keyCode, event);
	        }
	    }
      
	    /**
	     * Make EachWebView a new webview
	     */
	    public void makeNewWebView(){
	        runOnUiThread(new Runnable() {
	            @Override
				public void run(){
	            	EachWebView=new WebView(StartActivity.this);
	            }
	        });
	    }
	    
	    /**
	     * Show the toast message
	     * @param msg the message to show
	     */
	    public void showToast(final String msg) {
	        runOnUiThread(new Runnable() {
	            @Override
				public void run(){
	                Toast.makeText(StartActivity.this, msg, Toast.LENGTH_SHORT).show();
	            }
	        });
	    }

	    /**
	     * fetches user info
	     * @param msg the message to show
	     */
	    public void setUserInfoLogIn(final String tag_id) {
	       uid=UserClass.getPatientUID(StartActivity.this, tag_id);
 		   username=UserClass.getPatientName(StartActivity.this, uid);
 		   health_id=UserClass.getPatientHealth(StartActivity.this, uid);	    		  
 		   attendance=UserClass.getPatientAtten(StartActivity.this, uid);//TODO:delete after test. for testing health_id="13";
 		   Log.i("StartActivity.tag_id=",tag_id);
 		   Log.i("StartActivity.uid=",uid);
 		   Log.i("StartActivity.username=",username);
 		   Log.i("StartActivity.health_id=",health_id);
 		   Log.i("StartActivity.attendance=",attendance);
 		   Intent i = new Intent(getApplicationContext(), MainActivity.class);
	       startActivity(i);
	    }
    
	    
	    /**
	     * Query the server address (field url) to get a list of uids.
	     * Then for each uid, query the server again to add tag_id to preference 
	     *
	     */
	    private class DownloadUidTask extends AsyncTask<String, Integer, String> {
			@Override
			protected synchronized String doInBackground(String... arg0) {
				// If not, query the server for a list of currect uids		 
    		    FetchUIDList runnable=new UserClass.FetchUIDList( myWebView, feedback);
		  		Thread a= new Thread( runnable);
    		    try {
    		    	a.start();
    		    	a.join();
				} 
    		    catch (InterruptedException e) {Log.i("StartActivity, checking","thread running interruptedException");}		    	

		    	//hang while key not set
    		    while(!keySet){}
		    	Log.i("StartActivity", "Size of key= "+keys.size());
		    	//for each of the uid, fetch the tag_id and add it to preference
			    for (int i=0;i<keys.size();i++){			    	
			    	String uid=keys.get(i);
			    	Log.i("StartActivity", "Start looping for uid"+uid);
			    	get=false;	
			    	makeNewWebView();
			    	FetchEachUID a_runnable=new UserClass.FetchEachUID(EachWebView, feedback,uid,StartActivity.this);
			  		Thread a_thread= new Thread(a_runnable);
	    		    try {		    	 
	    		    	 a_thread.start(); 
	    		    	 a_thread.join();	    		    	 
					} 
	    		    catch (InterruptedException e) {Log.i("StartActivity, checking","thread running interruptedException");}		    	
	    		    while(!oneUidSet){}	 
	    		    oneUidSet=false;
			     }			    
			      keys.clear();
			      Log.i("StartActivity" , "Finished looping");
			      UserClass.logName(StartActivity.this,UserClass.name1);
			      UserClass.logName(StartActivity.this,UserClass.name2);
			      UserClass.logName(StartActivity.this,UserClass.name3);
			      UserClass.logName(StartActivity.this,UserClass.name4);
			      //after adding in all new tag_id, checks if the user provided tag id matches the database.
			      if (UserClass.patientExists(StartActivity.this, tag_id)){
			    	  setUserInfoLogIn(tag_id);
		    	  }
		    	  else{	    		  
		    		  showToast("Wrong tag id.");
		    	  }	
				  return null;
			}
			
			@Override
			protected void onPostExecute(String result) {
	        	 Log.i("HealthData", "Asyntask finished.");
	        }
	    }
	    
}
