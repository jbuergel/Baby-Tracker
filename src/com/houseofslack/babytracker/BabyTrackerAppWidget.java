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

import java.text.DecimalFormat;
import java.util.HashMap;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.widget.RemoteViews;

public abstract class BabyTrackerAppWidget extends AppWidgetProvider
{
    public static final String UPDATE_INTENT = "com.houseofslack.babytracker.UPDATE_WIDGET";
    
    private static final HashMap<String, Integer> LEFT_IDS = new HashMap<String, Integer>();;
    private static final HashMap<String, Integer> RIGHT_IDS = new HashMap<String, Integer>();;
    private static final HashMap<String, String> LEFT_KEYS = new HashMap<String, String>();;
    private static final HashMap<String, String> RIGHT_KEYS = new HashMap<String, String>();;
    private static final HashMap<String, String> LEFT_INTENTS = new HashMap<String, String>();;
    private static final HashMap<String, String> RIGHT_INTENTS = new HashMap<String, String>();;
    
    private static final String FEEDING_KEY = "feeding";
    private static final String WET_DIAPER_KEY = "wet_diaper";
    private static final String BM_DIAPER_KEY = "bm_diaper";
    private static final String SLEEP_KEY = "sleep";
    private static final String SLEEP_START_KEY = "sleep_start";
    private static final String SLEEP_END_KEY = "sleep_end";
    private static final String CRYING_KEY = "crying";
    private static final String CRYING_START_KEY = "crying_start";
    private static final String CRYING_END_KEY = "crying_end";
    private static final String NAME_KEY = "name";
    private static final String CUSTOM_KEY = "custom";
    private static final String CUSTOM_START_KEY = "custom_start";
    private static final String CUSTOM_END_KEY = "custom_end";
    private static final String FEEDING_AMOUNT_KEY = "feedingAmount";
    private static final String CUSTOM_TIME_KEY = "custom_time_key";

    static
    {
        LEFT_IDS.put(FEEDING_KEY, R.id.left_time);
        LEFT_IDS.put(WET_DIAPER_KEY, R.id.left_wet_diaper);
        LEFT_IDS.put(BM_DIAPER_KEY, R.id.left_bm_diaper);
        LEFT_IDS.put(SLEEP_KEY, R.id.left_sleep);
        LEFT_IDS.put(CRYING_KEY, R.id.left_crying);
        LEFT_IDS.put(NAME_KEY, R.id.left_title);
        LEFT_IDS.put(CUSTOM_KEY, R.id.left_custom);
        RIGHT_IDS.put(FEEDING_KEY, R.id.right_time);
        RIGHT_IDS.put(WET_DIAPER_KEY, R.id.right_wet_diaper);
        RIGHT_IDS.put(BM_DIAPER_KEY, R.id.right_bm_diaper);
        RIGHT_IDS.put(SLEEP_KEY, R.id.right_sleep);
        RIGHT_IDS.put(CRYING_KEY, R.id.right_crying);
        RIGHT_IDS.put(NAME_KEY, R.id.right_title);
        RIGHT_IDS.put(CUSTOM_KEY, R.id.right_custom);
        LEFT_KEYS.put(FEEDING_KEY, UpdateService.LEFT_FEEDING_TIME);
        LEFT_KEYS.put(WET_DIAPER_KEY, UpdateService.LEFT_WET_DIAPER_TIME);
        LEFT_KEYS.put(BM_DIAPER_KEY, UpdateService.LEFT_BM_DIAPER_TIME);
        LEFT_KEYS.put(SLEEP_START_KEY, UpdateService.LEFT_SLEEP_START);
        LEFT_KEYS.put(SLEEP_END_KEY, UpdateService.LEFT_SLEEP_END);
        LEFT_KEYS.put(CRYING_START_KEY, UpdateService.LEFT_CRYING_START);
        LEFT_KEYS.put(CRYING_END_KEY, UpdateService.LEFT_CRYING_END);
        LEFT_KEYS.put(CUSTOM_START_KEY, UpdateService.LEFT_CUSTOM_START);
        LEFT_KEYS.put(CUSTOM_END_KEY, UpdateService.LEFT_CUSTOM_END);
        LEFT_KEYS.put(FEEDING_AMOUNT_KEY, UpdateService.LEFT_FEEDING_AMOUNT);
        LEFT_KEYS.put(CUSTOM_TIME_KEY, UpdateService.LEFT_CUSTOM_TIME);
        RIGHT_KEYS.put(FEEDING_KEY, UpdateService.RIGHT_FEEDING_TIME);
        RIGHT_KEYS.put(WET_DIAPER_KEY, UpdateService.RIGHT_WET_DIAPER_TIME);
        RIGHT_KEYS.put(BM_DIAPER_KEY, UpdateService.RIGHT_BM_DIAPER_TIME);
        RIGHT_KEYS.put(SLEEP_START_KEY, UpdateService.RIGHT_SLEEP_START);
        RIGHT_KEYS.put(SLEEP_END_KEY, UpdateService.RIGHT_SLEEP_END);
        RIGHT_KEYS.put(CRYING_START_KEY, UpdateService.RIGHT_CRYING_START);
        RIGHT_KEYS.put(CRYING_END_KEY, UpdateService.RIGHT_CRYING_END);
        RIGHT_KEYS.put(CUSTOM_START_KEY, UpdateService.RIGHT_CUSTOM_START);
        RIGHT_KEYS.put(CUSTOM_END_KEY, UpdateService.RIGHT_CUSTOM_END);
        RIGHT_KEYS.put(FEEDING_AMOUNT_KEY, UpdateService.RIGHT_FEEDING_AMOUNT);
        RIGHT_KEYS.put(CUSTOM_TIME_KEY, UpdateService.RIGHT_CUSTOM_TIME);
        LEFT_INTENTS.put(FEEDING_KEY, UpdateService.UPDATE_LEFT_FEEDING);
        LEFT_INTENTS.put(WET_DIAPER_KEY, UpdateService.UPDATE_LEFT_WET_DIAPER);
        LEFT_INTENTS.put(BM_DIAPER_KEY, UpdateService.UPDATE_LEFT_BM_DIAPER);
        LEFT_INTENTS.put(SLEEP_KEY, UpdateService.UPDATE_LEFT_SLEEP);
        LEFT_INTENTS.put(CRYING_KEY, UpdateService.UPDATE_LEFT_CRYING);
        LEFT_INTENTS.put(CUSTOM_KEY, UpdateService.UPDATE_LEFT_CUSTOM);
        LEFT_INTENTS.put(CUSTOM_TIME_KEY, UpdateService.UPDATE_LEFT_CUSTOM_TIME);
        RIGHT_INTENTS.put(FEEDING_KEY, UpdateService.UPDATE_RIGHT_FEEDING);
        RIGHT_INTENTS.put(WET_DIAPER_KEY, UpdateService.UPDATE_RIGHT_WET_DIAPER);
        RIGHT_INTENTS.put(BM_DIAPER_KEY, UpdateService.UPDATE_RIGHT_BM_DIAPER);
        RIGHT_INTENTS.put(SLEEP_KEY, UpdateService.UPDATE_RIGHT_SLEEP);
        RIGHT_INTENTS.put(CRYING_KEY, UpdateService.UPDATE_RIGHT_CRYING);
        RIGHT_INTENTS.put(CUSTOM_KEY, UpdateService.UPDATE_RIGHT_CUSTOM);
        RIGHT_INTENTS.put(CUSTOM_TIME_KEY, UpdateService.UPDATE_RIGHT_CUSTOM_TIME);
    }
    
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(UPDATE_INTENT)) 
        {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            onUpdate(context, mgr, mgr.getAppWidgetIds(new ComponentName(context, this.getClass())));
        }
        else
        {
            super.onReceive(context, intent);
        }
    }
    
    public abstract int getBothConfiguredId();
    public abstract int getOneConfiguredId();
    public abstract boolean hasCrying();
    public abstract boolean hasExtras();
    
    public static void updateWidget(Context context)
    {
        // update the widget whenever we go away
        Intent updateIntent = new Intent();
        updateIntent.setAction(BabyTrackerAppWidget.UPDATE_INTENT);
        context.sendBroadcast(updateIntent);
    }
    
    private void updateArea(Context context, RemoteViews views, int areaId, String key, String headerString, String unconfiguredHelpString, boolean showAmount, String amountKey)
    {
        // get the time
        long longTime = PreferenceManager.getDefaultSharedPreferences(context).getLong(key, 0);
        if (0 == longTime)
        {
            views.setTextViewText(areaId, unconfiguredHelpString);
        }
        else
        {
            StringBuffer sb = new StringBuffer(headerString);
            Time time = new Time();
            time.set(longTime);
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.twenty_four_hour_key), true)) {
                sb.append(time.format("%R"));
            } else {
                sb.append(time.format("%I:%M %p"));
            }
            
            if (showAmount) {
                float amount = PreferenceManager.getDefaultSharedPreferences(context).getFloat(amountKey, 0);
                sb.append(" ");
            	sb.append(context.getString(R.string.display_amount, new DecimalFormat("#.##").format(amount)));
            }

            views.setTextViewText(areaId, sb.toString());
        }
    }
    
    public static String computeEventLength(Context context, long startTime, long endTime)
    {
        long millis = endTime - startTime;
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        return context.getResources().getString(R.string.duration_string, hours, minutes);
    }
    
    private void updateDurationEvent(Context context, RemoteViews views, int areaId, String startKey, String endKey, int noId, int goingId, int endedId, String customString)
    {
        // get the times
        long startTime = PreferenceManager.getDefaultSharedPreferences(context).getLong(startKey, 0);
        long endTime = PreferenceManager.getDefaultSharedPreferences(context).getLong(endKey, 0);
        if (0 == startTime)
        {
            if (null != customString) 
            {
                views.setTextViewText(areaId, context.getResources().getString(noId, customString));
            } 
            else 
            {
                views.setTextViewText(areaId, context.getResources().getString(noId));
            }
        }
        else if (0 == endTime)
        {
            Time time = new Time();
            time.set(startTime);
            String timeFormat = "%R";
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.twenty_four_hour_key), true)) {
                timeFormat = "%I:%M %p";
            }
            
            if (null != customString) 
            {
                // upper-case the first character
                String upperCustomString = customString.substring(0, 1).toUpperCase() + customString.substring(1);
                views.setTextViewText(areaId, context.getResources().getString(goingId, upperCustomString, time.format(timeFormat)));
            }
            else
            {
                views.setTextViewText(areaId, context.getResources().getString(goingId, time.format(timeFormat)));
            }
        }
        else 
        {
            if (null != customString) 
            {
                views.setTextViewText(areaId, context.getResources().getString(endedId, customString, computeEventLength(context, startTime, endTime)));
            }
            else
            {
                views.setTextViewText(areaId, context.getResources().getString(endedId, computeEventLength(context, startTime, endTime)));
            }
        }
    }
    
    private void configureBaby(Context context, RemoteViews views, HashMap<String, Integer> ids, String nameKey, HashMap<String, String> keys, HashMap<String, String> intents)
    {
        // set the name
        String name = PreferenceManager.getDefaultSharedPreferences(context).getString(nameKey, "");
        views.setTextViewText(ids.get(NAME_KEY), name);
        
        boolean useFeedingAmounts = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.enter_feeding_quantities_key), false);
        boolean isCustomDuration = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.custom_duration_key), true);
        String customString = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.custom_name_key), "Custom").toLowerCase();
        
        // update the displays for items
        updateArea(context, views, ids.get(WET_DIAPER_KEY), keys.get(WET_DIAPER_KEY), context.getResources().getString(R.string.last_wet_diaper_header), context.getResources().getString(R.string.unconfigured_wet_diaper), false, null);
        updateArea(context, views, ids.get(BM_DIAPER_KEY), keys.get(BM_DIAPER_KEY), context.getResources().getString(R.string.last_bm_diaper_header), context.getResources().getString(R.string.unconfigured_bm_diaper), false, null);
        if (hasExtras())
        {
            updateArea(context, views, ids.get(FEEDING_KEY), keys.get(FEEDING_KEY), context.getResources().getString(R.string.last_feeding_header), context.getResources().getString(R.string.unconfigured_feeding), useFeedingAmounts, keys.get(FEEDING_AMOUNT_KEY));
            updateDurationEvent(context, views, ids.get(SLEEP_KEY), keys.get(SLEEP_START_KEY), keys.get(SLEEP_END_KEY), R.string.no_sleep, R.string.sleep_going, R.string.sleep_ended, null);
            if (isCustomDuration) 
            {
            	updateDurationEvent(context, views, ids.get(CUSTOM_KEY), keys.get(CUSTOM_START_KEY), keys.get(CUSTOM_END_KEY), R.string.no_custom, R.string.custom_going, R.string.custom_ended, customString);
            }
            else
            {
                updateArea(context, views, ids.get(CUSTOM_KEY), keys.get(CUSTOM_TIME_KEY), context.getResources().getString(R.string.last_custom_time_header, customString), context.getResources().getString(R.string.unconfigured_custom_time, customString), false, null);
            }
        }
        if (hasCrying()) 
        {
            updateDurationEvent(context, views, ids.get(CRYING_KEY), keys.get(CRYING_START_KEY), keys.get(CRYING_END_KEY), R.string.no_crying, R.string.crying_going, R.string.crying_ended, null);
        }
        
        // set pending intents for our areas
        views.setOnClickPendingIntent(ids.get(WET_DIAPER_KEY), PendingIntent.getService(context, 0,
                new Intent(intents.get(WET_DIAPER_KEY)), 0));
        views.setOnClickPendingIntent(ids.get(BM_DIAPER_KEY), PendingIntent.getService(context, 0,
                new Intent(intents.get(BM_DIAPER_KEY)), 0));
        if (hasExtras())
        {
        	if (useFeedingAmounts)
        	{
        		Intent intent = new Intent(context, EnterQuantity.class);
        		intent.putExtra(EnterQuantity.EXTRA_SERVICE_INTENT, intents.get(FEEDING_KEY));
        		// note that we have to make these intents look different, since it doesn't distinguish based on extras
                views.setOnClickPendingIntent(ids.get(FEEDING_KEY), PendingIntent.getActivity(context, ids.get(FEEDING_KEY), intent, 0));
        	}
        	else
        	{
                views.setOnClickPendingIntent(ids.get(FEEDING_KEY), PendingIntent.getService(context, 0,
                        new Intent(intents.get(FEEDING_KEY)), 0));
        	}
            views.setOnClickPendingIntent(ids.get(SLEEP_KEY), PendingIntent.getService(context, 0,
                    new Intent(intents.get(SLEEP_KEY)), 0));
            if (isCustomDuration)
            {
                views.setOnClickPendingIntent(ids.get(CUSTOM_KEY), PendingIntent.getService(context, 0,
                        new Intent(intents.get(CUSTOM_KEY)), 0));
            }
            else
            {
                views.setOnClickPendingIntent(ids.get(CUSTOM_KEY), PendingIntent.getService(context, 0,
                        new Intent(intents.get(CUSTOM_TIME_KEY)), 0));
            }
        }
        if (hasCrying()) 
        {
            views.setOnClickPendingIntent(ids.get(CRYING_KEY), PendingIntent.getService(context, 0,
                    new Intent(intents.get(CRYING_KEY)), 0));
        }
    }
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Construct the RemoteViews object.  It takes the package name (in our case, it's our
        // package, but it needs this because on the other side it's the widget host inflating
        // the layout from our package).
        for (int appWidgetId : appWidgetIds)
        {
            RemoteViews views;
            AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
            // check if we have both babies configured or not
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            
            boolean baby0Enabled = prefs.getBoolean(context.getString(R.string.enable_baby_key_1), false);
            boolean baby1Enabled = prefs.getBoolean(context.getString(R.string.enable_baby_key_2), false);
            
            if ((appWidgetInfo == null) || (!baby0Enabled && !baby1Enabled)) 
            {
                views = new RemoteViews(context.getPackageName(), R.layout.unconfigured_widget);
                views.setOnClickPendingIntent(R.id.widget_container, PendingIntent.getActivity(context, 0,
                        new Intent(context, SettingsActivity.class), 0));
            } 
            else 
            {
                // we got here, so at least one baby is tracked
                if (!baby0Enabled || !baby1Enabled)
                {
                    // use the one baby view
                    views = new RemoteViews(context.getPackageName(), getOneConfiguredId());
                }
                else
                {
                    // use the two baby view
                    views = new RemoteViews(context.getPackageName(), getBothConfiguredId());
                }
                
                // update the display for the first configured baby
                if (baby0Enabled)
                {
                    configureBaby(context, views, LEFT_IDS, context.getString(R.string.baby_name_key_1), LEFT_KEYS, LEFT_INTENTS); 
                }
                
                // update the other baby
                if (baby1Enabled)
                {
                    // if the other baby is enabled, use the right-hand slots
                    if (baby0Enabled)
                    {
                        configureBaby(context, views, RIGHT_IDS, context.getString(R.string.baby_name_key_2), RIGHT_KEYS, RIGHT_INTENTS); 
                    }
                    else
                    {
                        configureBaby(context, views, LEFT_IDS, context.getString(R.string.baby_name_key_2), LEFT_KEYS, LEFT_INTENTS); 
                    }
                }
                
                // add in the intent to go to the settings application
                views.setOnClickPendingIntent(R.id.widget_title, PendingIntent.getActivity(context, 0,
                        new Intent(context, SettingsActivity.class), 0));
                
            }
            
            // Tell the widget manager
            appWidgetManager.updateAppWidget(appWidgetId, views);
            
        }
    }
}
