package org.wentura.franko.activityedit

import androidx.preference.PreferenceDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.wentura.franko.Constants


class DataStoreActivity(
    private val activityId: String
) : PreferenceDataStore() {

    private val db = Firebase.firestore

    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    companion object {
        val TAG = DataStoreActivity::class.simpleName
    }

    override fun putBoolean(key: String?, value: Boolean) {
        if (key == null) return

        db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
            .document(activityId)
            .update(key, value)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return defValue
    }

    override fun putString(key: String?, value: String?) {
        if (key == null) return

        db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
            .document(activityId)
            .update(key, value)
    }

    override fun getString(key: String?, defValue: String?): String? {
        return defValue
    }
}
