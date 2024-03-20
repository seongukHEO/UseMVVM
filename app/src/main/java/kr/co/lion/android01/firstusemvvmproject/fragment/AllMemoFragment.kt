package kr.co.lion.android01.firstusemvvmproject.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.lion.android01.firstusemvvmproject.FragmentMemoName
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.activity.LoginActivity
import kr.co.lion.android01.firstusemvvmproject.dao.UserDao
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentAllMemoBinding
import kr.co.lion.android01.firstusemvvmproject.databinding.RowMainBinding
import kr.co.lion.android01.firstusemvvmproject.viewModel.AllMemoViewModel

class AllMemoFragment : Fragment() {

    lateinit var fragmentAllMemoBinding: FragmentAllMemoBinding
    lateinit var loginActivity: LoginActivity

    lateinit var allMemoViewModel: AllMemoViewModel

    //아이디 객체를 담을 변수
    var userId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentAllMemoBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_all_memo, container, false)
        allMemoViewModel = AllMemoViewModel()
        fragmentAllMemoBinding.allMemoViewModel = allMemoViewModel
        fragmentAllMemoBinding.lifecycleOwner = this

        userId = arguments?.getString("userId")!!

        loginActivity = activity as LoginActivity
        settingToolBar()
        initView()

        return fragmentAllMemoBinding.root
    }

    //툴바 설정
    private fun settingToolBar(){
        fragmentAllMemoBinding.apply {
            materialToolbar5.apply {

                val job1 = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main){
                    val userInfo = UserDao.getUserDataByUserId(userId)

                    title = "${userInfo?.name}님의 메모"

                    inflateMenu(R.menu.all_memo_menu)
                    setOnMenuItemClickListener {
                        when(it.itemId){
                            R.id.addition_memo -> {
                                loginActivity.replaceFragment(FragmentMemoName.INPUT_MEMO_FRAGMENT, true, true, null)
                            }
                            R.id.practice_menu -> {
                                loginActivity.replaceFragment(FragmentMemoName.SHOW_MEMO_FRAGMENT, true, true, null)
                            }
                        }

                        true
                    }
                }
            }
        }
    }

    private fun initView(){
        fragmentAllMemoBinding.apply {
            memoRecyclerView.apply {
                adapter = MemoRecyclerView()
                layoutManager = LinearLayoutManager(loginActivity)
                val deco = MaterialDividerItemDecoration(loginActivity, MaterialDividerItemDecoration.VERTICAL)
                addItemDecoration(deco)

            }
        }
    }

    inner class MemoRecyclerView : RecyclerView.Adapter<MemoRecyclerView.ViewHolderClass>(){

        inner class ViewHolderClass(rowMainBinding: RowMainBinding): RecyclerView.ViewHolder(rowMainBinding.root){
            var rowMainBinding:RowMainBinding

            init {
                this.rowMainBinding = rowMainBinding

                this.rowMainBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            var rowMainBinding = RowMainBinding.inflate(layoutInflater)
            var viewHolder = ViewHolderClass(rowMainBinding)
            return viewHolder
        }

        override fun getItemCount(): Int {
            return 100
        }

        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            holder.rowMainBinding.textViewtitleAllMemo.text = "메모"
            holder.rowMainBinding.textViewDateAllMemo.text = "2024/03/21"
        }
    }
}




















































