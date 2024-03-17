package kr.co.lion.android01.firstusemvvmproject.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import kr.co.lion.android01.firstusemvvmproject.FragmentMemoName
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.activity.LoginActivity
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentAllMemoBinding
import kr.co.lion.android01.firstusemvvmproject.viewModel.AllMemoViewModel

class AllMemoFragment : Fragment() {

    lateinit var fragmentAllMemoBinding: FragmentAllMemoBinding
    lateinit var loginActivity: LoginActivity

    lateinit var allMemoViewModel: AllMemoViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentAllMemoBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_all_memo, container, false)
        allMemoViewModel = AllMemoViewModel()
        fragmentAllMemoBinding.allMemoViewModel = allMemoViewModel
        fragmentAllMemoBinding.lifecycleOwner = this

        loginActivity = activity as LoginActivity
        settingToolBar()

        return fragmentAllMemoBinding.root
    }

    //툴바 설정
    private fun settingToolBar(){
        fragmentAllMemoBinding.apply {
            materialToolbar5.apply {
                title = "나의 메모"

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

    inner class RecyclerView{

        inner class ViewHolderClass()
    }
}




















































