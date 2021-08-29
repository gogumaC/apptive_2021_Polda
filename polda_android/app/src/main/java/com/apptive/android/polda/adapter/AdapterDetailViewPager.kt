package com.apptive.android.polda

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginRight
import androidx.core.view.setPadding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.apptive.android.polda.AdapterDetailViewPager.cutPola.cutPolaroid
import com.apptive.android.polda.customView.PolaroidFront
import com.apptive.android.polda.fragment.FragmentDetail
import com.apptive.android.polda.fragment.FragmentDetail.polar.polarListD
import com.apptive.android.polda.fragment.FragmentDetail.polaroidEditINFO.cutinmode
import com.apptive.android.polda.fragment.FragmentDetail.polaroidEditINFO.cutoffmode
import com.apptive.android.polda.fragment.FragmentDetail.polaroidEditINFO.deletemode
import com.apptive.android.polda.fragment.FragmentDetail.polaroidEditINFO.polarID
import com.apptive.android.polda.fragment.FragmentDetail.polaroidINFO.polarCount
import com.apptive.android.polda.fragment.FragmentDetailDirections


class AdapterDetailViewPager(val context:Context):RecyclerView.Adapter<DetailHolder>() {
    var dataList=listOf<PolaroidData>().apply{
        Log.d("detail","어댑터 호출")
    }
    object cutPola {
        lateinit var cutPolaroid: String
    }

    //클릭이벤트 설정
    interface OnItemClickListener{
        fun onItemClick(v:View, pos : Int)
    }
    var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    //롱클릭이벤트 설정
    interface OnItemLongClickListener{
        fun onItemLongClick()
    }
    var longListener:OnItemLongClickListener?=null
    fun setOnItemLongClickListener(listener:OnItemLongClickListener){
        this.longListener=listener
    }

    //터치 이벤트 설정
    interface OnItemTouchListener{
        fun onItemTouch(v:View,pos:Int)
    }
    var touchListener:OnItemTouchListener?=null
    fun setOnItemTouchListener(listener:OnItemTouchListener){
        this.touchListener=listener
    }
    //상태콜백
    interface OnItemStateListener{
        fun onItemState(pos:Int)//0:삭제 1:....
    }

    var statelistener : OnItemStateListener? = null
    fun setOnItemStateListener(listener : OnItemStateListener) {
        this.statelistener = listener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_detail,parent,false)
        return DetailHolder(view,this)
    }



    override fun onBindViewHolder(holder: DetailHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener?.onItemClick(it,position)
        }


        holder.itemView.setOnTouchListener(object:View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                touchListener?.onItemTouch(p0!!,position)
                return false
            }

        })
        holder.bind(position,dataList)
        holder.setOnItemStateListener(object :DetailHolder.OnItemStateListener{

            override fun onItemState(state: Int, pos: Int, v:View) {
                when (state) {
                    0 -> delete(pos)
                    1 -> cutoff(pos)
                    2 -> cutin(pos)
                    else->statelistener?.onItemState(state)
                }
            }
        })


        holder.setOnItemLongClickListener(object:DetailHolder.OnItemLongClickListener{
            override fun onItemLongClick() {
                longListener?.onItemLongClick()
            }
        })

    }

    override fun getItemCount(): Int {

        return getPageCount(dataList.size)
    }

    private fun getPageCount(size:Int):Int{
        val page=when{
            size==0->0
            size%4==0->size/4
            else->size/4+1
        }
        return page
    }

    fun delete(pos: Int){
        var builder = AlertDialog.Builder(context)
        builder.setMessage("삭제하시겠습니까?")

        var listener = object :
            DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                var dbHelperP: DBHelperPolaroid = DBHelperPolaroid(context, null, 1)
                var databaseP: SQLiteDatabase = dbHelperP.readableDatabase
                var cursor: Cursor = databaseP.rawQuery("SELECT * FROM polaroid", null)
                lateinit var date: String
                lateinit var arr: Array<String>
                while(cursor.moveToNext()) {
                    if(cursor.getString(cursor.getColumnIndex("_id")).equals(polarListD.get(pos).date)) {
                        arr = arrayOf(cursor.getString(cursor.getColumnIndex("_id")))
                        date = cursor.getString(cursor.getColumnIndex("diaryName"))
                        databaseP.delete("polaroid", "_id=?", arr)
                        break
                    }
                }
                polarCount--
                polarListD.removeAt(pos)
                //삭제 작업 끝. 인덱스 재정비
                cursor = databaseP.rawQuery("SELECT * FROM polaroid", null)


                var i = 0
                var j = 1
                var update = ContentValues()
                if(cursor.moveToFirst()) {
                    do{
                        if(cursor.getString(cursor.getColumnIndex("diaryName")).equals(date)){
                            i++
                        }
                    } while(cursor.moveToNext())
                    cursor.moveToFirst()
                    do {
                        if (cursor.getString(cursor.getColumnIndex("diaryName")).equals(date)) {
                            arr = arrayOf(cursor.getString(cursor.getColumnIndex("_id")))
                            update.clear()
                            update.put("polaroidIndex", j)
                            databaseP.update("polaroid", update, "_id=?", arr)
                            j++
                        }
                        if (j == i + 1) {
                            break
                        }
                    } while (cursor.moveToNext())
                }
                //인덱스 재정비 끝.

                Log.d("detail","DB : $polarListD")
                Log.d("detail","data : $dataList")
                statelistener?.onItemState(0).apply{
                    Log.d("detail","리스너 콜")
                }
            }
        }



        builder.setPositiveButton("네", listener)
        builder.setNegativeButton("아니오", null)
        builder.show()

    }


    fun cutoff(pos: Int){
        cutPolaroid = polarListD.get(pos).date
        cutinmode = true
        cutoffmode = false
        Toast.makeText(context, "앞에 붙여넣을 폴라로이드를 선택하세요.", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("Recycle")
    fun cutin(pos: Int){
        var dbHelperP: DBHelperPolaroid = DBHelperPolaroid(context, null, 1)
        var databaseP: SQLiteDatabase = dbHelperP.readableDatabase
        var cursor: Cursor = databaseP.rawQuery("SELECT * FROM polaroid", null)
        var update = ContentValues()
        var dontmoveIndex = ""
        var pull = false
        var isLastIndex = 0
        lateinit var arr: Array<String>
        var index = 0
        while(cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndex("_id")) == polarListD.get(pos).date){ // 붙여넣을 인덱스를 저장한다. 정렬에 필요
                index = cursor.getInt(cursor.getColumnIndex("polaroidIndex"))
                break
            }
        }

        cursor.moveToFirst()
        do{
            if(cursor.getString(cursor.getColumnIndex("_id")).equals(cutPolaroid)){ //만약 잘라낸 폴라로이드를 찾았다면, 인덱스를 변경해준다.
                arr = arrayOf(cursor.getString(cursor.getColumnIndex("_id")))
                isLastIndex = cursor.getInt(cursor.getColumnIndex("polaroidIndex"))

                if(isLastIndex==index-1){
                    Toast.makeText(context, "동일한 위치입니다. 잘라내기를 다시 진행해주세요.", Toast.LENGTH_SHORT).show()
                    cutinmode = false
                    break
                }else {
                    //잘라낼 폴라로이드를 앞이 아닌, 뒤로 보내는 경우 당기기가 필요함. 따라서 기준을 잘라낼 폴라로이드의 인덱스가 붙여넣을 인덱스보다 앞이라면.
                    if (cursor.getInt(cursor.getColumnIndex("polaroidIndex")) < index) {
                        pull = true
                        update.put("polaroidIndex", index-1)
                    }else {
                        update.put("polaroidIndex", index)
                    }
                    dontmoveIndex = cursor.getString(cursor.getColumnIndex("_id"))
                    databaseP.update("polaroid", update, "_id=?", arr)
                    break
                }
            }

        }while(cursor.moveToNext())


        if(cutinmode){
            //TODO 앞에 있는걸 뒤로 보낼 때 수정 필요함!!!!!!!!!!!!!!!!!!!!!!!!!!
            //인덱스 당기기 시작
            cursor = databaseP.rawQuery("SELECT * FROM polaroid", null)
            cursor.moveToFirst()

            if (pull) { //만약 당겨야한다면
                do {
                    if (cursor.getInt(cursor.getColumnIndex("polaroidIndex")) < index
                        && !(cursor.getString(cursor.getColumnIndex("_id")).equals(dontmoveIndex))
                    ) {
                        var downIn = cursor.getInt(cursor.getColumnIndex("polaroidIndex"))
                        arr = arrayOf(cursor.getString(cursor.getColumnIndex("_id")))
                        // Toast.makeText(context, cursor.getString(cursor.getColumnIndex("_id")), Toast.LENGTH_SHORT).show()
                        downIn--
                        update.clear()
                        update.put("polaroidIndex", downIn)
                        databaseP.update("polaroid", update, "_id=?", arr)
                    }
                } while (cursor.moveToNext())
                //인덱스 당기기 끝
            }else {
                cursor = databaseP.rawQuery("SELECT * FROM polaroid", null)
                cursor.moveToFirst()
                do {
                    if (cursor.getInt(cursor.getColumnIndex("polaroidIndex")) == polarCount - 1 && isLastIndex != polarCount - 1) {
                        break
                    }
                    if (cursor.getInt(cursor.getColumnIndex("polaroidIndex")) >= index
                        && !(cursor.getString(cursor.getColumnIndex("_id")).equals(dontmoveIndex))
                    ) {
                        var upIn = cursor.getInt(cursor.getColumnIndex("polaroidIndex"))
                        arr = arrayOf(cursor.getString(cursor.getColumnIndex("_id")))
                        // Toast.makeText(context, cursor.getString(cursor.getColumnIndex("_id")), Toast.LENGTH_SHORT).show()
                        upIn++
                        update.clear()
                        update.put("polaroidIndex", upIn)
                        databaseP.update("polaroid", update, "_id=?", arr)
                    }
                } while (cursor.moveToNext())
            }
            //인덱스 미루기 끝
            cutinmode = false
            Toast.makeText(context, "잘라내기 완료", Toast.LENGTH_SHORT).show()
            statelistener?.onItemState(2)
        }


        Log.d("detail","DB : $polarListD")
        Log.d("detail","data : $dataList")
    }


    fun setUpdate(dataList: List<PolaroidData>){
        val diffResult= DiffUtil.calculateDiff(PolaroidDiffUtil(this.dataList, dataList), false)
        diffResult.dispatchUpdatesTo(this)
        this.dataList=dataList
    }

    fun updateData(pos:Int){
        dataList= polarListD
        notifyDataSetChanged()
        Log.d("sleeping","notify called")
    }

}

class DetailHolder(itemView: View,adapter:AdapterDetailViewPager): RecyclerView.ViewHolder(itemView){

    interface OnItemStateListener{
        fun onItemState(state:Int, pos: Int, v: View)//0:삭제 1:....
    }
    var statelistener : OnItemStateListener? = null
    fun setOnItemStateListener(listener : OnItemStateListener) {
        this.statelistener = listener
    }

    //롱클릭이벤트 설정
    interface OnItemLongClickListener{
        fun onItemLongClick()
    }
    var longListener:OnItemLongClickListener?=null
    fun setOnItemLongClickListener(listener:OnItemLongClickListener){
        this.longListener=listener
    }

    fun bind(pos:Int,data:List<PolaroidData>) {
        Log.d("detail","바인드함수 호출됨 pos:$pos")
        val pageEndIndex = if (data.size % 4 == 0) data.size / 4 - 1 else data.size / 4
        val lastPageItemNum = if (data.size % 4 == 0) 4 else data.size % 4

        val pageStartPolaroidIndex = pos * 4
        when (pos) {
            pageEndIndex -> createPolaroid(lastPageItemNum, itemView.context, pos, data)
            in 0 until pageEndIndex -> {
                createPolaroid(4, itemView.context, pos, data)
            }
        }

    }


    //동적으로 폴라로이드 만들기
    fun createPolaroid(
        itemNum: Int,
        context: Context,
        dataIndex: Int,
        data: List<PolaroidData>
    ) {
        Log.d("detail","크리에이트 폴라로이드 호출됨")
        val viewArray = Array(itemNum, { ImageView(context) })
        val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        viewArray.forEachIndexed { index, view ->
            view.setId(dataIndex + index)
            lp.gravity=Gravity.TOP
            view.layoutParams = lp
            view.setPadding(10)
            /**나중에 다시보기**/
            view.setScaleType(ImageView.ScaleType.FIT_START)



            view.setImageBitmap(data.get(dataIndex*4 + index).imageC)


            when (index) {
                0, 1 -> {
                    itemView.findViewById<LinearLayout>(R.id.rowTop).addView(view)
                }
                2, 3 -> {
                    itemView.findViewById<LinearLayout>(R.id.rowBottom).addView(view)
                }
            }

            //내부 뷰 클릭이벤트
            view.setOnClickListener {
                if(deletemode){
                    statelistener?.onItemState(0, dataIndex*4 + index, itemView)
                }else if(cutoffmode){
                    statelistener?.onItemState(1, dataIndex*4 + index, itemView)
                }else if(cutinmode){
                    statelistener?.onItemState(2, dataIndex*4 + index, itemView)
                }
                else{
                    polarID = data.get(dataIndex*4+index).date
                    statelistener?.onItemState(9,dataIndex*4+index,itemView)
                }

            }

            //내부 뷰 롱클릭 이벤트
            view.setOnLongClickListener {
                longListener?.onItemLongClick()
                true
            }


        }

    }



}