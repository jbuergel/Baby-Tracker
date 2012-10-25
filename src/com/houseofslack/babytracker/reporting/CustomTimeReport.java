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
import com.houseofslack.babytracker.UpdateService;

public class CustomTimeReport extends DurationReport
{
    @Override
    protected DataProvider.EventType getEventType()
    {
        return DataProvider.EventType.CUSTOM_TIME;
    }
    
    @Override
    protected String[] getPrefs(int babyIndex)
    {
        return babyIndex == 0 ? 
            new String[] {UpdateService.LEFT_CUSTOM_TIME} :
            new String[] {UpdateService.RIGHT_CUSTOM_TIME};
    }
}
