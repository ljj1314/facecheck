package com.att;

import android.util.Log;

public class Mutex  
{  
    private boolean syncLock;  
      
    ////////////////////////////////////////////////  
    //  Constructor  
    ////////////////////////////////////////////////  
  
    public Mutex()  
    {  
        syncLock = false;  
    }  
      
    ////////////////////////////////////////////////  
    //  lock  
    ////////////////////////////////////////////////  
      
    public synchronized void lock()  
    {  
        while(syncLock == true) {  
            try {  
                wait();  
            }  
            catch (Exception e) {  
               Log.i("debug", e.toString());  
            };  
        }  
        syncLock = true;  
    }  
  
    public synchronized void unlock()  
    {  
        syncLock = false;  
        notifyAll();  
    }  
  
}  
