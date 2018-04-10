package fantasy;

import java.io.IOException;
import java.util.Calendar;
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
        		
        BidManager.main(new String[] {"1"});;// clear sheets
        		 System.out.println("Running******************* ");
       	}
        	catch(Exception e)
        	{
        		 System.out.println("Job Failed******************* "+e.getMessage());
        	}
        	
        	  
            System.out.println("Start Job"+stopTime);
            System.out.println("End Job"+System.currentTimeMillis());
          }    
    private static Date get3PM(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
         return calendar.getTime();
      }
    //call this method from your servlet init method
    public static void startTask(){
        MyTimerTask task = new MyTimerTask();
        Timer timer = new Timer();  
        timer.schedule(task,get3PM(),1000*60*60*24);// for your case u need to give 1000*60*60*24
    }
    public static void main(String args[]){
        startTask();
        
    }

}