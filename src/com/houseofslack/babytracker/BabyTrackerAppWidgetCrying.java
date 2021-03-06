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

public class BabyTrackerAppWidgetCrying extends BabyTrackerAppWidget
{
	@Override
    public int getBothConfiguredId()
    {
        return R.layout.both_configured_crying;
    }
    
	@Override
    public int getOneConfiguredId()
    {
        return R.layout.one_configured_crying;
    }
    
	@Override
    public boolean hasCrying()
    {
        return true;
    }
    
	@Override
    public boolean hasExtras()
    {
        return true;
    }
}
