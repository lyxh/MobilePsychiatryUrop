package health;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.clutch.dates.StringToTime;

public class Client extends Activity  {
	
   private static String server= "http://labs.ashametrics.com/openmrs/";
   private static int numOfPatients=0;
   private static ArrayList<Integer> IDs= new ArrayList<Integer>();
   private static Map<Integer, Integer> idToFileNum=new HashMap<Integer,Integer>();
   
   @Override protected void onCreate(Bundle state){ super.onCreate(state); }
   @Override protected void onStop(){super.onStop();}
   
   /**
    * @param args
    * @throws Exception
    */
   public void main(String[] args) throws Exception{
	   String[] result=DataManage.downloadFile("867", "2", Client.this);
	   System.out.println(result[0]);System.out.println(result[1]);System.out.println(result[2]);
	   //listAllPatients();29,28,26,23,22,21,20,17,16-4,2
	  //getUploadedDataFromId(13,  "2012-04-26 00:00:00", "2012-04-27 23:59:59");
	 //  getCreatedDataFromId(13,  "2012-04-27 00:00:00", "2013-07-21 23:59:59");
	 // StringBuilder response=downloadFileOfId("867");
	
   }

  public static StringBuilder test(){
	  StringBuilder response = new StringBuilder("Not yet get the input...");
	  return response;
  }
   
   /**
    * 
    * @return
    * @throws Exception
    */
	public static ArrayList<Integer> listAllPatients() throws Exception{
		ArrayList<Integer> result= new ArrayList<Integer>();
		 // run an HTTP fetch
	    URI u = new java.net.URI(server+"moduleServlet/physlabdispatch/api/listpatients");
	    HttpURLConnection httpUrlConnection = (HttpURLConnection) u.toURL().openConnection();
	    OutputStream outputStream = null;
	    String response = null;
	    httpUrlConnection.setConnectTimeout(7500);
	    httpUrlConnection.setReadTimeout(7500);
	    httpUrlConnection.setRequestMethod("POST");
	    httpUrlConnection.setDoOutput(true);
	    httpUrlConnection.connect();
	    outputStream = httpUrlConnection.getOutputStream();    
	    httpUrlConnection.setInstanceFollowRedirects(false);
	    int code = httpUrlConnection.getResponseCode();
	    if(code == 200){
			// parse the JSON response
			response = fromInputStream(httpUrlConnection.getInputStream());
			//System.err.println(response);
			JSONArray arr = (JSONArray)JSONValue.parse(response);
			JSONObject patient;
			numOfPatients=arr.size();
			System.out.println(arr.size()+" patients found.");
			for(int i=0;i<arr.size();i++){
			    patient = (JSONObject)arr.get(i);
			    Integer idInt=new BigDecimal((Long) patient.get("id")).intValueExact();
			    System.out.println("Patient "+patient.get("patient")+"; ID: "+patient.get("id"));
			    result.add(idInt);
			}
	    }else{
		        System.err.println("Error listing patients: "+code);
	    }
		return result;
	}  
	 
	
	
	/**
	 * 
	 * @param patientid
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<String> getUploadedDataFromId(Integer patientid, String startTime, String endTime) throws Exception{
		ArrayList<String>  result=new ArrayList<String> ();
		StringToTime start = new StringToTime(startTime.trim());
		StringToTime end = new StringToTime(endTime.trim());
		URI u = new java.net.URI(server+"moduleServlet/physlabdispatch/api/list/byserver/"+patientid+"/"+start.getTime()+"/"+end.getTime());
	    HttpURLConnection httpUrlConnection = (HttpURLConnection) u.toURL().openConnection();
	    OutputStream outputStream = null;
	    String response = null;
	    httpUrlConnection.setConnectTimeout(7500);
	    httpUrlConnection.setReadTimeout(7500);
	    httpUrlConnection.setRequestMethod("POST");
	    httpUrlConnection.setDoOutput(true);
	    httpUrlConnection.connect();
	    outputStream = httpUrlConnection.getOutputStream();
	    
	    httpUrlConnection.setInstanceFollowRedirects(false);
	    int code = httpUrlConnection.getResponseCode();
	    if(code == 200){
			response = fromInputStream(httpUrlConnection.getInputStream());
			JSONArray arr = (JSONArray)JSONValue.parse(response);
			JSONObject file;
			System.out.println(arr.size()+" files found.");
			idToFileNum.put(patientid,arr.size());
			for(int i=0;i<arr.size();i++){
			    file = (JSONObject)arr.get(i);
			    Integer idInt=new BigDecimal((Long)file.get("id")).intValueExact();
			    System.out.println("ID: "+ file.get("id")+"; Name: "+file.get("filename")+"(Size: "+file.get("size")+" bytes).");
			    result.add(String.valueOf(file.get("id")));
			    Log.i("Client.getUploadedDataFromID","Result has size "+result.size());
			}
	    }else{
		    System.err.println("Error listing patient files for ["+patientid+"]: "+code);
		    Log.i("Client.getUploadedDataFromID","Error listing patient files for ["+patientid+"]: "+code);
	    }
	    return result;
	 }
	    
	

	/**
	 * 
	 * @param patientid
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static ArrayList<String>  getCreatedDataFromId(Integer patientid, String startTime, String endTime) throws Exception{
		ArrayList<String>  result=new ArrayList<String> ();
		StringToTime start = new StringToTime(startTime.trim());
		StringToTime end = new StringToTime(endTime.trim());
	    // run an HTTP fetch
	    URI u = new java.net.URI(server+"moduleServlet/physlabdispatch/api/list/byfile/"+patientid+"/"+start.getTime()+"/"+end.getTime());
		HttpURLConnection httpUrlConnection =  (HttpURLConnection) u.toURL().openConnection();
	    OutputStream outputStream = null;
	    String response = "Initialized in getCreatedDataFromId";
	    httpUrlConnection.setConnectTimeout(7500);
	    httpUrlConnection.setReadTimeout(7500);
	    httpUrlConnection.setRequestMethod("POST");
		httpUrlConnection.setDoOutput(true);
	    httpUrlConnection.connect();
		outputStream = httpUrlConnection.getOutputStream();
		httpUrlConnection.setInstanceFollowRedirects(false);
	    int code= httpUrlConnection.getResponseCode();
	    if(code == 200){
			response = fromInputStream(httpUrlConnection.getInputStream());
			JSONArray arr = (JSONArray)JSONValue.parse(response);
			JSONObject file;
			//System.out.println(arr.size()+" files found.");
			idToFileNum.put(patientid,arr.size());
			for(int i=0;i<arr.size();i++){
			    file = (JSONObject)arr.get(i);
			    //Integer idInt=new BigDecimal((Long)file.get("id")).intValueExact();
			   // System.out.println("ID: "+ file.get("id")+"; Name: "+file.get("filename")+"(Size: "+file.get("size")+" bytes).");
			    result.add(String.valueOf(file.get("id")));
			}
	    }else{
		System.err.println("Error listing patient files for ["+patientid+"]: "+code);
	    }
	    return result;
    }



	/**
	 * 
	 * @param fileid
	 * @return 
	 * @throws Exception
	 */
	public static String downloadFileOfId(String fileid) throws Exception {
		String response = "No getting the content of file!";
	    URL u = new URL( "http://labs.ashametrics.com/openmrs/"+"moduleServlet/physlabdispatch/api/download/"+fileid);
		HttpURLConnection httpUrlConnection =  (HttpURLConnection) u.openConnection();
		
		//  OutputStream outputStream = null;
	    httpUrlConnection.setConnectTimeout(7500);
	    httpUrlConnection.setReadTimeout(7500);
	    httpUrlConnection.setRequestMethod("GET");
	    httpUrlConnection.setDoOutput(true);
	    httpUrlConnection.connect();
		httpUrlConnection.setInstanceFollowRedirects(false);
	    int code= httpUrlConnection.getResponseCode();
		if(code == 200){
			response = fromInputStream(httpUrlConnection.getInputStream()); 	
	    }else{
	      	System.err.println("Error retrieving file ["+fileid+"]");
	    }
	 return response;
    }
	
	
	/**
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private  static String fromInputStream(InputStream inputStream) throws IOException {
		if (inputStream == null)
		    return null;
		BufferedReader reader = null;		
		try {
		    reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		    StringBuilder response = new StringBuilder(512);	    
		    String line = null;
		    while ((line = reader.readLine()) != null)
			response.append(line);		    
		    return response.toString();
		} finally {
		    if (reader != null)
			try {
			    reader.close();
			} catch (Throwable t) {
			    // Really nothing we can do but log the error
			}
		}
	}
	  
    private static String parse(String fileContent) {
    	String ret="";
    	fileContent=fileContent.replaceAll("\\s","");
		// Parse the string, get the timestamp and data.
		String[] result=fileContent.split("\\*");
		//ArrayList<Data>  finalResult=new ArrayList<Data>();
		//parse each individual line
		for(int i=1;i<result.length;i++){	
			if( result[i].substring(0,4).equals("0005")){//result[i].length()==54 && 
				//ret+=result[i];
				String time=result[i].substring(6, 6+16);//begin to end-1(ex:begin=0,end=3:read 0,1,2)
				String x=result[i].substring(22, 22+4);
				String y=result[i].substring(26, 26+4);
				String z=result[i].substring(30, 30+4);
				String skinTemp=result[i].substring(34, 34+4);
				String edap=result[i].substring(38, 38+4);
				String edabias=result[i].substring(42, 42+4);
				String ambientTemp=result[i].substring(46, 46+4);
				String ambientHumi=result[i].substring(50, 50+4);
				//String check=result[i].substring(54, 54+2);
				//Data indi=new Data(time,edap,edabias);
				double EDA =getEDA(edap,edabias);
				if(EDA>0){ret+="*"+time+" "+edap+" "+ edabias+ " " +EDA +"   ";}
			}
		}
		
		System.out.println(ret);
		return ret;
	}
    
    public static double getEDA(String edap, String edabias){
    	int edaP=Integer.parseInt(edap,16);
		int edaB=Integer.parseInt(edabias,16);
		int K=1;
		double EDA =K*((edaB-edaP)/(double)(4096-edaB));
		return EDA;
    }
}
