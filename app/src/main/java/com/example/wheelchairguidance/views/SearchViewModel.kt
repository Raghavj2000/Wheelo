package com.example.wheelchairguidance.views

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import org.osmdroid.bonuspack.location.NominatimPOIProvider
import org.osmdroid.bonuspack.location.POI
import org.osmdroid.util.GeoPoint

class SearchViewModel : ViewModel() {

    val poiList = MutableLiveData<List<POI>>()
    var job: Job? = null
    val loading = MutableLiveData<Boolean>()

    fun searchPoi(poi: String) {

        job = CoroutineScope(Dispatchers.IO).launch {
            val startPoint = GeoPoint(13.0545, 77.5916)
            val poiProvider = NominatimPOIProvider("OSMBonusPackTutoUserAgent")
            val listSearch: List<POI> = poiProvider.getPOICloseTo(startPoint, poi, 70, 0.1)
            withContext(Dispatchers.Main){
                if(listSearch!= null || listSearch){
                    poiList.postValue(listSearch)
                }else{
                    Log.d("SearchViewModel", "searchPoi: List Empty" + listSearch)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}