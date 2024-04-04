package kr.co.lion.android01.firstusemvvmproject.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.lion.android01.firstusemvvmproject.dao.MemoDao
import kr.co.lion.android01.firstusemvvmproject.model.MemoModel

class AllMemoViewModel :ViewModel() {

    private val _memoList:MutableLiveData<List<MemoModel>> = MutableLiveData(emptyList())
    val memoList: LiveData<List<MemoModel>> = _memoList



    fun gettingUserData(userId:String){
        viewModelScope.launch(Dispatchers.IO){
            //서버에서 데이터를 가져온다
            _memoList.postValue(MemoDao.gettingMemoList(userId))


        }
    }
}