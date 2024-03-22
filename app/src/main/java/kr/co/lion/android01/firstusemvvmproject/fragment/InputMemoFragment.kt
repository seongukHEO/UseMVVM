package kr.co.lion.android01.firstusemvvmproject.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.lion.android01.firstusemvvmproject.FragmentMemoName
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.activity.LoginActivity
import kr.co.lion.android01.firstusemvvmproject.dao.MemoDao
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentInputMemoBinding
import kr.co.lion.android01.firstusemvvmproject.hideSoftInput
import kr.co.lion.android01.firstusemvvmproject.model.MemoModel
import kr.co.lion.android01.firstusemvvmproject.showDialog
import kr.co.lion.android01.firstusemvvmproject.showSoftInput
import kr.co.lion.android01.firstusemvvmproject.viewModel.InputMemoViewModel
import java.text.SimpleDateFormat
import java.util.Date

class InputMemoFragment : Fragment() {

    lateinit var fragmentInputMemoBinding: FragmentInputMemoBinding
    lateinit var loginActivity: LoginActivity

    lateinit var inputMemoViewModel: InputMemoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentInputMemoBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_input_memo, container, false)
        inputMemoViewModel = InputMemoViewModel()
        fragmentInputMemoBinding.inputMemoViewModel = inputMemoViewModel
        fragmentInputMemoBinding.lifecycleOwner = this

        loginActivity = activity as LoginActivity
        settingToolBar()
        setEvent()
        settingView()

        return fragmentInputMemoBinding.root
    }

    //툴바 설정
    private fun settingToolBar(){
        fragmentInputMemoBinding.apply {
            materialToolbar6.apply {
                title = "메모 입력"
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    loginActivity.removeFragment(FragmentMemoName.INPUT_MEMO_FRAGMENT)
                }
            }
        }
    }

    //이벤트 설정
    private fun setEvent(){
        fragmentInputMemoBinding.apply {
            buttonAllMemo.setOnClickListener {
                val chk = checkOK()
                if (chk == true){
                    saveMemo()
                }
            }
        }
    }

    //뷰 설정
    private fun settingView(){
        fragmentInputMemoBinding.apply {
            inputMemoViewModel!!.memoTitle.value = ""
            inputMemoViewModel!!.memoContents.value = ""
            //포커스를 준다
            loginActivity.showSoftInput(textTitleAllMemo, loginActivity)
        }
    }


    //유효성 검사
    private fun checkOK():Boolean{
        fragmentInputMemoBinding.apply {
            val title = inputMemoViewModel?.memoTitle?.value!!
            val contents = inputMemoViewModel?.memoContents?.value!!

            if (title.trim().isEmpty()){
                loginActivity.showDialog("제목 입력 오류", "제목을 입력해주세요"){ dialogInterface: DialogInterface, i: Int ->
                    loginActivity.showSoftInput(textTitleAllMemo, loginActivity)
                }
                return false
            }
            if (contents.trim().isEmpty()){
                loginActivity.showDialog("내용 입력 오류", "내용을 입력해주세요"){ dialogInterface: DialogInterface, i: Int ->
                    loginActivity.showSoftInput(textContentsAllMemo, loginActivity)
                }
                return false
            }

            return true
        }
    }

    //저장처리
    private fun saveMemo(){

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main){
            //게시글 시퀀스 값을 업데이트한다
            val memoSequence = MemoDao.getUserSequence()
            //시쿼스 값을 업데이트 한다
            MemoDao.updateSequence(memoSequence + 1)

            //업로드 할 정보를 담아준다
            val memoIdx = memoSequence + 1
            val memoTitle = inputMemoViewModel.memoTitle.value!!
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = simpleDateFormat.format(Date())
            val memoContents = inputMemoViewModel.memoContents.value!!

            val memoModel = MemoModel(memoIdx, memoTitle, date, memoContents)
            MemoDao.insertUserData(memoModel)

            loginActivity.removeFragment(FragmentMemoName.INPUT_MEMO_FRAGMENT)
            loginActivity.hideSoftInput(loginActivity)
        }
    }
}






































