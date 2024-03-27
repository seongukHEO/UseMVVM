package kr.co.lion.android01.firstusemvvmproject.fragment

import android.animation.Animator
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.lion.android01.firstusemvvmproject.FragmentName
import kr.co.lion.android01.firstusemvvmproject.MainActivity
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.activity.LoginActivity
import kr.co.lion.android01.firstusemvvmproject.dao.UserDao
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentLoginBinding
import kr.co.lion.android01.firstusemvvmproject.showDialog
import kr.co.lion.android01.firstusemvvmproject.showSoftInput

import kr.co.lion.android01.firstusemvvmproject.viewModel.LoginViewModel



class LoginFragment : Fragment() {

    lateinit var fragmentLoginBinding:FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false )
        loginViewModel = LoginViewModel()
        fragmentLoginBinding.loginViewModel = loginViewModel
        fragmentLoginBinding.lifecycleOwner = this

        mainActivity = activity as MainActivity
        settingToolBar()
        setEvent()
        return fragmentLoginBinding.root
    }

    override fun onResume() {
        super.onResume()
        fragmentLoginBinding.apply {
            loginViewModel!!.textMainId.value = ""
            loginViewModel!!.textMainPw.value = ""
        }
    }

    //툴바 설정
    private fun settingToolBar(){
        fragmentLoginBinding.apply {
            materialToolbar.apply {
                title = "메모 하세요~"
            }
        }
    }

    //이벤트 설정
    private fun setEvent(){
        fragmentLoginBinding.apply {
            //로그인 버튼
            buttonMainLogin.setOnClickListener {
                val chk = checkOK()
                if (chk == true){
                    loginCheck()
                }

            }
            //회원가입 버튼
            buttonMainJoin.setOnClickListener {
                mainActivity.replaceFragment(FragmentName.JOIN_FRAGMENT, true, true, null)
            }
            //아이디 찾기 버튼
            buttonMainSearchId.setOnClickListener {
                mainActivity.replaceFragment(FragmentName.SEARCH_ID_FRAGMENT, true, true, null)
            }
            //비밀번호 찾기 버튼
            buttonMainSearchPW.setOnClickListener {
                mainActivity.replaceFragment(FragmentName.SEARCH_PW_FRAGMENT, true, true, null)
            }
        }
    }


    // 유효성 검사
    private fun checkOK():Boolean{
        fragmentLoginBinding.apply {
            val userId = loginViewModel!!.textMainId.value!!
            val userPw = loginViewModel!!.textMainPw.value!!

            if (userId.trim().isEmpty()){
                mainActivity.showDialog("아이디 입력 오류", "아이디를 입력해주세요"){ dialogInterface: DialogInterface, i: Int ->
                    mainActivity.showSoftInput(textMainId, mainActivity)
                }
                return false
            }
            if (userPw.trim().isEmpty()){
                mainActivity.showDialog("비밀번호 입력 오류", "비밀번호를 입력해주세요"){ dialogInterface: DialogInterface, i: Int ->
                    mainActivity.showSoftInput(textMainPW, mainActivity)
                }
                return false
            }
            return true
        }
    }

    //로그인한다
    private fun loginCheck(){
        fragmentLoginBinding.apply {

            var userId = loginViewModel!!.textMainId.value!!
            var userPw = loginViewModel!!.textMainPw.value!!

            val job1 = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                val loginUserModel = UserDao.getUserDataByUserId(userId)
                //아이디가 null이라면?
                if (loginUserModel == null){
                    mainActivity.showDialog("로그인 오류", "존재 하지 않는 아이디 입니다"){ dialogInterface: DialogInterface, i: Int ->
                        mainActivity.showSoftInput(textMainId, mainActivity)
                    }
                }else{
                    //아이디는 유효한데 비번이 틀릴경우
                    if (userPw != loginUserModel.userPw){
                        mainActivity.showDialog("로그인 오류", "존재 하지 않는 비밀번호 입니다"){ dialogInterface: DialogInterface, i: Int ->
                            mainActivity.showSoftInput(textMainPW, mainActivity)
                        }
                    }else{
                        lottieUse()
                    }
                }
            }
        }
    }

    //Lottie를 사용하자
    private fun lottieUse(){
        fragmentLoginBinding.apply {
            lottieLinearLayout.visibility = View.VISIBLE
            val userId = loginViewModel!!.textMainId.value!!

            lottieMain.repeatCount = 0
            lottieMain.loop(false)
            lottieMain.addAnimatorListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    //애니메이션이 종료되면 이동
                    val newIntent = Intent(mainActivity, LoginActivity::class.java)
                    newIntent.putExtra("userId", userId)
                    startActivity(newIntent)
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }

            })
            lottieMain.playAnimation()
        }
    }

}














































