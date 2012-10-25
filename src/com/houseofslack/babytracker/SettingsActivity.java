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

import com.houseofslack.babytracker.reporting.TabMain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      
      addPreferencesFromResource(R.xml.prefs);
      setContentView(R.layout.settings);
    }
    
    @Override
    public void onPause()
    {
        BabyTrackerAppWidget.updateWidget(this);
        super.onPause();
    }
    
    @Override
    public void onStart()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        if (!prefs.getBoolean(getString(R.string.toast_shown), false))
        {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.toast_shown), true);
            edit.commit();
            Toast.makeText(this, R.string.tip_toast, Toast.LENGTH_LONG).show();
        }
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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
        default:
            return super.onOptionsItemSelected(item);
        }
    }    

    protected void displayAboutDialog()
    {
        new AlertDialog.Builder(this)
          .setMessage(R.string.about_message)
          .setPositiveButton(R.string.ok, null)
          .show();        
    }
    
    public void onResetClick(View v)
    {
        new AlertDialog.Builder(this)
            .setMessage(R.string.confirm_delete)
            .setPositiveButton(android.R.string.yes, new OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1)
                {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    SharedPreferences.Editor edit = prefs.edit();
                    
                    // unfortunately, I can't use Editor.clear here, because I started putting all the baby metrics in
                    // the shared preferences, and I don't really want to move them out at this point and potentially break
                    // upgrades.  It's a relatively minor piece of pain.
                    edit.putLong(UpdateService.LEFT_FEEDING_TIME, 0);
                    edit.putLong(UpdateService.PREVIOUS_LEFT_FEEDING_TIME, 0);
                    edit.putLong(UpdateService.RIGHT_FEEDING_TIME, 0);
                    edit.putLong(UpdateService.PREVIOUS_RIGHT_FEEDING_TIME, 0);
                    edit.putLong(UpdateService.LEFT_WET_DIAPER_TIME, 0);
                    edit.putLong(UpdateService.PREVIOUS_LEFT_WET_DIAPER_TIME, 0);
                    edit.putLong(UpdateService.RIGHT_WET_DIAPER_TIME, 0);
                    edit.putLong(UpdateService.PREVIOUS_RIGHT_WET_DIAPER_TIME, 0);
                    edit.putLong(UpdateService.LEFT_BM_DIAPER_TIME, 0);
                    edit.putLong(UpdateService.PREVIOUS_LEFT_BM_DIAPER_TIME, 0);
                    edit.putLong(UpdateService.RIGHT_BM_DIAPER_TIME, 0);
                    edit.putLong(UpdateService.PREVIOUS_RIGHT_BM_DIAPER_TIME, 0);
                    edit.putLong(UpdateService.LEFT_SLEEP_START, 0);
                    edit.putLong(UpdateService.LEFT_SLEEP_END, 0);
                    edit.putLong(UpdateService.RIGHT_SLEEP_START, 0);
                    edit.putLong(UpdateService.RIGHT_SLEEP_END, 0);
                    edit.putLong(UpdateService.LEFT_CRYING_START, 0);
                    edit.putLong(UpdateService.LEFT_CRYING_END, 0);
                    edit.putLong(UpdateService.RIGHT_CRYING_START, 0);
                    edit.putLong(UpdateService.RIGHT_CRYING_END, 0);
                    edit.putLong(UpdateService.LEFT_CUSTOM_START, 0);
                    edit.putLong(UpdateService.LEFT_CUSTOM_END, 0);
                    edit.putLong(UpdateService.RIGHT_CUSTOM_START, 0);
                    edit.putLong(UpdateService.RIGHT_CUSTOM_END, 0);
                    edit.putLong(UpdateService.LEFT_CUSTOM_TIME, 0);
                    edit.putLong(UpdateService.RIGHT_CUSTOM_TIME, 0);
                    
                    edit.commit();
                    
                    // reset the databases
                    DataProvider.deleteAll(getContentResolver());
                }
                
            })
            .setNegativeButton(android.R.string.no, null)
            .show();
    }
    
    public void onReportClick(View v)
    {
        startActivity(new Intent(this, TabMain.class));
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
