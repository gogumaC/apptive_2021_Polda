package com.apptive.android.polda.customView

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import com.apptive.android.polda.R
import com.apptive.android.polda.databinding.ItemPolaroidBackBinding

@RequiresApi(Build.VERSION_CODES.O)
class PolaroidBack@kotlin.jvm.JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int=0)
    : LinearLayout(context,attrs,defStyleAttr) {

    private val binding: ItemPolaroidBackBinding
    private var tagContainerView:FlowLayout
    private var titleView:EditText
    private var memoView:EditText
    private var tagInputView:EditText
    private var fontNum:Int=0
    private var typeFace:Typeface?=null
    private val TYPELIMIT=7
    private val COUNTLIMIT=5
    private var atLimit=""



    val title:String
        get() { return "${titleView.text}"}


    val memo:String
        get(){return "${memoView.text}"}

    var hashTagList= mutableListOf<String>()




    init {
        binding = ItemPolaroidBackBinding.inflate(LayoutInflater.from(context), this, true)
        titleView = binding.title
        memoView = binding.memoView
        tagContainerView = binding.flowLayout
        tagInputView = binding.hashTagInput



        tagInputView.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }


            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.toString().length>TYPELIMIT+1){
                    Toast.makeText(context,"0해시태그는 ${TYPELIMIT}자 내로 ${COUNTLIMIT}개까지 적을 수 있어요(◞‸◟；)",Toast.LENGTH_SHORT).show()
                    tagInputView.setText(atLimit)
                }
                else if(hashTagList.size==COUNTLIMIT&&p0!!.contains(" ")){
                    Toast.makeText(context,"1해시태그는 ${TYPELIMIT}자 내로 ${COUNTLIMIT}개까지 적을 수 있어요(◞‸◟；)",Toast.LENGTH_SHORT).show()
                    tagInputView.setText("")
                }
                else {
                        Log.d("checkfor",p0.toString()+"   "+atLimit)
                    if(p0?.length==TYPELIMIT){
                        atLimit=p0.toString()
                    }
                    if(p0.toString()!=" "&&p0!!.contains(" ")&&hashTagList.size<COUNTLIMIT){

                        Log.d("PolaroidBack",p0.toString())
                        val inputText=p0.toString().replace(" ","")
                        createTagView(inputText,typeFace,true)
                        hashTagList.add(inputText)
                    }

                    if(p0.toString()==" ") tagInputView.text=null
                }

            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        tagInputView.setOnKeyListener(object:View.OnKeyListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                if(hashTagList.size==COUNTLIMIT){
                    Toast.makeText(context,"2해시태그는 ${TYPELIMIT}자 내로 ${COUNTLIMIT}개까지 적을 수 있어요(◞‸◟；)",Toast.LENGTH_SHORT).show()
                }
                else if(p2?.getAction()==KeyEvent.ACTION_DOWN&&p1==KeyEvent.KEYCODE_ENTER&&hashTagList.size<COUNTLIMIT){
                    val inputText=tagInputView.text.toString()
                    if(inputText!="") {
                        createTagView(inputText,typeFace,true)
                        hashTagList.add(inputText)
                    }
                }

                return false
            }
        })

        tagContainerView.setOnClickListener{
            tagInputView.requestFocus()
            val inputManager=context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(tagInputView,0)
        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createTagView(text:String, tf:Typeface?, enable:Boolean){


            if(text!=""){
                Log.d("PolaroidBack",hashTagList.toString())
                val tagView= TagView(context)
                val lp=FlowLayout.LayoutParams(10,10)
                tagView.layoutParams=lp
                tagView.text=text
                if(!enable) {
                    tagView.setDelButton()
                }
                tagContainerView.addView(tagView)
                tagView.delButtonClicked(object:TagView.BackButtonClickListener{
                    override fun setOnDelButtonClick(v: View,text: String) {
                        tagContainerView.removeView(v)
                        hashTagList.remove(text)
                    }
                })
                if(tf!=null){
                    tagView.setFont(tf)
                }

                refreshTagInput(tagInputView)
            }


    }


    private fun refreshTagInput(v:View){
        tagContainerView.removeView(v)
        tagContainerView.addView(v)
        tagInputView.text=null
        tagInputView.requestFocus()

    }


    fun getState():MemoState{
        val state=MemoState(titleView.text.toString(), memoView.text.toString(), hashTagList,fontNum)
        Log.d("checkfor","넣는 메모태그" + hashTagList.toString())
        return state
    }


    fun restoreMemoState(state:MemoState){
        if(findViewById<FlowLayout>(R.id.flowLayout)!=null){
            if(tagContainerView.isNotEmpty()){
                val temp=tagInputView
                tagContainerView.removeAllViewsInLayout()
                tagContainerView.addView(temp)
            }
            fontNum=state.fontNum
            typeFace=getTypeFace(state.fontNum)
            titleView.setText(state.title)
            memoView.setText(state.memo)
            setTextViewFont(typeFace!!)
            if(state.tagList!=null){
                hashTagList=state.tagList
                hashTagList.forEach {
                    createTagView(it,typeFace,true)
                }
            }
        }

    }
    fun restoreShowMemoState(state:MemoState) {
        if (findViewById<FlowLayout>(R.id.flowLayout) != null) {
            tagContainerView.removeAllViewsInLayout()
            fontNum=state.fontNum!!
            typeFace=getTypeFace(state.fontNum)
            titleView.setText(state.title)
            memoView.setText(state.memo)

            setTextViewFont(typeFace!!)
            if (state.tagList != null) {
                hashTagList = state.tagList
                hashTagList.forEach {
                    createTagView(it,typeFace,false)
                }
            }
        }
    }

    private fun setUseableEditText(et: EditText, useable: Boolean) {
        et.isClickable = useable
        et.isEnabled = useable
        et.isFocusable = useable
        et.isFocusableInTouchMode = useable
    }
    fun setEnable(){
        setUseableEditText(titleView,false)
        setUseableEditText(memoView,false)
        setUseableEditText(tagInputView,false)
        tagInputView.visibility=GONE
    }


    private fun setTextViewFont(tf:Typeface){
        titleView.setTypeface(tf)
        memoView.setTypeface(tf)
        tagInputView.setTypeface(tf)
    }

    fun getTypeFace(fontNum:Int?):Typeface{
        val fontId=when(fontNum){
            0->R.font.binggrae_samanco
            1->R.font.cafe24oneprettynight
            2->R.font.goodtoday_medium
            3->R.font.kyobo_handwriting
            4->R.font.maruburi_regular
            5->R.font.nanum_squarer
            6->R.font.scdream6
            7->R.font.uhbee_ann
            else->R.font.font
        }
        val tf=context.resources.getFont(fontId)
        return tf
    }
}

data class MemoState(var title:String="",var memo:String="",val tagList:MutableList<String>?=null,var fontNum:Int = 0)