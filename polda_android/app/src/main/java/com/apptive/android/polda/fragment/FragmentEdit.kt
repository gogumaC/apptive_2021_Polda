package com.apptive.android.polda.fragment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.apptive.android.polda.R
import com.apptive.android.polda.databinding.FragmentEditBinding


class FragmentEdit : Fragment() {
    private lateinit var binding: FragmentEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit, container, false)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnFlip.setOnClickListener {
            val action = FragmentEditDirections.actionFragmentEditToFragmentEditMemo2()
            view.findNavController().navigate(action)
        }
        binding.btnShowStickers.setOnClickListener {
            //frame.setVisibility(View.VISIBLE)
            //childFragmentManager.beginTransaction().add(R.id.fragment2,FragmentSticker()).commit()
//            val mTransaction:FragmentTransaction=getChildFragmentManager().beginTransaction()
//            mTransaction.add(R.id.frameLayout,FragmentSticker())
//            mTransaction.commit()
        }

        binding.btnEditSave.setOnClickListener {
            val action=FragmentEditDirections.actionGlobalShow()
            view.findNavController().navigate(action)
        }


    }
}