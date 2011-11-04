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

package com.houseofslack.babytracker.reporting;

import java.util.HashMap;

import com.houseofslack.babytracker.BabyTrackerAppWidget;
import com.houseofslack.babytracker.DataProvider;
import com.houseofslack.babytracker.R;
import com.houseofslack.babytracker.DataProvider.EventType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public abstract class ReportBase extends Activity
{
    protected String reportContents = null;
    protected static final String SAVED_REPORT_KEY = "savedreport";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);
        
        eventStrings.put(DataProvider.EventType.FEEDING, getString(R.string.feeding_event_string));
        eventStrings.put(DataProvider.EventType.BM_DIAPER, getString(R.string.bm_diaper_event_string));
        eventStrings.put(DataProvider.EventType.WET_DIAPER, getString(R.string.wet_diaper_event_string));
        eventStrings.put(DataProvider.EventType.SLEEP, getString(R.string.sleeping_event_string));
        eventStrings.put(DataProvider.EventType.CRYING, getString(R.string.crying_event_string));
        eventStrings.put(DataProvider.EventType.CUSTOM, getString(R.string.custom_event_string, 
                PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.custom_name_key), "Custom").toLowerCase()));
        
        if ((null != savedInstanceState) && (null != savedInstanceState.getString(SAVED_REPORT_KEY)))
        {
            reportContents = savedInstanceState.getString(SAVED_REPORT_KEY);
        }
        else
        {
            reportContents = createReport(getEventType());
        }
        ((TextView) findViewById(R.id.report_content)).setText(reportContents);
    }
    
    @Override
    public void onPause()
    {
        BabyTrackerAppWidget.updateWidget(this);
        super.onPause();
    }
    
    protected void refresh()
    {
        reportContents = createReport(getEventType());
        ((TextView) findViewById(R.id.report_content)).setText(reportContents);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // stash the current text
        outState.putString(SAVED_REPORT_KEY, reportContents);
    }
    
    // overrides done by derived classes
    protected abstract DataProvider.EventType getEventType();
    protected abstract String[] getPrefs(int babyIndex);

    protected void clearPrefs(int babyIndex)
    {
        // clear the prefs
        String[] prefs = getPrefs(babyIndex);
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        for (String pref : prefs)
        {
            edit.putLong(pref, 0);
        }
        edit.commit();
    }
    
    // rolls back the last event
    protected void rollback()
    {
        DataProvider.Event lastEvent = DataProvider.getLastEvent(this, getContentResolver(), getEventType(), null);
        if (null != lastEvent)
        {
            DataProvider.deleteEvent(getContentResolver(), getEventType(), lastEvent.getDateMillis(), lastEvent.getBabyIndex());
            clearPrefs(lastEvent.getBabyIndex());
        }
    }

    protected HashMap<DataProvider.EventType, String> eventStrings = new HashMap<DataProvider.EventType, String>(); 
    
    protected String createReport(DataProvider.EventType type)
    {
        Cursor cursor = null;
        StringBuffer sb = new StringBuffer();
        try
        {
            cursor = DataProvider.queryTypeCursor(getContentResolver(), type);
            
            DataProvider.Event firstEventsSleep[] = new DataProvider.Event[2];
            DataProvider.Event firstEventsCrying[] = new DataProvider.Event[2];
            DataProvider.Event firstEventsCustom[] = new DataProvider.Event[2];
            
            if ((null != cursor) && cursor.moveToFirst()) 
            {
                do
                {
                    DataProvider.Event currentEvent = DataProvider.getEvent(this, cursor);
                    if ((currentEvent.getType() == EventType.SLEEP) || (currentEvent.getType() == EventType.CRYING) || (currentEvent.getType() == EventType.CUSTOM)) 
                    {
                        DataProvider.Event firstEvents[] = null;
                        if (currentEvent.getType() == EventType.SLEEP) {
                            firstEvents = firstEventsSleep;
                        } else if (currentEvent.getType() == EventType.CRYING) {
                            firstEvents = firstEventsCrying;
                        } else if (currentEvent.getType() == EventType.CUSTOM) {
                            firstEvents = firstEventsCustom;
                        }
                        if (null == firstEvents[currentEvent.getBabyIndex()])
                        {
                            firstEvents[currentEvent.getBabyIndex()] = DataProvider.getEvent(this, cursor);
                        }
                        else
                        {
                            DataProvider.Event firstEvent = firstEvents[currentEvent.getBabyIndex()];
                            DataProvider.Event secondEvent = currentEvent;
                            sb.append(firstEvent.getBabyName());
                            sb.append(" ");
                            sb.append(eventStrings.get(firstEvent.getType()));
                            sb.append(" ");
                            sb.append(firstEvent.getDateString());
                            sb.append(" to ");
                            sb.append(secondEvent.getDateString());
                            sb.append(" (length: ");
                            sb.append(BabyTrackerAppWidget.computeEventLength(this, firstEvent.getDateMillis(), secondEvent.getDateMillis()));
                            sb.append(")");
                            sb.append("\n");
                            firstEvents[currentEvent.getBabyIndex()] = null;
                        }
                    }
                    else 
                    {
                        sb.append(currentEvent.getBabyName());
                        sb.append(" ");
                        sb.append(eventStrings.get(currentEvent.getType()));
                        sb.append(" at ");
                        sb.append(currentEvent.getDateString());
                        sb.append("\n");
                    }
                } while (cursor.moveToNext());
            }
        }
        finally
        {
            if (null != cursor)
            {
                cursor.close();
            }
        }
        return sb.toString();
    }
    
    protected void createEmail(String content)
    {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        sendIntent.setType("message/rfc822");
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        try 
        {
            startActivity(sendIntent);
        } 
        catch (ActivityNotFoundException e) 
        {
            new AlertDialog.Builder(this).setMessage(getString(R.string.no_email_error)).setNeutralButton(getString(android.R.string.ok), null).show();
        }
    }
    
    public void onEmailClick(View v)
    {
        createEmail(createReport(getEventType()));
    }
    
    public void onEmailAllClick(View v)
    {
        createEmail(createReport(null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_about:
            displayAboutDialog();
            return true;
        case R.id.menu_help:
            showHelp();
            return true;
        case R.id.menu_more:
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=Joshua%20Buergel")));
            return true;
        case R.id.menu_clear_data:
            clearData();
            return true;
        case R.id.menu_remove_last:
            removeLast();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }    
    
    protected void clearData() 
    {
        new AlertDialog.Builder(this)
        .setMessage(R.string.confirm_clear_data)
        .setPositiveButton(android.R.string.yes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                // reset the databases
                DataProvider.deleteAll(getContentResolver(), getEventType());
                clearPrefs(0);
                clearPrefs(1);
                refresh();
            }
            
        })
        .setNegativeButton(android.R.string.no, null)
        .show();
    }
    
    protected void removeLast()
    {
        new AlertDialog.Builder(this)
        .setMessage(R.string.confirm_remove_last)
        .setPositiveButton(android.R.string.yes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                // reset the databases
                rollback();
                refresh();
            }
            
        })
        .setNegativeButton(android.R.string.no, null)
        .show();
    }

    protected void displayAboutDialog()
    {
        new AlertDialog.Builder(this)
          .setMessage(R.string.about_message)
          .setPositiveButton(R.string.ok, null)
          .show();        
    }
    
    public void showHelp()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.help_text))
               .setCancelable(true)
               .setNeutralButton(getString(R.string.help_done_caption), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                   }
               })
               .show();
    }
}
