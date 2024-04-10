package kr.co.lion.android01.firstusemvvmproject.fragment

import android.animation.Animator
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.lion.android01.firstusemvvmproject.FragmentName
import kr.co.lion.android01.firstusemvvmproject.MainActivity
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.activity.LoginActivity
import kr.co.lion.android01.firstusemvvmproject.dao.UserDao
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentLoginBinding

import kr.co.lion.android01.firstusemvvmproject.model.UserModel
import kr.co.lion.android01.firstusemvvmproject.showDialog
import kr.co.lion.android01.firstusemvvmproject.showSoftInput

import kr.co.lion.android01.firstusemvvmproject.viewModel.LoginViewModel


class LoginFragment : Fragment() {

    lateinit var fragmentLoginBinding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    lateinit var loginViewModel: LoginViewModel

    //아이디 중복 검사
    var checkUserIdExist = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentLoginBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        loginViewModel = LoginViewModel()
        fragmentLoginBinding.loginViewModel = loginViewModel
        fragmentLoginBinding.lifecycleOwner = this

        mainActivity = activity as MainActivity
        //네이버 로그인 모듈 initalize
        val naverClientId = getString(R.string.naver_client_id)
        val naverClientSecret = getString(R.string.naver_client_secret)
        val naverClientName = getString(R.string.naver_client_name)
        NaverIdLoginSDK.initialize(mainActivity, naverClientId, naverClientSecret, naverClientName)


        settingToolBar()
        setEvent()
        return fragmentLoginBinding.root
    }

    override fun onResume() {
        super.onResume()
        fragmentLoginBinding.apply {
            loginViewModel!!.textMainId.value = ""
            loginViewModel!!.textMainPw.value = ""
        }
    }

    //툴바 설정
    private fun settingToolBar() {
        fragmentLoginBinding.apply {
            materialToolbar.apply {
                title = "메모 하세요~"
            }
        }
    }

    //이벤트 설정
    private fun setEvent() {
        fragmentLoginBinding.apply {
            //로그인 버튼
            buttonMainLogin.setOnClickListener {
                val chk = checkOK()
                if (chk == true) {
                    loginCheck()
                }

            }
            //회원가입 버튼
            buttonMainJoin.setOnClickListener {
                mainActivity.replaceFragment(FragmentName.JOIN_FRAGMENT, true, true, null)
            }
            //아이디 찾기 버튼
            buttonMainSearchId.setOnClickListener {
                mainActivity.replaceFragment(FragmentName.SEARCH_ID_FRAGMENT, true, true, null)
            }
            //비밀번호 찾기 버튼
            buttonMainSearchPW.setOnClickListener {
                mainActivity.replaceFragment(FragmentName.SEARCH_PW_FRAGMENT, true, true, null)
            }
            imageView3.setOnClickListener {
                kakaoLogin()
            }
            imageNaver.setOnClickListener {
                Toast.makeText(mainActivity, "dddd", Toast.LENGTH_SHORT).show()
                startNaverLogin()
            }
        }
    }


    // 유효성 검사
    private fun checkOK(): Boolean {
        fragmentLoginBinding.apply {
            val userId = loginViewModel!!.textMainId.value!!
            val userPw = loginViewModel!!.textMainPw.value!!

            if (userId.trim().isEmpty()) {
                mainActivity.showDialog(
                    "아이디 입력 오류",
                    "아이디를 입력해주세요"
                ) { dialogInterface: DialogInterface, i: Int ->
                    mainActivity.showSoftInput(textMainId, mainActivity)
                }
                return false
            }
            if (userPw.trim().isEmpty()) {
                mainActivity.showDialog(
                    "비밀번호 입력 오류",
                    "비밀번호를 입력해주세요"
                ) { dialogInterface: DialogInterface, i: Int ->
                    mainActivity.showSoftInput(textMainPW, mainActivity)
                }
                return false
            }
            return true
        }
    }

    //로그인한다
    private fun loginCheck() {
        fragmentLoginBinding.apply {

            var userId = loginViewModel!!.textMainId.value!!
            var userPw = loginViewModel!!.textMainPw.value!!

            val job1 = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                val loginUserModel = UserDao.getUserDataByUserId(userId)
                //아이디가 null이라면?
                if (loginUserModel == null) {
                    mainActivity.showDialog(
                        "로그인 오류",
                        "존재 하지 않는 아이디 입니다"
                    ) { dialogInterface: DialogInterface, i: Int ->
                        mainActivity.showSoftInput(textMainId, mainActivity)
                    }
                } else {
                    //아이디는 유효한데 비번이 틀릴경우
                    if (userPw != loginUserModel.userPw) {
                        mainActivity.showDialog(
                            "로그인 오류",
                            "존재 하지 않는 비밀번호 입니다"
                        ) { dialogInterface: DialogInterface, i: Int ->
                            mainActivity.showSoftInput(textMainPW, mainActivity)
                        }
                    } else {
                        lottieUse()
                    }
                }
            }
        }
    }

    //Lottie를 사용하자
    private fun lottieUse() {
        fragmentLoginBinding.apply {
            lottieLinearLayout.visibility = View.VISIBLE
            val userId = loginViewModel!!.textMainId.value!!

            lottieMain.repeatCount = 0
            lottieMain.loop(false)
            lottieMain.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    //애니메이션이 종료되면 이동
                    val newIntent = Intent(mainActivity, LoginActivity::class.java)
                    newIntent.putExtra("userId", userId)
                    startActivity(newIntent)
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }

            })
            lottieMain.playAnimation()
        }
    }

    fun kakaoLogin() {

        val TAG = "test1234"

        //KaKaoSdk 초기화
        KakaoSdk.init(mainActivity, "b18d5d5fe01c6450a1c7ae6a2dd79abf")

        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                //이 부분에는 로그인이 실패했을 때 처리해라
                Log.e(TAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                //이 부분에는 로그인에 성공했을 떄의 처리
                //주로 현재 액티비티를 종료하고 다른 액티비티로 이동함
                Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(mainActivity)) {
            UserApiClient.instance.loginWithKakaoTalk(mainActivity) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = callback)
                    Log.d("seong1234", Utility.getKeyHash(mainActivity))
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")

                    // 로그인한 사용자 정보를 가져온다.
                    // 이 때 accessToken 을 카카오 서버로 전달해야 해야하는데 알아서해준다.
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(TAG, "사용자 정보를 가져오는데 실패하였습니다", error)
                        } else if (user != null) {

                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {

                                checkUserIdExist = UserDao.checkUserIdExist(user.kakaoAccount?.email!!)

                                if (checkUserIdExist != false) {

                                    //사용자 번호 시퀀스 값을 가져온다
                                    val userSequence = UserDao.getUserSequence()
                                    //시퀀스 값을 1 증가시켜 덮어 씌운다
                                    UserDao.updateUserSequence(userSequence + 1)


                                    //저장할 데이터를 가져온다
                                    val userIdx = userSequence + 1
                                    val name = user.kakaoAccount?.profile?.nickname
                                    val number = user.kakaoAccount?.phoneNumber
                                    val userId = user.kakaoAccount?.email
                                    val userPw = user.id.toString()

                                    checkUserIdExist = UserDao.checkUserIdExist(userId!!)


                                    //저장할 데이터를 객체에 담는다
                                    val userModel =
                                        UserModel(userIdx, name!!, number!!, userId!!, userPw)

                                    //사용자 정보를 저장한다
                                    UserDao.insertUserData(userModel)

                                    mainActivity.showDialog(
                                        "가입 완료",
                                        "가입이 완료되었습니다"
                                    ) { dialogInterface: DialogInterface, i: Int ->
                                        val newIntent =
                                            Intent(mainActivity, LoginActivity::class.java)
                                        newIntent.putExtra("userId", userId)
                                        startActivity(newIntent)

                                    }
                                }else{
                                    val newIntent = Intent(mainActivity, LoginActivity::class.java)
                                    newIntent.putExtra("userId", user.kakaoAccount?.email)
                                    startActivity(newIntent)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = callback)
        }
    }

    //네이버 로그인
    private fun startNaverLogin(){

        var naverToken:String? = ""

        val profileCallback = object : NidProfileCallback<NidProfileResponse>{
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(mainActivity, "에러코드 : ${errorCode}" + "에러 이유 : ${errorDescription}", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(result: NidProfileResponse) {
                Toast.makeText(mainActivity, "성공", Toast.LENGTH_SHORT).show()
                Log.d("test1234", "토큰 : ${naverToken}")
                Log.d("test1234", "아이디 : ${result.profile?.id}")
                Log.d("test1234", "이메일 : ${result.profile?.email}")
                Log.d("test1234", "번호 : ${result.profile?.mobile}")
                Log.d("test1234", "이름 : ${result.profile?.name}")
                Log.d("test1234", "닉네임 : ${result.profile?.nickname}")
                Log.d("test1234", "몰라1 : ${result.profile?.ci}")
                Log.d("test1234", "몰라2 : ${result.profile?.encId}")
            }

        }

        val oauthLoginCallback = object : OAuthLoginCallback{
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(mainActivity, "에러코드 : ${errorCode}" + "에러 이유 : ${errorDescription}", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                naverToken = NaverIdLoginSDK.getAccessToken()

                NidOAuthLogin().callProfileApi(profileCallback)
            }

        }
        NaverIdLoginSDK.authenticate(mainActivity, oauthLoginCallback)

    }


}














































