package kr.co.lion.android01.firstusemvvmproject.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.lion.android01.firstusemvvmproject.FragmentMemoName
import kr.co.lion.android01.firstusemvvmproject.MainActivity
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.activity.LoginActivity
import kr.co.lion.android01.firstusemvvmproject.dao.MemoDao
import kr.co.lion.android01.firstusemvvmproject.dao.UserDao
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentAllMemoBinding
import kr.co.lion.android01.firstusemvvmproject.databinding.RowMainBinding
import kr.co.lion.android01.firstusemvvmproject.model.MemoModel
import kr.co.lion.android01.firstusemvvmproject.showDialog
import kr.co.lion.android01.firstusemvvmproject.viewModel.AllMemoViewModel

class AllMemoFragment : Fragment() {

    lateinit var fragmentAllMemoBinding: FragmentAllMemoBinding
    lateinit var loginActivity: LoginActivity

    lateinit var allMemoViewModel: AllMemoViewModel


    //아이디 객체를 담을 변수
    var userId = ""

    // 어댑터
    val allMemoAdapter: AllMemoAdapter by lazy {
        val adapter = AllMemoAdapter()
        adapter.setRecyclerClickListener(object : AllMemoAdapter.ItemOnClickListener{
            override fun recyclerClickListener(position: Int) {
                val bundle = Bundle()
                bundle.putInt("memoIdx", position)
                loginActivity.replaceFragment(FragmentMemoName.SHOW_MEMO_FRAGMENT, true,true,bundle)
            }
        })
        adapter // setRecyclerClickListener 호출 후 어댑터 인스턴스 반환
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentAllMemoBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_all_memo, container, false)
        allMemoViewModel = AllMemoViewModel()
        fragmentAllMemoBinding.allMemoViewModel = allMemoViewModel
        fragmentAllMemoBinding.lifecycleOwner = this

        userId = arguments?.getString("userId")!!

        allMemoViewModel.gettingUserData(userId)

        loginActivity = activity as LoginActivity
        settingToolBar()
        initView()

        allMemoViewModel.memoList.observe(viewLifecycleOwner){
            allMemoAdapter.submitList(it)
        }

        settingRecyclerview()


        return fragmentAllMemoBinding.root
    }

    //viewmodel에 mutableLiveData / livedata선언
    //fragment에서 by viewModels로 뷰모델가져와서 선언해줌.
    //fragment에서 viewModel.memoList.observe 시작
    //viewModel에서 api나 room 통해서 데이터가져와서 mutableLivedata에 넣어줌.
    //observing하고있다가 새로운 데이터 들어오면 adapter에 submitList해줌.




    //툴바 설정
    private fun settingToolBar(){
        fragmentAllMemoBinding.apply {
            materialToolbar5.apply {

                val job1 = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main){
                    val userInfo = UserDao.getUserDataByUserId(userId)

                    title = "${userInfo?.name}님의 메모"

                    inflateMenu(R.menu.all_memo_menu)
                    setOnMenuItemClickListener {
                        when(it.itemId){
                            R.id.addition_memo -> {
                                val bundle = Bundle()
                                bundle.putString("userId", userId)
                                loginActivity.replaceFragment(FragmentMemoName.INPUT_MEMO_FRAGMENT, true, true, bundle)
                            }
                            R.id.practice_menu -> {
                                loginActivity.showDialog("회원 탈퇴", "회원탈퇴를 하시겠습니까?\n입력한 메모가 전부 삭제됩니다"){ dialogInterface: DialogInterface, i: Int ->
                                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                                        MemoDao.deleteUserMemoByUserId(userId)
                                        MemoDao.deleteUserInfo(userId)
                                        loginActivity.finish()
                                    }
                                }
                            }
                        }

                        true
                    }
                }
            }
        }
    }


    private fun initView(){
        fragmentAllMemoBinding.apply {
            memoRecyclerView.apply {
                
                adapter = allMemoAdapter
                layoutManager = LinearLayoutManager(loginActivity)
                val deco = MaterialDividerItemDecoration(loginActivity, MaterialDividerItemDecoration.VERTICAL)
                addItemDecoration(deco)

            }
        }
    }

    private fun settingRecyclerview(){

    }

}




















































