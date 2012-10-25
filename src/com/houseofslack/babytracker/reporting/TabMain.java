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

import com.houseofslack.babytracker.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class TabMain extends TabActivity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_main);

        TabHost tabHost = getTabHost();  // The activity TabHost
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, FeedingReport.class);
        // Initialize a TabSpec for each tab and add it to the TabHost
        tabHost.addTab(tabHost.newTabSpec("feeding").setIndicator(getString(R.string.tab_name_feeding)).setContent(intent));

        intent = new Intent().setClass(this, WetDiaperReport.class);
        tabHost.addTab(tabHost.newTabSpec("wet_diaper").setIndicator(getString(R.string.tab_name_wet_diaper)).setContent(intent));

        intent = new Intent().setClass(this, BMDiaperReport.class);
        tabHost.addTab(tabHost.newTabSpec("bm_diaper").setIndicator(getString(R.string.tab_name_bm_diaper)).setContent(intent));

        intent = new Intent().setClass(this, SleepReport.class);
        tabHost.addTab(tabHost.newTabSpec("sleeping").setIndicator(getString(R.string.tab_name_sleep)).setContent(intent));

        intent = new Intent().setClass(this, CryingReport.class);
        tabHost.addTab(tabHost.newTabSpec("crying").setIndicator(getString(R.string.tab_name_crying)).setContent(intent));

        intent = new Intent().setClass(this, CustomReport.class);
        tabHost.addTab(tabHost.newTabSpec("custom").setIndicator(getString(R.string.tab_name_custom)).setContent(intent));

        intent = new Intent().setClass(this, CustomTimeReport.class);
        tabHost.addTab(tabHost.newTabSpec("custom_time").setIndicator(getString(R.string.tab_name_custom_time)).setContent(intent));
        
        tabHost.setCurrentTab(0);    
    }
}
