package kr.co.lion.android01.firstusemvvmproject.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import kr.co.lion.android01.firstusemvvmproject.FragmentName
import kr.co.lion.android01.firstusemvvmproject.MainActivity
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentLoginBinding

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

}

















































