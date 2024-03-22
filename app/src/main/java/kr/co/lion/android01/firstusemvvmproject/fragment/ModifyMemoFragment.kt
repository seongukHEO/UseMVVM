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
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentModifyMemoBinding
import kr.co.lion.android01.firstusemvvmproject.hideSoftInput
import kr.co.lion.android01.firstusemvvmproject.model.MemoModel
import kr.co.lion.android01.firstusemvvmproject.showDialog
import kr.co.lion.android01.firstusemvvmproject.showSoftInput
import kr.co.lion.android01.firstusemvvmproject.viewModel.ModifyMemoViewModel

class ModifyMemoFragment : Fragment() {

    lateinit var fragmentModifyMemoBinding: FragmentModifyMemoBinding
    lateinit var loginActivity: LoginActivity

    lateinit var modifyMemoViewModel: ModifyMemoViewModel

    var memoIdx = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentModifyMemoBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_modify_memo, container, false)
        modifyMemoViewModel = ModifyMemoViewModel()
        fragmentModifyMemoBinding.modifyMemoViewModel = modifyMemoViewModel
        fragmentModifyMemoBinding.lifecycleOwner = this

        memoIdx = arguments?.getInt("memoIdx")!!

        loginActivity = activity as LoginActivity
        settingToolBar()
        settingView()
        setEvent()

        return fragmentModifyMemoBinding.root
    }

    //툴바 설정
    private fun settingToolBar(){
        fragmentModifyMemoBinding.apply {
            materialToolbar8.apply {
                title = "메모 수정"
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    loginActivity.removeFragment(FragmentMemoName.MODIFY_MEMO_FRAGMENT)
                }
            }
        }
    }

    //뷰설정
    private fun settingView(){
        fragmentModifyMemoBinding.apply {

            val job1 = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main){
                var memoInfo = MemoDao.gettingMemoByMemoIdx(memoIdx)

                modifyMemoViewModel!!.userId.value = memoInfo?.userId
                modifyMemoViewModel!!.memoTitle.value = memoInfo?.memoTitle
                modifyMemoViewModel!!.memoDate.value = memoInfo?.date
                modifyMemoViewModel!!.memoContents.value = memoInfo?.memoContents

                //포커스를 준다
                loginActivity.showSoftInput(textModifyTitle, loginActivity)
            }
        }
    }

    //이벤트 설정
    private fun setEvent(){
        fragmentModifyMemoBinding.apply {
            buttonModify.setOnClickListener {
                val chk = checkOK()
                if (chk == true){
                    loginActivity.showDialog("메모 수정", "메모를 수정하시겠습니까?"){ dialogInterface: DialogInterface, i: Int ->
                        updateMemoData()
                        loginActivity.removeFragment(FragmentMemoName.MODIFY_MEMO_FRAGMENT)
                        loginActivity.removeFragment(FragmentMemoName.SHOW_MEMO_FRAGMENT)
                        loginActivity.hideSoftInput(loginActivity)
                    }
                }
            }
        }
    }

    //유효성 검사
    private fun checkOK():Boolean{
        fragmentModifyMemoBinding.apply {
            val title = modifyMemoViewModel!!.memoTitle.value!!
            val contents = modifyMemoViewModel!!.memoContents.value!!

            if (title.trim().isEmpty()){
                loginActivity.showDialog("제목 입력 오류", "제목을 입력해주세요"){ dialogInterface: DialogInterface, i: Int ->
                    loginActivity.showSoftInput(textModifyTitle, loginActivity)
                }
                return false
            }

            if (contents.trim().isEmpty()){
                loginActivity.showDialog("내용 입력 오류", "내용을 입력해주세요"){ dialogInterface: DialogInterface, i: Int ->
                    loginActivity.showSoftInput(textModifyContents, loginActivity)
                }
                return false
            }
            return true
        }
    }

    //메모 정보를 업데이트 한다
    private fun updateMemoData(){
        fragmentModifyMemoBinding.apply {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                var idx = memoIdx
                var userId = modifyMemoViewModel!!.userId.value!!
                var memoTitle = modifyMemoViewModel!!.memoTitle.value!!
                var date = modifyMemoViewModel!!.memoDate.value!!
                var memoContents = modifyMemoViewModel!!.memoContents.value!!

                var memoModel = MemoModel(idx, userId ,memoTitle, date, memoContents)

                MemoDao.updateUserMemo(memoIdx, memoModel)
            }
        }
    }
}











































