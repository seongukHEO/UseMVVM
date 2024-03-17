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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.lion.android01.firstusemvvmproject.FragmentName
import kr.co.lion.android01.firstusemvvmproject.MainActivity
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.activity.LoginActivity
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

    //툴바 설정
    private fun settingToolBar(){
        fragmentLoginBinding.apply {
            materialToolbar.apply {
                title = "메모합니다~"
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
                    lottieUse()
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

    //Lottie를 사용하자
    private fun lottieUse(){
        fragmentLoginBinding.apply {
            lottieLinearLayout.visibility = View.VISIBLE

            lottieMain.repeatCount = 0
            lottieMain.loop(false)
            lottieMain.addAnimatorListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    //애니메이션이 종료되면 이동
                    val newIntent = Intent(mainActivity, LoginActivity::class.java)
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














































