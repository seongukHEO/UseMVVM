package kr.co.lion.android01.firstusemvvmproject.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ModifyMemoViewModel : ViewModel() {
    //아이디
    var userId = MutableLiveData<String>()

    //메모 제목
    val memoTitle = MutableLiveData<String>()

    //작성 날짜
    val memoDate = MutableLiveData<String>()

    //메모 내용
    val memoContents = MutableLiveData<String>()

    //
}