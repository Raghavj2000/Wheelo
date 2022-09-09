package com.example.wheelchairguidance.views

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wheelchairguidance.MainActivity
import com.example.wheelchairguidance.MapsActivity
import com.example.wheelchairguidance.R
import com.example.wheelchairguidance.adapter.RecyclerViewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.osmdroid.bonuspack.location.NominatimPOIProvider
import org.osmdroid.bonuspack.location.POI
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.FolderOverlay
import java.util.ArrayList

class ListRecyclerView : AppCompatActivity() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter;
    private var poiList: List<POI>? = null
    lateinit var viewModel: SearchViewModel
    var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_recycler_view)
        val actionBar: ActionBar?
        actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#718FF7"))
        actionBar!!.setBackgroundDrawable(colorDrawable)
        val poiString = intent.getSerializableExtra("poiList")
        location = intent.getParcelableExtra("location") as Location?

        adapter = RecyclerViewAdapter(
            RecyclerViewAdapter.OnClickListener{poi ->
                Toast.makeText(this, "${poi.mLocation.latitude} , ${poi.mLocation.longitude}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("poi", poi)
                intent.putExtra("location", location)
                startActivity(intent)
            }
        )

        layoutManager = LinearLayoutManager(this)
//        searchpoi(poiString as String)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        Log.d("ListRecyclerView", "onCreate: "+poiList)
//        adapter = RecyclerViewAdapter(poiList)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        viewModel.poiList.observe(this,{
            adapter.setList(it)
        })

        viewModel.loading.observe(this, Observer {
            if(it){
                Log.d("ListRecyclerView", "onCreate: Loading")
            }else{
                Log.d("ListRecyclerView", "onCreate: Not Loading")
            }
        })

        viewModel.searchPoi(poiString as String)


    }
    private fun searchpoi(poi: String) {
        GlobalScope.launch(Dispatchers.IO) {
                val startPoint = GeoPoint(13.0545, 77.5916)
                val poiProvider = NominatimPOIProvider("OSMBonusPackTutoUserAgent")
                poiList = poiProvider.getPOICloseTo(startPoint, poi, 70, 0.1)
//                setAdapter(poList)
        }
       

        val poiMarkers = FolderOverlay(this)

//        map.overlays.add(poiMarkers)
    }

    override fun onBackPressed() {
        if(isTaskRoot()){
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }

//    private fun setAdapter(poList: ArrayList<POI>) {
//        Log.d("ListRecyclerView", "setAdapter: "+ poList)
//        adapter = RecyclerViewAdapter(poList)
//    }
}