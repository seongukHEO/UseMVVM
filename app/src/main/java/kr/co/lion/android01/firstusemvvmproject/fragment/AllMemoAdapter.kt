package kr.co.lion.android01.firstusemvvmproject.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.lion.android01.firstusemvvmproject.FragmentMemoName
import kr.co.lion.android01.firstusemvvmproject.dao.MemoDao
import kr.co.lion.android01.firstusemvvmproject.databinding.RowMainBinding
import kr.co.lion.android01.firstusemvvmproject.model.MemoModel

class AllMemoAdapter : RecyclerView.Adapter<ViewHolderClass>() {

    var memoList = listOf<MemoModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {

        val layoutInflater = LayoutInflater.from(parent.context)

        var rowMainBinding = RowMainBinding.inflate(layoutInflater)
        var viewHolder = ViewHolderClass(rowMainBinding)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        holder.rowMainBinding.textViewtitleAllMemo.text = "${memoList[position].memoTitle}"
        holder.rowMainBinding.textViewDateAllMemo.text = "${memoList[position].date}"
        holder.rowMainBinding.root.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putInt("memoIdx", memoList[position].memoIdx)
//            loginActivity.replaceFragment(FragmentMemoName.SHOW_MEMO_FRAGMENT, true, true, bundle)
        }
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    fun submitList(newMemo: List<MemoModel>){
        memoList = newMemo

        notifyDataSetChanged()
    }

}



class ViewHolderClass(rowMainBinding: RowMainBinding): RecyclerView.ViewHolder(rowMainBinding.root){
    var rowMainBinding: RowMainBinding

    init {
        this.rowMainBinding = rowMainBinding

        this.rowMainBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT

        )
    }
}