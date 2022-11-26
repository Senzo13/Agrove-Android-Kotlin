package fr.devid.plantR.services

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit
import javax.inject.Inject

class SharedPreferencesService @Inject constructor(context: Context) {

    companion object {
        private const val AUTH_TOKEN = "AUTH_TOKEN"
        private const val ISBRANCH = "ISBRANCH"
        private const val TYPE = "TYPE"
        private const val GARDENERID = "GARDENERID"
        private const val GARDENERNAME = "GARDENERNAME"
        private const val OLDTOPICS = "OLDTOPICS"

    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    internal var token: String?
        get() = prefs.getString(AUTH_TOKEN, null)
        set(value) = prefs.edit { putString(AUTH_TOKEN, value) }

    internal var isBranch: Boolean
        get() = prefs.getBoolean(ISBRANCH, false)
        set(value) = prefs.edit { putBoolean(ISBRANCH, value)
        }

    internal var type: String?
        get() = prefs.getString(TYPE, null)
        set(value) = prefs.edit { putString(TYPE, value)
        }

    internal var gardenerId: String?
        get() = prefs.getString(GARDENERID, null)
        set(value) = prefs.edit { putString(GARDENERID, value)
        }
    internal var oldTopics: String?
        get() = prefs.getString(OLDTOPICS, "")
        set(value) = prefs.edit { putString(OLDTOPICS, value)
        }
    internal var gardenerName: String?
        get() = prefs.getString(GARDENERNAME, null)
        set(value) = prefs.edit { putString(GARDENERNAME, value)
        }
}