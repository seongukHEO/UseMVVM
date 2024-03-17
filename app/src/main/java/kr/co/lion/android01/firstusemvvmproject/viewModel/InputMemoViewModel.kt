package kr.co.lion.android01.firstusemvvmproject.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InputMemoViewModel : ViewModel() {
    //메모 제목
    var memoTitle = MutableLiveData<String>()

    //메모 내용
    var memoContents = MutableLiveData<String>()

}