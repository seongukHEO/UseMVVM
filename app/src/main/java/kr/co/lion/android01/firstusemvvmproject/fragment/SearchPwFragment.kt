package kr.co.lion.android01.firstusemvvmproject.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import kr.co.lion.android01.firstusemvvmproject.FragmentName
import kr.co.lion.android01.firstusemvvmproject.MainActivity
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentSearchPwBinding
import kr.co.lion.android01.firstusemvvmproject.hideSoftInput
import kr.co.lion.android01.firstusemvvmproject.showDialog
import kr.co.lion.android01.firstusemvvmproject.showSoftInput
import kr.co.lion.android01.firstusemvvmproject.viewModel.SearchPwViewModel

class SearchPwFragment : Fragment() {

    lateinit var fragmentSearchPwBinding: FragmentSearchPwBinding
    lateinit var mainActivity: MainActivity

    lateinit var searchPwViewModel: SearchPwViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentSearchPwBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_search_pw, container, false)
        searchPwViewModel = SearchPwViewModel()
        fragmentSearchPwBinding.searchPwViewModel = searchPwViewModel
        fragmentSearchPwBinding.lifecycleOwner = this

        mainActivity = activity as MainActivity
        settingToolBar()
        setView()
        setEvent()

        return fragmentSearchPwBinding.root
    }

    //툴바 설정
    private fun settingToolBar(){
        fragmentSearchPwBinding.apply {
            materialToolbar4.apply {
                title = "비밀번호를 찾아볼까요~?"
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(FragmentName.SEARCH_PW_FRAGMENT)
                }
            }
        }
    }


    //이벤트 설정
    private fun setEvent(){
        fragmentSearchPwBinding.apply {
            buttonSearchPW.setOnClickListener {
                val chk = checkOK()
                if (chk == true){
                    mainActivity.removeFragment(FragmentName.SEARCH_PW_FRAGMENT)
                    mainActivity.hideSoftInput(mainActivity)
                }
            }
        }
    }

    //화면 설정
    private fun setView(){
        fragmentSearchPwBinding.apply {
            searchPwViewModel!!.textSearchPwUserid.value = ""
            searchPwViewModel!!.textSearchPWNumber.value = ""

            mainActivity.showSoftInput(textSearchPwUserid, mainActivity)
        }
    }

    //유효성 검사
    private fun checkOK():Boolean{
        fragmentSearchPwBinding.apply {
            val userId = searchPwViewModel?.textSearchPwUserid?.value!!
            val number = searchPwViewModel?.textSearchPWNumber?.value!!

            if (userId.trim().isEmpty()){
                mainActivity.showDialog("아이디 입력 오류", "아이디를 입력해주세요"){ dialogInterface: DialogInterface, i: Int ->
                    mainActivity.showSoftInput(textSearchPwUserid, mainActivity)
                }
                return false
            }
            if (number.isEmpty()){
                mainActivity.showDialog("휴대폰 번호 입력 오류", "휴대폰 번호를 입력해주세요"){ dialogInterface: DialogInterface, i: Int ->
                    mainActivity.showSoftInput(textSearchPwNumber, mainActivity)
                }
                return false
            }
            return true
        }
    }
}



























































