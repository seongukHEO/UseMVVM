package kr.co.lion.android01.firstusemvvmproject.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    //아이디
    var textMainId = MutableLiveData<String>()

    //비번
    var textMainPw = MutableLiveData<String>()

}