package com.apptive.android.polda

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.apptive.android.polda.databinding.FragmentDetailBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Observer

class FragmentDetail : Fragment() {
    private lateinit var binding:FragmentDetailBinding
    //private val detailViewModel:ViewModelDetail by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container,false)
        val sampleList=listOf("1","2","3","4","5")
        val pageNum=sampleList.size
        val adapter=AdapterDetailViewPager()
        val seekBar=binding.seekBar
        val detailViewPager=binding.viewPager
        val FabMain=binding.detailFabMain
        val FabDel=binding.detailFabDel
        val FabCopy=binding.detailFabCopy
        val FabPaste=binding.detailFabPaste
        adapter.sampleTitleList=sampleList
        detailViewPager.adapter=adapter

        //FAB
        FabMain.setOnClickListener{view->

        }



        //SeekBar-PageScroll

        //var page:Int
        //var scroll:Int
        var setProgress:Int

        detailViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d("checkfor", "page : ${position + 1}")
                seekBar.progress=when(position){
                    0->0
                    pageNum-1->100
                    else->100/(pageNum-1)*(position)

                }


            }

        })


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                Log.d("checkfor","progress: $p1, page : ${pageLocation(p1,pageNum)}")
                //Log.d("checkfor","page : $p1")
                setProgress=pageLocation(p1,pageNum)
                detailViewPager.setCurrentItem(pageLocation(setProgress,pageNum))


            }
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        return binding.root
    }

 fun pageLocation(progress:Int,pageNum:Int):Int{
     if(progress==100) return 4
     val page=progress.toDouble()*(pageNum.toDouble()/100)
     return if(page.toInt()!=pageNum) page.toInt()
        else page.toInt()
 }

}