package kr.co.lion.android01.firstusemvvmproject.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.lion.android01.firstusemvvmproject.FragmentMemoName
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.activity.LoginActivity
import kr.co.lion.android01.firstusemvvmproject.dao.MemoDao
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentShowMemoBinding
import kr.co.lion.android01.firstusemvvmproject.showDialog
import kr.co.lion.android01.firstusemvvmproject.viewModel.ShowMemoViewModel

class ShowMemoFragment : Fragment() {

    lateinit var fragmentShowMemoBinding: FragmentShowMemoBinding
    lateinit var loginActivity: LoginActivity

    lateinit var showMemoViewModel: ShowMemoViewModel

    //메모 idx깂
    var memoIdx = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentShowMemoBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_show_memo, container, false)
        showMemoViewModel = ShowMemoViewModel()
        fragmentShowMemoBinding.showMemoViewModel = showMemoViewModel
        fragmentShowMemoBinding.lifecycleOwner = this

        memoIdx = arguments?.getInt("memoIdx")!!

        loginActivity = activity as LoginActivity
        settingToolBar()
        setEvent()
        setView()


        return fragmentShowMemoBinding.root
    }

    //툴바 설정
    private fun settingToolBar(){
        fragmentShowMemoBinding.apply {
            materialToolbar7.apply {
                title = "메모 보기"
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    loginActivity.removeFragment(FragmentMemoName.SHOW_MEMO_FRAGMENT)
                }
            }
        }
    }

    //이벤트 설정
    private fun setEvent(){
        fragmentShowMemoBinding.apply {
            buttonModifyShowMemo.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("memoIdx", memoIdx)
                loginActivity.replaceFragment(FragmentMemoName.MODIFY_MEMO_FRAGMENT, true, true, bundle)

            }
            buttonDeleteShowMemo.setOnClickListener {
                loginActivity.showDialog("메모 삭제", "메모를 삭제하시겠습니까?"){ dialogInterface: DialogInterface, i: Int ->
                    viewLifecycleOwner.lifecycleScope.launch (Dispatchers.Main){
                        MemoDao.deleteUserMemo(memoIdx)
                        loginActivity.removeFragment(FragmentMemoName.SHOW_MEMO_FRAGMENT)
                    }
                }
            }
        }
    }

    //유효성 검사가 필요가 없구나..?

    //뷰 설정
    private fun setView(){
        fragmentShowMemoBinding.apply {
            //데배 하면 여기다가 집어넣자

            val job1 = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main){
                //메모 idx를 활용하여 값을 가져온다
                val memoInfo = MemoDao.gettingMemoByMemoIdx(memoIdx)

                showMemoViewModel!!.memoTitle.value = memoInfo?.memoTitle
                showMemoViewModel!!.memoDate.value = memoInfo?.date
                showMemoViewModel!!.memoContents.value = memoInfo?.memoContents
            }
        }
    }

}
















































