package com.apptive.android.polda.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.apptive.android.polda.AdapterDetailViewPager
import com.apptive.android.polda.R
import com.apptive.android.polda.databinding.FragmentDetailBinding

class FragmentDetail : Fragment() {
    private lateinit var binding:FragmentDetailBinding
    //private val detailViewModel:ViewModelDetail by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_detail,container,false)
        val sampleList=listOf("1","2","3","4","5")
        val pageNum=sampleList.size
        val adapter= AdapterDetailViewPager()
        val seekBar=binding.seekBar
        val detailViewPager=binding.viewPager
        val fabMain=binding.detailFabMain
        adapter.sampleTitleList=sampleList
        detailViewPager.adapter=adapter

        //클릭 이벤트
        adapter.setOnItemClickListener(object : AdapterDetailViewPager.OnItemClickListener {
            override fun onItemClick(v: View, pos: Int) {
                //TODO("코드 더 간단하게 수정하는 방법찾기")
                val pol1 = view?.findViewById<View>(R.id.polaloid1)
                val pol2 = view?.findViewById<View>(R.id.polaloid2)
                val pol3 = view?.findViewById<View>(R.id.polaloid3)
                val pol4 = view?.findViewById<View>(R.id.polaloid4)

                pol1?.setOnClickListener { polaloidClicked()}
                pol2?.setOnClickListener { polaloidClicked()}
                pol3?.setOnClickListener { polaloidClicked()}
                pol4?.setOnClickListener { polaloidClicked()}

            }
            fun polaloidClicked(){}
        })

        //FAB 누르면 편집부로 전환
//        TODO("fab버튼 앞으로 빼고 다시 확인 요망")
        fabMain.setOnClickListener{
            val action=FragmentDetailDirections.actionFragmentDetailToFragmentEdit()
            it.findNavController().navigate(action)
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