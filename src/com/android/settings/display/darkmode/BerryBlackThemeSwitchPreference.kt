/*
 * Copyright (C) 2024 The Android Open Source Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.display.darkmode

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.provider.Settings
import com.android.settings.R
import com.android.settingslib.datastore.KeyValueStore
import com.android.settingslib.datastore.SettingsSecureStore
import com.android.settingslib.metadata.PreferenceAvailabilityProvider
import com.android.settingslib.metadata.ReadWritePermit
import com.android.settingslib.metadata.SwitchPreference

class BerryBlackThemeSwitchPreference(private val berryBlackThemeDataStore: KeyValueStore) :
    SwitchPreference(KEY, R.string.berry_black_theme_title, R.string.berry_black_theme_summary),
    PreferenceAvailabilityProvider {

    override fun storage(context: Context) = berryBlackThemeDataStore

    override fun isAvailable(context: Context) = 
        context.isDarkMode() && isPackageInstalled(context, EUCLID_BLACK_THEME)

    private fun Context.isDarkMode() =
        (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_YES) != 0

    override fun getReadPermissions(context: Context) =
        SettingsSecureStore.getReadPermissions()

    override fun getWritePermissions(context: Context) =
        SettingsSecureStore.getWritePermissions()

    override fun getReadPermit(context: Context, callingPid: Int, callingUid: Int) =
        ReadWritePermit.ALLOW

    override fun getWritePermit(context: Context, callingPid: Int, callingUid: Int) =
        ReadWritePermit.ALLOW

    companion object {
        const val KEY = "berry_black_theme"
        const val DEFAULT_VALUE = false
        const val EUCLID_BLACK_THEME = "com.euclid.overlay.customization.blacktheme"

        val Context.berryBlackThemeDataStore: KeyValueStore
            get() =
                SettingsSecureStore.get(this).apply { 
                    setDefaultValue(KEY, DEFAULT_VALUE) 
                }
        
        fun isPackageInstalled(context: Context, pkg: String): Boolean =
            try {
                val pi = context.packageManager.getPackageInfo(pkg, 0)
                pi?.applicationInfo?.enabled == true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
    }
}
