package kr.co.lion.android01.firstusemvvmproject.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchIdViewModel : ViewModel() {
    //아이디를 찾기 위한 휴대폰 번호
    var textSearchIdNumber = MutableLiveData<String>()

    //아이디를 출력
    var textShowUserId = MutableLiveData<String>()
}