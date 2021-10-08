package org.wentura.franko.settings

import androidx.preference.PreferenceDataStore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.wentura.franko.Constants
import org.wentura.franko.Utilities.getCurrentUserUid

class DataStore : PreferenceDataStore() {

    private val db = Firebase.firestore
    private val uid = getCurrentUserUid()

    companion object {
        val TAG = DataStore::class.simpleName
    }

    override fun putBoolean(key: String?, value: Boolean) {
        if (key == null) return

        db.collection(Constants.USERS)
            .document(uid)
            .update(key, value)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return defValue
    }

    override fun putString(key: String?, value: String?) {
        if (key == null) return

        db.collection(Constants.USERS)
            .document(uid)
            .update(key, value?.trim())
    }

    override fun getString(key: String?, defValue: String?): String? {
        return defValue
    }
}
