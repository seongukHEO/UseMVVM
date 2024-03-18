package kr.co.lion.android01.firstusemvvmproject.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.lion.android01.firstusemvvmproject.FragmentName
import kr.co.lion.android01.firstusemvvmproject.MainActivity
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.dao.UserDao
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentJoinBinding
import kr.co.lion.android01.firstusemvvmproject.hideSoftInput
import kr.co.lion.android01.firstusemvvmproject.model.UserModel
import kr.co.lion.android01.firstusemvvmproject.showDialog
import kr.co.lion.android01.firstusemvvmproject.showSoftInput
import kr.co.lion.android01.firstusemvvmproject.viewModel.JoinViewModel
import java.util.regex.Pattern

class JoinFragment : Fragment() {

    lateinit var fragmentJoinBinding: FragmentJoinBinding
    lateinit var mainActivity: MainActivity

    lateinit var joinViewModel: JoinViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //fragmentJoinBinding = FragmentJoinBinding.inflate(layoutInflater)
        fragmentJoinBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_join, container, false)
        joinViewModel = JoinViewModel()
        fragmentJoinBinding.joinViewModel = joinViewModel
        fragmentJoinBinding.lifecycleOwner = this

        mainActivity = activity as MainActivity
        settingToolBar()
        setEvent()
        setView()
        return fragmentJoinBinding.root
    }

    //툴바 설정
    private fun settingToolBar() {
        fragmentJoinBinding.apply {
            materialToolbar2.apply {
                title = "회원가입"
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(FragmentName.JOIN_FRAGMENT)
                }
            }
        }
    }

    //이벤트 설정
    private fun setEvent() {
        fragmentJoinBinding.apply {
            buttonJoin.setOnClickListener {
                val chk = checkEmptyText()
                if (chk == true) {
                    checkOK()
                    mainActivity.hideSoftInput(mainActivity)
                }
            }
        }
    }

    //화면 설정
    private fun setView() {
        fragmentJoinBinding.apply {

            //입력 요소 초기화
            joinViewModel!!.textJoinName.value = ""
            joinViewModel!!.textJoinNumber.value = ""
            joinViewModel!!.textJoinId.value = ""
            joinViewModel!!.textJoinPw.value = ""
            joinViewModel!!.textJoinCheckPw.value = ""


            //포커스 주기
            mainActivity.showSoftInput(textJoinName, mainActivity)

            //에러 해결
            textJoinName.addTextChangedListener {
                textJoinNameLayout.error = null
            }
            textJoinNumber.addTextChangedListener {
                textJoinNumberLayout.error = null
            }
            textJoinId.addTextChangedListener {
                textJoinIdLayout.error = null
            }
            textJoinPw.addTextChangedListener {
                textJoinPwLayout.error = null
            }
        }
    }

    //빈 TextField가 있는지 확인한다
    private fun checkEmptyText(): Boolean {
        fragmentJoinBinding.apply {
            var errorText: View? = null

            val name = joinViewModel!!.textJoinName.value!!
            val number = joinViewModel!!.textJoinNumber.value!!
            val userId = joinViewModel!!.textJoinId.value!!
            val userPw = joinViewModel!!.textJoinPw.value!!

            //이름
            if (name.trim().isEmpty()) {
                textJoinNameLayout.error = "이름을 입력해주세요"
                if (errorText == null) {
                    errorText = textJoinName
                } else {
                    textJoinNameLayout.error = null
                }
            }

            //핸드폰 번호
            if (number.trim().isEmpty()) {
                textJoinNumberLayout.error = "휴대폰 번호를 입력해주세요"
                if (errorText == null) {
                    errorText = textJoinNumber
                } else {
                    textJoinNumberLayout.error = null
                }
            }
            //아이디
            if (userId.trim().isEmpty()) {
                textJoinIdLayout.error = "아이디를 입력해주세요"
                if (errorText == null) {
                    errorText = textJoinId
                } else {
                    textJoinIdLayout.error = null
                }
            }
            //비밀번호
            if (userPw.trim().isEmpty()) {
                textJoinPwLayout.error = "비밀번호를 입력해주세요"
                if (errorText == null) {
                    errorText = textJoinPw
                } else {
                    textJoinPwLayout.error = null
                }
            }
            if (errorText != null) {
                mainActivity.showSoftInput(errorText, mainActivity)
                return false
            } else {
                return true
            }
        }
    }

    //중복 오류 체크
    private fun checkOK() {
        fragmentJoinBinding.apply {
            //val userId = joinViewModel!!.textJoinId.value!!
            val userPw = joinViewModel!!.textJoinPw.value!!
            val checkPw = joinViewModel!!.textJoinCheckPw.value!!

            //아이디는 중복 파이어 베이스를 이용한 중복 체크이므로 나중에!

            //비밀번호 특수문자 입력 받기
            val specialText = Pattern.compile("[!@#$%^&*)(+=.,;:]")
            val matchers = specialText.matcher(userPw)

            if (!matchers.find()) {
                textView.isVisible = true
                return
            } else if (userPw != checkPw) {
                mainActivity.showDialog(
                    "비밀번호 입력 오류",
                    "비밀번호가 틀립니다"
                ) { dialogInterface: DialogInterface, i: Int ->
                    mainActivity.showSoftInput(textJoinPw, mainActivity)
                }
                return
            }
            saveUserData()

        }
    }

    private fun saveUserData(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            //사용자 번호 시퀀스 값을 가져온다
            val userSequence = UserDao.getUserSequence()
            //시퀀스 값을 1 증가시켜 덮어 씌운다
            UserDao.updateUserSequence(userSequence + 1)

            //저장할 데이터를 가져온다
            val userIdx = userSequence + 1
            val name = joinViewModel.textJoinName.value!!
            val number = joinViewModel.textJoinNumber.value!!
            val userId = joinViewModel.textJoinId.value!!
            val userPw = joinViewModel.textJoinPw.value!!

            //저장할 데이터를 객체에 담는다
            val userModel = UserModel(userIdx, name, number, userId, userPw)

            //사용자 정보를 저장한다
            UserDao.insertUserData(userModel)

            mainActivity.showDialog("가입 완료", "가입이 완료되었습니다"){ dialogInterface: DialogInterface, i: Int ->
                mainActivity.removeFragment(FragmentName.JOIN_FRAGMENT)

            }
        }
    }
}























































