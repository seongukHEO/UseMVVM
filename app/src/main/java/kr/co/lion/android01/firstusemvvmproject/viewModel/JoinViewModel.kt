package kr.co.lion.android01.firstusemvvmproject.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class JoinViewModel : ViewModel() {
    //이름
    var textJoinName = MutableLiveData<String>()
    //휴대폰 번호
    var textJoinNumber = MutableLiveData<String>()
    //아이디
    var textJoinId = MutableLiveData<String>()
    //비밀번호
    var textJoinPw = MutableLiveData<String>()
    //비밀번호 확인
    var textJoinCheckPw = MutableLiveData<String>()
}