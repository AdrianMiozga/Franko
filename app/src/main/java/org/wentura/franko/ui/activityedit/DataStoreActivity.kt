package org.wentura.franko.ui.activityedit

import androidx.preference.PreferenceDataStore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.wentura.franko.Constants


class DataStoreActivity(
    private val activityId: String
) : PreferenceDataStore() {

    private val db = Firebase.firestore

    companion object {
        val TAG = DataStoreActivity::class.simpleName
    }

    override fun putBoolean(key: String?, value: Boolean) {
        if (key == null) return

        db.collection(Constants.ACTIVITIES)
            .document(activityId)
            .update(key, value)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return defValue
    }

    override fun putString(key: String?, value: String?) {
        if (key == null) return

        db.collection(Constants.ACTIVITIES)
            .document(activityId)
            .update(key, value?.trim())
    }

    override fun getString(key: String?, defValue: String?): String? {
        return defValue
    }
}
