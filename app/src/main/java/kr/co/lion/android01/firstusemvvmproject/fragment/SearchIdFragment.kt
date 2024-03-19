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
import kr.co.lion.android01.firstusemvvmproject.FragmentName
import kr.co.lion.android01.firstusemvvmproject.MainActivity
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.dao.UserDao
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentSearchIdBinding
import kr.co.lion.android01.firstusemvvmproject.hideSoftInput
import kr.co.lion.android01.firstusemvvmproject.model.UserModel
import kr.co.lion.android01.firstusemvvmproject.showDialog
import kr.co.lion.android01.firstusemvvmproject.showSoftInput
import kr.co.lion.android01.firstusemvvmproject.viewModel.SearchIdViewModel

class SearchIdFragment : Fragment() {

    lateinit var fragmentSearchIdBinding: FragmentSearchIdBinding
    lateinit var mainActivity: MainActivity

    lateinit var searchIdViewModel: SearchIdViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentSearchIdBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_search_id, container, false)
        searchIdViewModel = SearchIdViewModel()
        fragmentSearchIdBinding.searchIdViewModel = searchIdViewModel
        fragmentSearchIdBinding.lifecycleOwner = this

        mainActivity = activity as MainActivity

        settingToolBar()
        setEvent()
        settingView()

        return fragmentSearchIdBinding.root
    }

    //툴바 설정
    private fun settingToolBar(){
        fragmentSearchIdBinding.apply {
            materialToolbar3.apply {
                title = "아이디를 찾아봅시다~!"
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(FragmentName.SEARCH_ID_FRAGMENT)
                }
            }
        }
    }

    //이벤트 설정
    private fun setEvent(){
        fragmentSearchIdBinding.apply {
            buttonSearchId.setOnClickListener {
                checkOK()
            }
        }
    }

    //화면 뷰 설정
    private fun settingView(){
        fragmentSearchIdBinding.apply {
            searchIdViewModel!!.textSearchIdNumber.value = ""

            mainActivity.showSoftInput(textSearchIdNumber, mainActivity)
        }
    }

    //유효성 검사
    private fun checkOK(){
        fragmentSearchIdBinding.apply {
            val number = searchIdViewModel!!.textSearchIdNumber.value!!

            if (number.trim().isEmpty()){
                mainActivity.showDialog("휴대폰 번호 입력 오류", "휴대폰 번호를 입력해주세요"){ dialogInterface: DialogInterface, i: Int ->
                    mainActivity.showSoftInput(textSearchIdNumber, mainActivity)
                }
                return
            }
            //데이터가 있는지 없는지는 나중에 체크한다
            val job1 = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main){
                val searchIdNumber = UserDao.getUserDataByNumber(number)

                if (searchIdNumber == null){
                    mainActivity.showDialog("휴대폰 번호 입력 오류", "휴대폰 번호로 등록된 아이디가 없습니다"){ dialogInterface: DialogInterface, i: Int ->
                        searchIdViewModel!!.textSearchIdNumber.value = ""
                        mainActivity.showSoftInput(textSearchIdNumber, mainActivity)
                    }
                }else{
                    textShowUserId.text = "아이디 : ${searchIdNumber.userId}"
                }
            }
        }
    }
}
























































