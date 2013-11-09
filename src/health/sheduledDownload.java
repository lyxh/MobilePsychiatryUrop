package health;

import com.androidplot.xy.XYPlot;
import android.app.Activity;
import android.text.format.Time;

public class sheduledDownload extends Activity{
	private String[] weeknumbers;
    private XYPlot mySimpleXYPlot;
    private int week, year=2013;
    public String start_time, now;
    Time today;

    private boolean debug=true;
	private int user_id=1368813830;//Integer.parseInt(StartActivity.uid);
    private String health_id="13";//StartActivity.health_id;
    public static final String name1= "patient_ids";
    public static final String name2="patientIdToFileIds";
    public static final String name3="patientIdToLatestMili";
    public static final String name4="patientIdToCompleteDate";
    public static final String name5="patientIdDateToScore";
	    /*
	    
	    at each day at 2AM, do the following:
        1. from the server, fetch a list of resident and their corresponding ids
        2. Delete the information associated with deleted patient
        3. For each new resident:
           3.1 add new patient id to the database
           3.2 download all available files (time range: four weeks ago to now)  and save them into internal storage
           3.3 update the complete time to latest time of a file, add (patient_id, latest_date) to name4 preference.
           3.4 find out which files' date is before and on the latest date
           3.5 for files older than latest date, calculate a score for each date(Score = MeanEDA * standard deviation), store the score and delete the files
           3.4 for files on the latest date, store the patient_id+"File"+file_id to the name2 preference.
            
        4. For existing resident
          4.1 download all files and save them to internal storage(time range: stored latest_date to today)
          4.2 update the complete time to latest time of a file, modify (patient_id, latest_date) in name4 preference.
          4.3 find out which files' date is before and on the latest date
          4.4 for files older than latest date, calculate a score for each date, store the score and delete the files
          4.5 for files on the latest date, store the patient_id+"File"+file_id to the name2 preference. 
          
        
	   private static int weekNums=4;
	   
	    @Override
	    protected void onCreate(Bundle savedInstanceState){
	         super.onCreate(savedInstanceState);
	         setContentView(R.layout.scheduleddownload);
	    	 WebView webView = (WebView) findViewById(R.id.webView);
	    	 TextView contentView= (TextView) findViewById(R.id.feedback);
	    	 
	    	 //get the start and end date range
	    	 Time today= new Time(Time.getCurrentTimezone());
		     today.setToNow();
		     Long todaymilis=today.toMillis(true);
		     Long startmilis=todaymilis-1000*60*60*24*7*weekNums;
		     Time start=new Time();
		     start.set(startmilis);
    		 String startTime;
		    String endTime;
			
	    	 //do step 1 to modify the health_data_ids
	    	 ArrayList<Integer> health_data_ids=getResidentHealthIds(webView,contentView);
	    	 ResidentHealthData.setPatient_ids(health_data_ids);
	    	
	    	 //first, if the patient get deleted, remove all its data and files from ResidentHealthData fields
	    	 for (Integer i:ResidentHealthData.getFieldPatientIds().keySet()){
	    		 boolean exist=false;
	    		 for(Integer resident:health_data_ids){if (resident==i){exist=true;}}
	    		 if (!exist){
	    			 //TODO: remove a resident
	    			 removeResident(i);}
	    	 }
	    	 
	    	 //for each patient in the newer id database just queried from the server.
             for(Integer resident:health_data_ids){     	 
            	 //check against the exisitng database
            	 if (ResidentHealthData.getFieldPatientIds().containsKey(resident)){
            		 //downloadDataForWeekYear();
            	 }
            	 else{
            		 addResident(resident, weekNums); 
            		 //downloadDataForWeekYear();
            		 }
                    	 
            	 
             }
	    }
	    
	   
	    private void modifyResident(Integer resident, int weekNums2) throws Exception {
   		    String startTime = null;
			String endTime = null;
  		    ArrayList<String>  newFileIds=Client.getCreatedDataFromId(resident, startTime, endTime);
  		//    ArrayList<String> filenames=fetchFileList(resident);
			
		}




	    public static ArrayList<Integer> getResidentHealthIds(final WebView myWebView, TextView contentView){
	    	 final String account_name="tablet_access";
	         final String account_password="3217132";
	         final String url="http://hectorreyeshouse.org/hectorre/resident.json";
	    	 final ArrayList<Integer> ids= new ArrayList<Integer>();	   	
			 class MyJavaScriptInterface { 
		            private TextView contentView;
		            public MyJavaScriptInterface(TextView aContentView) {contentView = aContentView;}
                    boolean once=true;
		            @SuppressWarnings("unused") 
		            public void processContent(String aContent) { 
		                  final String content = aContent;
		                  contentView.post(new Runnable()  {    
		                       public void run() {    
		                        	if(once){
		                    		//parse json file located at the resident link
			                    	 JsonElement jelement = new JsonParser().parse(content);
			                    	 JsonArray jarray = jelement.getAsJsonArray();	                      	  
			                      	 for (int i=0;i<jarray.size();i++){
			                      		  JsonObject  jobject = jarray.get(i).getAsJsonObject();
			                      		  String uid = jobject.get("tag ID").toString().trim();
			                      		  uid=uid.replaceAll("^\"|\"$", "");
			                      	      //TODO:Add the health_id to the patient
			                      	      ids.add(Integer.parseInt(uid));}//end of for
			                      	 once=false;
		                    	    }//end of if                   	
		                        }//end of run
		                  });//end of new Runnable()
		             } //end of methof
		     } //end of class
	         myWebView.getSettings().setJavaScriptEnabled(true);
	    //     myWebView.addJavascriptInterface(new MyJavaScriptInterface(contentView), "INTERFACE"); 
	         myWebView.setWebViewClient(new WebViewClient() {  
			     boolean t=true;
			     @Override public boolean shouldOverrideUrlLoading(WebView webView, String url) {  return true; }			           
	             @Override   
	             public void onPageFinished(WebView view, String url)  {  
	            	if (t){
	            	        myWebView.loadUrl("javascript: {" +
	                        "document.getElementById('edit-name').value = '"+account_name +"';" +
	                        "document.getElementById('edit-pass').value = '"+account_password+"';" +
	                        "document.getElementById('user-login').submit(); };");
	            	        myWebView.loadUrl("http://hectorreyeshouse.org/user/login");
	            	        view.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].innerText);");
	            	        t=false;
	            	 }
	             }  
             });   
	         myWebView.loadUrl(url);
			return ids;	
	    }
*/
	


   
}
