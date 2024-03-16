package kr.co.lion.android01.firstusemvvmproject.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchPwViewModel : ViewModel() {
    //아이디
    var textSearchPwUserid = MutableLiveData<String>()

    //휴대폰 번호
    var textSearchPWNumber = MutableLiveData<String>()

    //비밀번호를 보이게 한다
    var textShowUserPw = MutableLiveData<String>()
}