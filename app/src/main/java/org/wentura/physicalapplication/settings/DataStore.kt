package org.wentura.physicalapplication.settings

import androidx.preference.PreferenceDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DataStore : PreferenceDataStore() {
    private val db = Firebase.firestore

    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    companion object {
        const val TAG = "DataStore"
    }

    override fun putBoolean(key: String?, value: Boolean) {
        if (key == null) return

        db.collection("users")
            .document(uid)
            .update(key, value)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return defValue
    }
}
