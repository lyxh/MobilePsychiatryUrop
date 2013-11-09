package rfid;

import java.io.IOException;
import java.util.ArrayList;
import com.example.mobilepsychiatry.R;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Reader extends Activity {
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
    char[] readDataToText;
    public boolean bReadThreadGoing = false;
 
    boolean uart_configured =false;
    TextView feedback;
    Button writeButton; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
	    setContentView(R.layout.reader);
	    final TextView feedback=(TextView)findViewById(R.id.readerfeedback);	  
	    
		//Intialize the ftD2xx(the manager)
    	try {
    		ftD2xx = D2xxManager.getInstance(this);
    	} catch (D2xxManager.D2xxException ex) {
    		ex.printStackTrace();
    		feedback.append("\n-exception");
    	}
       
	    //initialize the variables for configuration and reading
		readData = new byte[readLength];
		readDataToText = new char[readLength];
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
        		if(iavailable > 0){
        			for (int i = 0; i < iavailable; i++) {
					    //append the message to the feedback(a TextView)
        				feedback.append(""+(char)readData[i]);
        			}
        		}
        	}
        }; 
		
        //get the number of connected device
        int tempDevCount = ftD2xx.createDeviceInfoList(Reader.this);
		feedback.append("\n-tempDevCount is " + tempDevCount);
		if (tempDevCount > 0){DevCount = tempDevCount;}	
        
		//if there is device connected, then open that device and start the reading thread
        if (tempDevCount > 0){
			int tmpProtNumber = openIndex + 1;
			if( currentIndex != openIndex ){
				feedback.append("\n-currentIndex != openIndex");
				if(null == ftDev){
					feedback.append("\n-null == ftDev");
					ftDev = ftD2xx.openByIndex(Reader.this, openIndex);
					feedback.append("\n-Device port " + openIndex + "opened");
				}
				else{
					feedback.append("\n-null != ftDev");
					synchronized(ftDev){
						ftDev = ftD2xx.openByIndex(Reader.this, openIndex);
						feedback.append("\n-Device port " + openIndex + "opened");}
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
										for (i = 0; i < iavailable; i++) {
											readDataToText[i] = (char) readData[i];
										}
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
        
    /**
     * Show the toast message. Useful for debugging
     * @param msg the message to show
     */
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
			public void run(){
                Toast.makeText(Reader.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
	protected void onDestroy() {
    	//this.unregisterReceiver(mUsbReceiver);
    	super.onDestroy();
	}
    private void SetupD2xxLibrary () {
    	if(!ftD2xx.setVIDPID(0x0403, 0xada1))
    		feedback.append("\n-SetupD2xxLibrary ()  ftd2xx-java:setVIDPID Error");
    }   	
 
     
} 
