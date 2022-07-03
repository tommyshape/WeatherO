package com.example.weathero.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathero.MainViewModel
import com.example.weathero.R
import com.example.weathero.Repository
import com.example.weathero.adapters.RecyclerAdapter
import com.example.weathero.database.Weather
import com.example.weathero.database.WeatherViewModel
import com.example.weathero.databinding.FragmentAddBinding
import com.example.weathero.myobjects.WeatherMain
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


private const val TAG = "my_tag"
private const val LOCATION_PERMISSION_CODE = 1

class AddFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var adapter: RecyclerAdapter
    private var userGivenLocationNameString = "userGiven..."
    var list: List<Weather> = mutableListOf()
    lateinit var binding: FragmentAddBinding
    private lateinit var drawableAnimation: AnimationDrawable
    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(UP or DOWN or START or END,
            LEFT or RIGHT) {

            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                val adapter = recyclerView.adapter as RecyclerAdapter
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                Handler().postDelayed({
                    //doSomethingHere()
                    adapter.moveItem(from, to)
                }, 1000)

                adapter.notifyItemMoved(from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                val itemToDelete = list[position]
                weatherViewModel.deleteWeather(itemToDelete)
                adapter.notifyItemRemoved(position)

            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if (actionState == ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.5f
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)

                viewHolder.itemView.alpha = 1.0f
            }
        }

        ItemTouchHelper(simpleItemTouchCallback)
    }
    //for current location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_add, container, false)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbarList)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        weatherViewModel.readData().observe(viewLifecycleOwner){
            list = it
        }
        setUpRecyclerView(binding)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        initializeLocationRequest()
        initializeLocationCallback()

        clearSky()


        return binding.root
    }


    private fun initializeLocationRequest(){
        locationRequest = LocationRequest().apply {
            interval = TimeUnit.SECONDS.toMillis(10)
            fastestInterval = TimeUnit.SECONDS.toMillis(5)
            //maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun initializeLocationCallback(){
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location = locationResult.lastLocation
                // use latitude and longitude as per your need
                /*if (currentLocation != null){

                    nameLocationDialog()

                    val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                    removeTask.addOnCompleteListener {task->
                        if (task.isSuccessful){
                            Toast.makeText(requireActivity(), "update canceled", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(requireActivity(), "failed to cancel update", Toast.LENGTH_SHORT).show()
                        }
                    }
                }*/

                currentLocation = location
                Toast.makeText(requireActivity(), "lat: ${currentLocation!!.latitude}\n lon: ${currentLocation!!.longitude}", Toast.LENGTH_SHORT).show()
                nameLocationDialog()
                val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                removeTask.addOnCompleteListener {task->
                    if (task.isSuccessful){
                        Toast.makeText(requireActivity(), "update canceled", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireActivity(), "failed to cancel update", Toast.LENGTH_SHORT).show()
                    }
                }


            }
        }
    }

    private fun startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { return }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!)
    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireActivity(), "Step_1 checkPermission()\nYou have already granted location permission!", Toast.LENGTH_SHORT).show()
            enableGps()



        } else {
            Toast.makeText(requireActivity(), "Step_1 checkPermission()\nYou haven't granted location permission!", Toast.LENGTH_SHORT).show()
            requestLocationPermission()
        }
    }

    private fun enableGps() {
        //to enable gps at the launch of application
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val result: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(requireActivity()).checkLocationSettings(builder.build())


        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
                startLocationUpdates()



            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                requireActivity(),
                                LocationRequest.PRIORITY_HIGH_ACCURACY
                            )


                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    private fun requestLocationPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )) {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Permission needed")
            builder.setMessage("This permission is needed to locate your device location")
            builder.setPositiveButton("Ok"){_,_ ->
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)

                //????????
                //enableGps()
                //startLocationUpdates()
            }
            builder.setNegativeButton("Cancel"){_,_ -> }
            builder.create().show()
        }
        else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }
    }






















    private fun setUpRecyclerView(binding: FragmentAddBinding){
        weatherViewModel.readAllData.observe(viewLifecycleOwner){ listOfWeather ->
            /*val */adapter = RecyclerAdapter(listOfWeather!!)

            adapter.setMyInterfaceForList(object : RecyclerAdapter.MyInterfaceForList{
                override fun clickToSave2(list: MutableList<Weather>) {

                    list.asReversed().forEachIndexed { index, weather ->
                        weather.time = index.toString()
                    }
                    list.forEach {
                        Log.d(TAG, "clickToSave2: ${it.city_name}")
                    }
                    list.forEachIndexed { index, weather ->
                        weatherViewModel.updateWeather(weather)
                    }

                }

            })
            itemTouchHelper.attachToRecyclerView(binding.recyclerViewAdd)
            adapter.differ.submitList(weatherViewModel.readAllData.value)
            binding.recyclerViewAdd.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewAdd.setHasFixedSize(true)
            binding.recyclerViewAdd.adapter = adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
        val search = menu.findItem(R.id.list_search)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.queryHint = "city name"
        searchView?.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {requireActivity().onBackPressed()
                true
            }
            R.id.list_mylocation -> {
                checkPermission()
            true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    //var userGivenLocationNameString = ""
    private fun nameLocationDialog(){
        val alertAdd = AlertDialog.Builder(requireActivity())
        val input = EditText(requireActivity())
        //val factory: LayoutInflater = LayoutInflater.from(requireActivity())
        //val view: View = factory.inflate(R.layout.alert_dialog, null)
        alertAdd.setMessage("Give this location a name")
        alertAdd.setView(input)
        alertAdd.setNegativeButton("Cancel",DialogInterface.OnClickListener { dialog, which ->  } )
        alertAdd.setPositiveButton("Ok", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                userGivenLocationNameString = input.text.toString()
                //Toast.makeText(requireContext(), userGivenLocationNameString, Toast.LENGTH_SHORT).show()
                //burada sehir ekleme yap.

                try{
                    Repository.setMyInterface(object : Repository.MyInterface{
                    override fun clickToSave(response: Response<WeatherMain>?) {
                        Log.d(TAG, "clickToSave: saving response")
                        if (response != null){
                            weatherViewModel.addWeather(Weather(0, response!!.body(), userGivenLocationNameString.capitalize(), time()))
                        }
                    }

                    override fun updateWeather(list: MutableList<Response<WeatherMain>>) {
                        TODO("Not yet implemented")
                    }
                })
                    mainViewModel.requestWeatherByLatLon(currentLocation!!.latitude.toString(), currentLocation!!.longitude.toString())
                }catch (e: Exception){
                    Log.d(TAG, "onClick: $e")
                }

            }
        })
        alertAdd.show()

    }





    override fun onQueryTextSubmit(query: String?): Boolean {
        Toast.makeText(requireContext(), query, Toast.LENGTH_SHORT).show()

        try {
            mainViewModel.initialSearch(query!!)
            Repository.setMyInterface(object : Repository.MyInterface{
                override fun clickToSave(response: Response<WeatherMain>?) {
                    Log.d(TAG, "clickToSave: saving response")
                    weatherViewModel.addWeather(Weather(0, response!!.body(), query.toLowerCase().capitalize(), time()))
                }

                override fun updateWeather(list: MutableList<Response<WeatherMain>>) {
                    TODO("Not yet implemented")
                }
            })
            hideKeyboard()
        }catch (e: Exception){
            Log.d(TAG, "onQueryTextSubmit: $e")

        }

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        return true
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun time(): String {
        /*val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return formatter.format(now).toString()*/
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") //or use getDateInstance()
        return formatter.format(date)
    }


    private fun clearSky(){
        binding.constraintLayout.setBackgroundResource(R.drawable.add_fragment_animation)
        drawableAnimation = binding.constraintLayout.background as AnimationDrawable
        //drawableAnimation.setEnterFadeDuration(2000)
        drawableAnimation.setExitFadeDuration(3500)
        drawableAnimation.isOneShot = false
        drawableAnimation.start()
    }


}