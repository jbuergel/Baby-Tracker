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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;

public class UpdateService extends IntentService
{
    public static final String BABY_DATA_PREFERENCES = "baby_prefs";
    
    public static final String UPDATE_LEFT_FEEDING = "com.houseofslack.babytracker.UPDATE_LEFT_FEEDING";
    public static final String UPDATE_RIGHT_FEEDING = "com.houseofslack.babytracker.UPDATE_RIGHT_FEEDING";
    public static final String UPDATE_LEFT_WET_DIAPER = "com.houseofslack.babytracker.UPDATE_LEFT_WET_DIAPER";
    public static final String UPDATE_RIGHT_WET_DIAPER = "com.houseofslack.babytracker.UPDATE_RIGHT_WET_DIAPER";
    public static final String UPDATE_LEFT_BM_DIAPER = "com.houseofslack.babytracker.UPDATE_LEFT_BM_DIAPER";
    public static final String UPDATE_RIGHT_BM_DIAPER = "com.houseofslack.babytracker.UPDATE_RIGHT_BM_DIAPER";
    public static final String UPDATE_LEFT_SLEEP = "com.houseofslack.babytracker.UPDATE_LEFT_SLEEP";
    public static final String UPDATE_RIGHT_SLEEP = "com.houseofslack.babytracker.UPDATE_RIGHT_SLEEP";
    public static final String UPDATE_LEFT_CRYING = "com.houseofslack.babytracker.UPDATE_LEFT_CRYING";
    public static final String UPDATE_RIGHT_CRYING = "com.houseofslack.babytracker.UPDATE_RIGHT_CRYING";
    public static final String UPDATE_LEFT_CUSTOM = "com.houseofslack.babytracker.UPDATE_LEFT_CUSTOM";
    public static final String UPDATE_RIGHT_CUSTOM = "com.houseofslack.babytracker.UPDATE_RIGHT_CUSTOM";
    public static final String UPDATE_LEFT_CUSTOM_TIME = "com.houseofslack.babytracker.UPDATE_LEFT_CUSTOM_TIME";
    public static final String UPDATE_RIGHT_CUSTOM_TIME = "com.houseofslack.babytracker.UPDATE_RIGHT_CUSTOM_TIME";

    public static final String LEFT_FEEDING_TIME = "left_feeding_time"; 
    public static final String PREVIOUS_LEFT_FEEDING_TIME = "previous_left_feeding_time"; 
    public static final String RIGHT_FEEDING_TIME = "right_feeding_time"; 
    public static final String PREVIOUS_RIGHT_FEEDING_TIME = "previous_right_feeding_time"; 
    public static final String LEFT_WET_DIAPER_TIME = "left_wet_diaper_time"; 
    public static final String PREVIOUS_LEFT_WET_DIAPER_TIME = "previous_left_wet_diaper_time"; 
    public static final String RIGHT_WET_DIAPER_TIME = "right_wet_diaper_time"; 
    public static final String PREVIOUS_RIGHT_WET_DIAPER_TIME = "previous_right_wet_diaper_time";
    public static final String LEFT_BM_DIAPER_TIME = "left_bm_diaper_time"; 
    public static final String PREVIOUS_LEFT_BM_DIAPER_TIME = "previous_left_bm_diaper_time"; 
    public static final String RIGHT_BM_DIAPER_TIME = "right_bm_diaper_time"; 
    public static final String PREVIOUS_RIGHT_BM_DIAPER_TIME = "previous_right_bm_diaper_time"; 
    public static final String LEFT_SLEEP_START = "left_sleep_start";
    public static final String LEFT_SLEEP_END = "left_sleep_end";
    public static final String RIGHT_SLEEP_START = "right_sleep_start";
    public static final String RIGHT_SLEEP_END = "right_sleep_end";
    public static final String LEFT_CRYING_START = "left_crying_start";
    public static final String LEFT_CRYING_END = "left_crying_end";
    public static final String RIGHT_CRYING_START = "right_crying_start";
    public static final String RIGHT_CRYING_END = "right_crying_end";
    public static final String LEFT_CUSTOM_START = "left_custom_start";
    public static final String LEFT_CUSTOM_END = "left_custom_end";
    public static final String RIGHT_CUSTOM_START = "right_custom_start";
    public static final String RIGHT_CUSTOM_END = "right_custom_end";
    public static final String LEFT_FEEDING_AMOUNT = "left_feeding_amount";
    public static final String PREVIOUS_LEFT_FEEDING_AMOUNT = "previous_left_feeding_amount";
    public static final String RIGHT_FEEDING_AMOUNT = "right_feeding_amount";
    public static final String PREVIOUS_RIGHT_FEEDING_AMOUNT = "previous_right_feeding_amount";
    public static final String LEFT_CUSTOM_TIME = "left_custom_time"; 
    public static final String PREVIOUS_LEFT_CUSTOM_TIME = "previous_left_custom_time"; 
    public static final String RIGHT_CUSTOM_TIME = "right_custom_time"; 
    public static final String PREVIOUS_RIGHT_CUSTOM_TIME = "previous_right_custom_time"; 
    
    public static final String EXTRA_FEEDING_AMOUNT_VALUE = "extra_feeding_amount_value";
    
    private static final long ROLLBACK_INTERVAL = 1000 * 30;
    
    private static final String SERVICE_NAME = "BabyTrackerUpdateService";
    
    public UpdateService()
    {
        super(SERVICE_NAME);
    }
    
    private static boolean checkForRollback(long currentTime, long previousTime, Time now)
    {
        if (((now.toMillis(false) - currentTime) < ROLLBACK_INTERVAL) && (0 != previousTime))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private float getFloatFromPrefs(SharedPreferences prefs, String key) {
        float value = 0.0f;
        if (null != key) 
        {
            try 
            {
            	value = prefs.getFloat(key, 0.0f);
            }
            catch (Exception ex)
            {
            	// we don't care here
            }
        }
        return value;
    }
    
    public static boolean isFeedingRollback(Context context, String intent, Time now) 
    {
    	String currentTimeKey = null;
    	String previousTimeKey = null;
    	
        if (UPDATE_LEFT_FEEDING.equals(intent))
        {
        	currentTimeKey = LEFT_FEEDING_TIME;
        	previousTimeKey = PREVIOUS_LEFT_FEEDING_TIME;
        }
        else if (UPDATE_RIGHT_FEEDING.equals(intent))
        {
        	currentTimeKey = RIGHT_FEEDING_TIME;
        	previousTimeKey = PREVIOUS_RIGHT_FEEDING_TIME;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long currentTime = prefs.getLong(currentTimeKey, 0);
        long previousTime = prefs.getLong(previousTimeKey, 0);
        return checkForRollback(currentTime, previousTime, now);
    }
    
    private void updateTime(DataProvider.EventType type, SharedPreferences prefs, SharedPreferences.Editor edit, Time now, String currentTimeKey, 
    		String previousTimeKey, String currentQuantityKey, String previousQuantityKey, int babyNumber, float newQuantity)
    {
        long currentTime = prefs.getLong(currentTimeKey, 0);
        long previousTime = prefs.getLong(previousTimeKey, 0);
        float currentQuantity = getFloatFromPrefs(prefs, currentQuantityKey);
        float previousQuantity = getFloatFromPrefs(prefs, previousQuantityKey);
        
        // check if this is a rollback
        if (checkForRollback(currentTime, previousTime, now))
        {
            // remove the old entry
            DataProvider.deleteEvent(getContentResolver(), type, currentTime, babyNumber);
            edit.putLong(currentTimeKey, previousTime);
            edit.putLong(previousTimeKey, 0);
            if (null != currentQuantityKey)
            {
                edit.putFloat(currentQuantityKey, previousQuantity);
                edit.putFloat(previousQuantityKey, 0.0f);
            }
        }
        else
        {
            // add a time entry
            DataProvider.putEvent(getContentResolver(), type, now.toMillis(false), babyNumber, newQuantity);
            edit.putLong(currentTimeKey, now.toMillis(false));
            edit.putLong(previousTimeKey, currentTime);
            if (null != currentQuantityKey)
            {
                edit.putFloat(currentQuantityKey, newQuantity);
                edit.putFloat(previousQuantityKey, currentQuantity);
            }
        }
    }
    
    private void updateDurationEvent(SharedPreferences prefs, SharedPreferences.Editor edit, Time now, String startTimeKey, String endTimeKey, int babyNumber, DataProvider.EventType eventType)
    {
        long startTime = prefs.getLong(startTimeKey, 0);
        long endTime = prefs.getLong(endTimeKey, 0);
        
        // check if this is a rollback of either time - use the start time for both, since there's no "previous time" for the duration things
        // by passing in the start time as the previous, though, it will not mark a rollback if it's set as zero
        if (checkForRollback(startTime, startTime, now))
        {
            // start time rollback - zero out the start time
            edit.putLong(startTimeKey, 0);
        }
        else if (checkForRollback(endTime, endTime, now))
        {
            // end time rollback - zero out the end time
            edit.putLong(endTimeKey, 0);
            // clear out the last two duration events, because a nap is recorded as two consecutive events
            DataProvider.deleteEvent(getContentResolver(), eventType, startTime, babyNumber);
            DataProvider.deleteEvent(getContentResolver(), eventType, endTime, babyNumber);
        }
        else if ((endTime == 0) && (startTime != 0)) 
        {
            // update the end time
            edit.putLong(endTimeKey, now.toMillis(false));
            // add two time entries, for the start and end of the nap
            DataProvider.putEvent(getContentResolver(), eventType, startTime, babyNumber, 0.0f);
            DataProvider.putEvent(getContentResolver(), eventType, now.toMillis(false), babyNumber, 0.0f);
        }
        else
        {
            // update the start time, only - this is the start of a new nap
            edit.putLong(startTimeKey, now.toMillis(false));
            // zero the end time
            edit.putLong(endTimeKey, 0);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Time now = new Time();
        now.setToNow();
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefs.edit();
        
        boolean baby0Enabled = prefs.getBoolean(getString(R.string.enable_baby_key_1), false);
        int left = baby0Enabled ? 0 : 1;
        int right = 1;
        float quantity = intent.getFloatExtra(EXTRA_FEEDING_AMOUNT_VALUE, 0.0f);
        
        // see what the intent we're handling is
        if (UPDATE_LEFT_FEEDING.equals(intent.getAction()))
        {
            updateTime(DataProvider.EventType.FEEDING, prefs, edit, now, LEFT_FEEDING_TIME, PREVIOUS_LEFT_FEEDING_TIME, LEFT_FEEDING_AMOUNT, PREVIOUS_LEFT_FEEDING_AMOUNT, left, quantity);
        }
        else if (UPDATE_RIGHT_FEEDING.equals(intent.getAction()))
        {
            updateTime(DataProvider.EventType.FEEDING, prefs, edit, now, RIGHT_FEEDING_TIME, PREVIOUS_RIGHT_FEEDING_TIME, RIGHT_FEEDING_AMOUNT, PREVIOUS_RIGHT_FEEDING_AMOUNT, right, quantity);
        }
        else if (UPDATE_LEFT_WET_DIAPER.equals(intent.getAction()))
        {
            updateTime(DataProvider.EventType.WET_DIAPER, prefs, edit, now, LEFT_WET_DIAPER_TIME, PREVIOUS_LEFT_WET_DIAPER_TIME, null, null, left, 0.0f);
        }
        else if (UPDATE_RIGHT_WET_DIAPER.equals(intent.getAction()))
        {
            updateTime(DataProvider.EventType.WET_DIAPER, prefs, edit, now, RIGHT_WET_DIAPER_TIME, PREVIOUS_RIGHT_WET_DIAPER_TIME, null, null, right, 0.0f);
        }
        else if (UPDATE_LEFT_BM_DIAPER.equals(intent.getAction()))
        {
            updateTime(DataProvider.EventType.BM_DIAPER, prefs, edit, now, LEFT_BM_DIAPER_TIME, PREVIOUS_LEFT_BM_DIAPER_TIME, null, null, left, 0.0f);
        }
        else if (UPDATE_RIGHT_BM_DIAPER.equals(intent.getAction()))
        {
            updateTime(DataProvider.EventType.BM_DIAPER, prefs, edit, now, RIGHT_BM_DIAPER_TIME, PREVIOUS_RIGHT_BM_DIAPER_TIME, null, null, right, 0.0f);
        }
        else if (UPDATE_LEFT_SLEEP.equals(intent.getAction()))
        {
            updateDurationEvent(prefs, edit, now, LEFT_SLEEP_START, LEFT_SLEEP_END, left, DataProvider.EventType.SLEEP);
        }
        else if (UPDATE_RIGHT_SLEEP.equals(intent.getAction()))
        {
            updateDurationEvent(prefs, edit, now, RIGHT_SLEEP_START, RIGHT_SLEEP_END, right, DataProvider.EventType.SLEEP);
        }
        else if (UPDATE_LEFT_CRYING.equals(intent.getAction()))
        {
            updateDurationEvent(prefs, edit, now, LEFT_CRYING_START, LEFT_CRYING_END, left, DataProvider.EventType.CRYING);
        }
        else if (UPDATE_RIGHT_CRYING.equals(intent.getAction()))
        {
            updateDurationEvent(prefs, edit, now, RIGHT_CRYING_START, RIGHT_CRYING_END, right, DataProvider.EventType.CRYING);
        }
        else if (UPDATE_LEFT_CUSTOM.equals(intent.getAction()))
        {
            updateDurationEvent(prefs, edit, now, LEFT_CUSTOM_START, LEFT_CUSTOM_END, left, DataProvider.EventType.CUSTOM);
        }
        else if (UPDATE_RIGHT_CUSTOM.equals(intent.getAction()))
        {
            updateDurationEvent(prefs, edit, now, RIGHT_CUSTOM_START, RIGHT_CUSTOM_END, right, DataProvider.EventType.CUSTOM);
        }
        else if (UPDATE_LEFT_CUSTOM_TIME.equals(intent.getAction()))
        {
            updateTime(DataProvider.EventType.CUSTOM_TIME, prefs, edit, now, LEFT_CUSTOM_TIME, PREVIOUS_LEFT_CUSTOM_TIME, null, null, left, 0.0f);
        }
        else if (UPDATE_RIGHT_CUSTOM_TIME.equals(intent.getAction()))
        {
            updateTime(DataProvider.EventType.CUSTOM_TIME, prefs, edit, now, RIGHT_CUSTOM_TIME, PREVIOUS_RIGHT_CUSTOM_TIME, null, null, right, 0.0f);
        }
        edit.commit();
        BabyTrackerAppWidget.updateWidget(this);
    }
}
