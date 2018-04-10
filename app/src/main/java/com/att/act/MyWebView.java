package com.att.act;

import com.att.WebAcitivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class MyWebView extends WebView{

	public MyWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyWebView(Context context, AttributeSet attributeSet) {  
	    super(context, attributeSet);  
	    // TODO Auto-generated constructor stub  
	}  
	
	
	@Override  
	public boolean onTouchEvent(MotionEvent evt) {  
	    
	   switch (evt.getAction()){  
	     
	   case MotionEvent.ACTION_DOWN:  
	    //do something......   
		   WebAcitivity.backhan.postDelayed(WebAcitivity.rn, 1000*10);
		   requestDisallowInterceptTouchEvent(true);
		   
	    break;  
	      
	   case MotionEvent.ACTION_MOVE:  
		   requestDisallowInterceptTouchEvent(true);
	    //do something......   
	    break;          case MotionEvent.ACTION_UP:       
	    //do something......   
	     break;                 
	    }                   
	    return false;}  


	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
