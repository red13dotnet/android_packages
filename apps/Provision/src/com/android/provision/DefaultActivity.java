/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.provision;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.Context;
import android.os.SystemProperties;
import java.util.List;
import android.content.pm.PackageInfo;
/**
 * Application that sets the provisioned bit, like SetupWizard does.
 */
public class DefaultActivity extends Activity {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Add a persistent setting to allow other apps to know the device has been provisioned.
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
        Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);

        // remove this activity from the package manager.
        PackageManager pm = getPackageManager();
		disable_APP(getApplicationContext());
        ComponentName name = new ComponentName(this, DefaultActivity.class);
        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        // terminate the activity.
        finish();
    }

	 private static final String packageNameDialer[]   = {
			 "com.android.dialer",
	 };
	 private static final String packageNameMMS[]    = {
			 "com.android.mms"
	 };

	 private void disable_APP(Context mcontext){
		 PackageManager pm = getPackageManager();
		 setApplicationEnabled(
				 packageNameMMS,
				  SystemProperties.getBoolean("ro.sms.capable",false),
				 //false,
				 pm);
		 setApplicationEnabled(
				 packageNameDialer,
				 SystemProperties.getBoolean("ro.voice.capable",false),
				// false,
				 pm);
	 }



	 private void setApplicationEnabled(String packageName[], boolean enable,
              PackageManager pm) {
		          for (int i = 0, j = packageName.length; i < j; i++) {
				  if (isPackageInstalled(packageName[i], pm)) {
					  pm.setApplicationEnabledSetting(
							  packageName[i],
							  enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
							  : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
							  PackageManager.DONT_KILL_APP);
				  }
			  }
	 }

	 private boolean isPackageInstalled(String packageName, PackageManager pm) {
		         List<PackageInfo> installedList = pm
		                 .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		         int installedListSize = installedList.size();
		        for (int i = 0; i < installedListSize; i++) {
				             PackageInfo tmp = installedList.get(i);
				             if (packageName.equalsIgnoreCase(tmp.packageName)) {
						                 return true;
						             }
			}
			return false;
	 }
}

