

package com.exampleRxjava.android.cheesfind

import android.content.Context
import android.util.Log
import com.exampleRxjava.android.cheesfind.database.Cheese
import com.exampleRxjava.android.cheesfind.database.CheeseDatabase

class CheeseSearchEngine(private val context: Context) {

  fun search(query: String): List<Cheese> {
    Thread.sleep(2000)
    Log.d("Searching", "Searching for $query")
    return CheeseDatabase.getInstance(context).cheeseDao().findCheese("%$query%")
  }

}