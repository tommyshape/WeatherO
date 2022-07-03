package com.example.weathero.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.example.weathero.MainViewModel
import com.example.weathero.R
import com.example.weathero.Repository
import com.example.weathero.adapters.ViewPagerAdapter
import com.example.weathero.database.Weather
import com.example.weathero.database.WeatherViewModel
import com.example.weathero.databinding.FragmentListBinding
import com.example.weathero.myobjects.WeatherMain
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "my_tag_list"

class ListFragment : Fragment() {

    private lateinit var mainviewmodel: MainViewModel
    private lateinit var weatherViewModel: WeatherViewModel

    private lateinit var drawableAnimation: AnimationDrawable

    lateinit var navController: NavController
    lateinit var binding: FragmentListBinding

    lateinit var sharedPreferences: SharedPreferences

    val SHARED_PREFS = "sharedPrefs"
    //val sharedPreferences: SharedPreferences = ListFragment().activity!!.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainviewmodel = ViewModelProvider(this)[MainViewModel::class.java]
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        binding = DataBindingUtil.inflate<FragmentListBinding?>(inflater, R.layout.fragment_list, container, false).apply {
            this.lifecycleOwner = this@ListFragment
            this.viewmodel = mainviewmodel
        }

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbarMain)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        setHasOptionsMenu(true)

        sharedPreferences = activity!!.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

        var list = listOf<Weather>()

        weatherViewModel.readData().observe(viewLifecycleOwner){
            val adapter = ViewPagerAdapter(it)
            binding.viewPager.adapter = adapter
            list = it
        }

        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()
        Log.d(TAG, "Saat epoch -> $ts")

        val date = SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Date(tsLong * 1000))
        Log.d(TAG, "Saat insan -> $date")

        var lastValue = ""
        var typeOfLastWeather = ""
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                Log.d(TAG, "onPageScrolled: position: $position positionOffset: $positionOffset positionOffsetPixels: $positionOffsetPixels")
                var typeOfWeather = ""
                when(list[position].weatherMain!!.current.weather[0].icon.dropLast(1)){
                    "01" -> {typeOfWeather = "clear_sky"}
                    "02" -> {typeOfWeather = "few_clouds"}
                    "03" -> {typeOfWeather = "scattered_clouds"}
                    "04" -> {typeOfWeather = "broken_clouds"}
                    "09" -> {typeOfWeather = "shower_rain"}
                    "10" -> {typeOfWeather = "rain"}
                    "11" -> {typeOfWeather = "thunderstorm"}
                    "13" -> {typeOfWeather = "snow"}
                    "50" -> {typeOfWeather = "mist"}
                }

                when(typeOfWeather){
                    "clear_sky" -> {
                        if (typeOfLastWeather == "clear_sky"){ return }
                        clearSky()
                        typeOfLastWeather = "clear_sky"
                    }
                    "few_clouds" -> {
                        if (typeOfLastWeather == "few_clouds"){ return }
                        fewClouds()
                        typeOfLastWeather = "few_clouds"
                    }
                    "scattered_clouds" -> {
                        if (typeOfLastWeather == "scattered_clouds"){ return }
                        scatteredClouds()
                        typeOfLastWeather = "scattered_clouds"
                    }
                    "broken_clouds" -> {
                        if (typeOfLastWeather == "broken_clouds" || typeOfLastWeather == "scattered_clouds"){ return }
                        scatteredClouds()
                        typeOfLastWeather = "broken_clouds"
                    }
                    "shower_rain" -> {
                        if (typeOfLastWeather == "thunderstorm" || typeOfLastWeather == "rain" || typeOfLastWeather == "shower_rain" || typeOfLastWeather == "mist"){ return }
                        showerRain()
                        typeOfLastWeather = "shower_rain"
                    }
                    "rain" -> {
                        if (typeOfLastWeather == "thunderstorm" || typeOfLastWeather == "rain" || typeOfLastWeather == "shower_rain" || typeOfLastWeather == "mist"){ return }
                        showerRain()
                        typeOfLastWeather = "rain"
                    }
                    "thunderstorm" -> {
                        if (typeOfLastWeather == "thunderstorm" || typeOfLastWeather == "rain" || typeOfLastWeather == "shower_rain" || typeOfLastWeather == "mist"){ return }
                        showerRain()
                        typeOfLastWeather = "thunderstorm"
                    }
                    "snow" -> {
                        if (typeOfLastWeather == "snow"){ return }
                        snow()
                        typeOfLastWeather = "snow"
                    }
                    "mist" -> {
                        if (typeOfLastWeather == "mist"){ return }
                        showerRain()
                        typeOfLastWeather = "mist"
                    }
                }
                /*//Gunesli
                val type: String
                if (iconValue.contains("01") || iconValue.contains("02") ||iconValue.contains("10")){
                    Log.d(TAG, "its sunny")
                    type = "blueSky"
                }
                else{
                    Log.d(TAG, "its not sunny")
                    type = "darkSky"
                }

                val colorBlueSky = requireActivity().resources.getColor(R.color.blue_sky)
                val colorBlueSky2 = requireActivity().resources.getColor(R.color.blue_sky2)
                val colorDarkSky = requireActivity().resources.getColor(R.color.dark_sky)
                val colorDarkSky2 = requireActivity().resources.getColor(R.color.dark_sky2)

                when(type){
                    "blueSky" -> {
                        if (lastValue == "blueSky"){
                            return
                        }
                        if (lastValue == ""){
                            val inBlueSky =
                                ObjectAnimator.ofObject(binding.constraintLayout, "backgroundColor", ArgbEvaluator(),
                                    colorBlueSky2 , colorBlueSky)
                            inBlueSky.duration = 800
                            inBlueSky.startDelay = 0
                            inBlueSky.start()
                            lastValue = "blueSky"
                        }
                        if (lastValue == "darkSky"){
                            val inBlueSky =
                                ObjectAnimator.ofObject(binding.constraintLayout, "backgroundColor", ArgbEvaluator(),
                                    colorDarkSky , colorBlueSky)
                            inBlueSky.duration = 800
                            inBlueSky.startDelay = 0
                            inBlueSky.start()
                            lastValue = "blueSky"
                        }
                    }
                    "darkSky" -> {
                        if (lastValue == "darkSky"){
                            return
                        }
                        if (lastValue == ""){
                            val inDarkSky =
                                ObjectAnimator.ofObject(binding.constraintLayout, "backgroundColor", ArgbEvaluator(),
                                    colorDarkSky2 , colorDarkSky)
                            inDarkSky.duration = 800
                            inDarkSky.startDelay = 0
                            inDarkSky.start()
                            lastValue = "darkSky"
                        }
                        if (lastValue == "blueSky"){
                            val inDarkSky =
                                ObjectAnimator.ofObject(binding.constraintLayout, "backgroundColor", ArgbEvaluator(),
                                    colorBlueSky , colorDarkSky)
                            inDarkSky.duration = 800
                            inDarkSky.startDelay = 0
                            inDarkSky.start()
                            lastValue = "darkSky"
                        }
                    }
                }*/
            }
        })

        var check = false
        binding.buttonRefresh.setOnClickListener {
            if (check){
                check = false
            }
            weatherViewModel.readData().observe(viewLifecycleOwner){ bigList ->

                Log.d(TAG, "refresh list size: ${bigList.size}")

                if (!check){
                    mainviewmodel.updateWeather(bigList)
                    Repository.setMyInterface(object : Repository.MyInterface{
                        override fun clickToSave(response: Response<WeatherMain>?) {
                            TODO("Not yet implemented")
                        }
                        override fun updateWeather(list: MutableList<Response<WeatherMain>>) {
                            Log.d(TAG, "updateWeather: gelen list ${list.size}")
                            bigList.forEachIndexed { index, weather ->
                                weather.weatherMain = list[index].body()
                                weatherViewModel.updateWeather(weather)
                            }
                            //update tarihini kaydet
                            val editor = sharedPreferences.edit()
                            editor.putString("update_date", time())
                            editor.apply()
                            binding.lastUpdatedTv.text = "Last updated: ${time()}"


                            return
                        }
                    })
                    check = true
                }
            }
        }
        binding.buttonRefresh.setOnTouchListener { p0, motionEvent ->
            when (motionEvent!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.buttonRefresh.drawable.setColorFilter(
                        0x77000000,
                        PorterDuff.Mode.SRC_ATOP
                    )
                    binding.buttonRefresh.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    binding.buttonRefresh.drawable.clearColorFilter()
                    binding.buttonRefresh.invalidate()}
                MotionEvent.ACTION_CANCEL -> {
                    binding.buttonRefresh.drawable.clearColorFilter()
                    binding.buttonRefresh.invalidate()
                }
            }
            false
        }

        //once ilk kayitli updated date'i bul ve goster.
        //update tusuna bisincada yenisini shared pref'e ekle



        val update_date = sharedPreferences.getString("update_date", "no date")
        val dateToShow = "Last updated: $update_date"
        binding.lastUpdatedTv.text = dateToShow


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.list_cities -> {
                navController.navigate(R.id.action_listFragment_to_addFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clearSky(){
        binding.constraintLayout.setBackgroundResource(R.drawable.clear_sky)
        drawableAnimation = binding.constraintLayout.background as AnimationDrawable
        //drawableAnimation.setEnterFadeDuration(2000)
        drawableAnimation.setExitFadeDuration(500)
        drawableAnimation.isOneShot = true
        drawableAnimation.start()
    }
    private fun fewClouds(){
        binding.constraintLayout.setBackgroundResource(R.drawable.few_clouds)
        drawableAnimation = binding.constraintLayout.background as AnimationDrawable
        //drawableAnimation2.setEnterFadeDuration(2000)
        drawableAnimation.setExitFadeDuration(1000)
        drawableAnimation.isOneShot = true
        drawableAnimation.start()
    }
    private fun scatteredClouds(){
        binding.constraintLayout.setBackgroundResource(R.drawable.scattered_clouds)
        drawableAnimation = binding.constraintLayout.background as AnimationDrawable
        //drawableAnimation2.setEnterFadeDuration(2000)
        drawableAnimation.setExitFadeDuration(1000)
        drawableAnimation.isOneShot = true
        drawableAnimation.start()
    }
    private fun showerRain(){
        binding.constraintLayout.setBackgroundResource(R.drawable.shower_rain)
        drawableAnimation = binding.constraintLayout.background as AnimationDrawable
        //drawableAnimation2.setEnterFadeDuration(2000)
        drawableAnimation.setExitFadeDuration(1000)
        drawableAnimation.isOneShot = true
        drawableAnimation.start()
    }
    private fun snow(){
        binding.constraintLayout.setBackgroundResource(R.drawable.snow)
        drawableAnimation = binding.constraintLayout.background as AnimationDrawable
        //drawableAnimation2.setEnterFadeDuration(2000)
        drawableAnimation.setExitFadeDuration(1000)
        drawableAnimation.isOneShot = true
        drawableAnimation.start()
    }
    private fun time(): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("MM/dd HH:mm") //or use getDateInstance()
        return formatter.format(date)
    }



}

