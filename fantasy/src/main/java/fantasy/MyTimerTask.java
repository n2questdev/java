package fantasy;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MyTimerTask extends TimerTask {
    private final static long ONCE_PER_DAY = 1000*60*60*24;

    //private final static int ONE_DAY = 1;
    private final static int THREE_PM = 15;
    private final static int ZERO_MINUTES = 0;


    @Override
    public void run() {
        long currennTime = System.currentTimeMillis();
        long stopTime = currennTime + 2000;//provide the 2hrs time it should execute 1000*60*60*2
        	try {
        		
        		BidManager.main(new String[] {});;// Do your Job Here
        		
        	}
        	catch(IOException e)
        	{
        		 System.out.println("Job Failed******************* "+e.getMessage());
        	}
        	
        	  
            System.out.println("Start Job"+stopTime);
            System.out.println("End Job"+System.currentTimeMillis());
          }    
    private static Date getTomorrow3PM(){

        Date date2am = new java.util.Date(); 
          date2am.setHours(THREE_PM); 
            date2am.setMinutes(ZERO_MINUTES); 

           return date2am;
      }
    //call this method from your servlet init method
    public static void startTask(){
        MyTimerTask task = new MyTimerTask();
        Timer timer = new Timer();  
        timer.schedule(task,getTomorrow3PM(),1000*60*60);// for your case u need to give 1000*60*60*24
    }
    public static void main(String args[]){
        startTask();
        
    }

}