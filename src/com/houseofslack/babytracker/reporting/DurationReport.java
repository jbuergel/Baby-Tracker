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

import com.houseofslack.babytracker.DataProvider;

public abstract class DurationReport extends ReportBase
{
    
    // rolls back the last event
    @Override
    protected void rollback()
    {
        // figure out if we're deleting one event or two
        DataProvider.Event lastEvent = DataProvider.getLastEvent(this, getContentResolver(), getEventType(), null);
        if (null != lastEvent)
        {
            // delete the event
            DataProvider.deleteEvent(getContentResolver(), getEventType(), lastEvent.getDateMillis(), lastEvent.getBabyIndex());
            // fetch the last event again
            lastEvent = DataProvider.getLastEvent(this, getContentResolver(), getEventType(), new Integer(lastEvent.getBabyIndex()));
            if (null != lastEvent)
            {
                DataProvider.deleteEvent(getContentResolver(), getEventType(), lastEvent.getDateMillis(), lastEvent.getBabyIndex());
            }
            clearPrefs(lastEvent.getBabyIndex());
        }
    }

}
