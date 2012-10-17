/*
Copyright 2011 Joshua Buergel

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.houseofslack.babytracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.EditText;

public class EnterQuantity extends Activity {
	
	public static final String EXTRA_SERVICE_INTENT = "extraServiceIntent";
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      
      // if this is a rollback, just throw to the service and exit
      Time now = new Time();
      now.setToNow();
      if (UpdateService.isFeedingRollback(this, getIntent().getStringExtra(EXTRA_SERVICE_INTENT), now)) {
    	  sendIntent(0.0f);
    	  finish();
      }
      else
      {
          setContentView(R.layout.enter_quantity);
      }
    }
    
    private void sendIntent(float quantity)
    {
    	Intent intent = new Intent(getIntent().getStringExtra(EXTRA_SERVICE_INTENT));
    	intent.putExtra(UpdateService.EXTRA_FEEDING_AMOUNT_VALUE, quantity);
    	startService(intent);
    }

    public void onButtonComplete(View v)
    {
    	EditText edit = (EditText) findViewById(R.id.quantity);
    	float quantity = 0.0f;
    	try 
    	{
    		quantity = Float.parseFloat(edit.getText().toString());
		}
    	catch (NumberFormatException ex)
    	{
    	}
    	sendIntent(quantity);
        finish();
    }
}
