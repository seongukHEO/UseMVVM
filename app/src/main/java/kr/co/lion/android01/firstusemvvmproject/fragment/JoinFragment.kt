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
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentJoinBinding
import kr.co.lion.android01.firstusemvvmproject.viewModel.JoinViewModel

class JoinFragment : Fragment() {

    lateinit var fragmentJoinBinding:FragmentJoinBinding
    lateinit var mainActivity: MainActivity

    lateinit var joinViewModel:JoinViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //fragmentJoinBinding = FragmentJoinBinding.inflate(layoutInflater)
        fragmentJoinBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_join, container, false)
        joinViewModel = JoinViewModel()
        fragmentJoinBinding.joinViewModel = joinViewModel
        fragmentJoinBinding.lifecycleOwner = this

        mainActivity = activity as MainActivity
        settingToolBar()
        return fragmentJoinBinding.root
    }

    //툴바 설정
    private fun settingToolBar(){
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

    //
}























































