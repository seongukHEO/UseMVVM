package kr.co.lion.android01.firstusemvvmproject.fragment

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.lion.android01.firstusemvvmproject.Data
import kr.co.lion.android01.firstusemvvmproject.Data.Companion.getDegree
import kr.co.lion.android01.firstusemvvmproject.Data.Companion.resizeBitmap
import kr.co.lion.android01.firstusemvvmproject.Data.Companion.rotateBitmap
import kr.co.lion.android01.firstusemvvmproject.FragmentMemoName
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.activity.LoginActivity
import kr.co.lion.android01.firstusemvvmproject.dao.MemoDao
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentInputMemoBinding
import kr.co.lion.android01.firstusemvvmproject.hideSoftInput
import kr.co.lion.android01.firstusemvvmproject.model.MemoModel
import kr.co.lion.android01.firstusemvvmproject.showDialog
import kr.co.lion.android01.firstusemvvmproject.showSoftInput
import kr.co.lion.android01.firstusemvvmproject.viewModel.InputMemoViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class InputMemoFragment : Fragment() {

    lateinit var fragmentInputMemoBinding: FragmentInputMemoBinding
    lateinit var loginActivity: LoginActivity

    //촬영된 사진이 저장된 경로 정보를 가지고 있는 Uri 객체
    lateinit var contentUri:Uri

    lateinit var inputMemoViewModel: InputMemoViewModel

    lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    lateinit var albumLauncher: ActivityResultLauncher<Intent>

    //아이디 객체를 담은 변수
    var userId2 = ""

    //이미지를 첨부한 적이 있는지
    var isAddPicture = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentInputMemoBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_input_memo, container, false)
        inputMemoViewModel = InputMemoViewModel()
        fragmentInputMemoBinding.inputMemoViewModel = inputMemoViewModel
        fragmentInputMemoBinding.lifecycleOwner = this

        userId2 = arguments?.getString("userId")!!

        loginActivity = activity as LoginActivity
        settingView()
        settingToolBar()
        settingCameraLauncher()
        settingAlbumLauncher()



        return fragmentInputMemoBinding.root
    }

    //툴바 설정
    private fun settingToolBar(){
        fragmentInputMemoBinding.apply {
            materialToolbar6.apply {
                title = "메모 입력"
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    loginActivity.removeFragment(FragmentMemoName.INPUT_MEMO_FRAGMENT)
                }
                inflateMenu(R.menu.input_menu)
                setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.camera_input_menu -> {
                            startCamera()

                        }
                        R.id.album_input_menu -> {
                            startAlbumLauncher()
                        }
                        R.id.done_input_menu -> {
                            val chk = checkOK()
                            if (chk == true){
                                saveMemo()
                            }
                        }
                    }

                    true
                }
            }
        }
    }



    //뷰 설정
    private fun settingView(){
        fragmentInputMemoBinding.apply {
            inputMemoViewModel!!.memoTitle.value = ""
            inputMemoViewModel!!.memoContents.value = ""
            //포커스를 준다
            loginActivity.showSoftInput(textInputUserid, loginActivity)
            isAddPicture = false
        }
    }


    //유효성 검사
    private fun checkOK():Boolean{
        fragmentInputMemoBinding.apply {
            val userId = inputMemoViewModel?.userId?.value
            val title = inputMemoViewModel?.memoTitle?.value
            val contents = inputMemoViewModel?.memoContents?.value

            if (userId2 != userId){
                loginActivity.showDialog("아이디 오류", "아이디가 일치 하지 않습니다"){ dialogInterface: DialogInterface, i: Int ->
                    loginActivity.showSoftInput(textInputUserid, loginActivity)
                }
                return false
            }

            if (title!!.trim().isEmpty()){
                loginActivity.showDialog("제목 입력 오류", "제목을 입력해주세요"){ dialogInterface: DialogInterface, i: Int ->
                    loginActivity.showSoftInput(textTitleAllMemo, loginActivity)
                }
                return false
            }
            if (contents!!.trim().isEmpty()){
                loginActivity.showDialog("내용 입력 오류", "내용을 입력해주세요"){ dialogInterface: DialogInterface, i: Int ->
                    loginActivity.showSoftInput(textContentsAllMemo, loginActivity)
                }
                return false
            }

            return true
        }
    }

    //저장처리
    private fun saveMemo(){

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main){

            // 서버에서의 첨부 이미지 파일 이름
            var serverFileName:String? = null

            if (isAddPicture == true){
                //이미지 뷰의 이미지 데이터를 파일로 저장한다
                Data.saveImageViewData(loginActivity, fragmentInputMemoBinding.imageView, "uploadTemp.jpg")
                //서버에서의 파일 이름
                //시간 단위로 해야 겹치지 않는다
                serverFileName = "image_${System.currentTimeMillis()}.jpg"
                //서버로 업로드한다
                MemoDao.uploadImage(loginActivity, "uploagTemp.jpg", serverFileName)
            }


            //게시글 시퀀스 값을 업데이트한다
            val memoSequence = MemoDao.getUserSequence()
            //시쿼스 값을 업데이트 한다
            MemoDao.updateSequence(memoSequence + 1)

            //업로드 할 정보를 담아준다
            val memoIdx = memoSequence + 1
            val userId = inputMemoViewModel.userId.value!!
            val memoTitle = inputMemoViewModel.memoTitle.value!!
            val image = serverFileName
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = simpleDateFormat.format(Date())
            val memoContents = inputMemoViewModel.memoContents.value!!

            val memoModel = MemoModel(memoIdx, userId,memoTitle, image, date, memoContents)
            MemoDao.insertUserData(memoModel)

            loginActivity.removeFragment(FragmentMemoName.INPUT_MEMO_FRAGMENT)
            loginActivity.hideSoftInput(loginActivity)
        }
    }

    //카메라 런쳐 설정
    private fun settingCameraLauncher() {
        val contract1 = ActivityResultContracts.StartActivityForResult()
        cameraLauncher = registerForActivityResult(contract1) {
            //갔다가 돌아왔을 때
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                //사진 객체를 생성한다
                var bitmap = BitmapFactory.decodeFile(contentUri.path)

                // 회전 각도값을 구한다.
                val degree = getDegree(loginActivity, contentUri)
                // 회전된 이미지를 구한다.
                val bitmap2 = rotateBitmap(bitmap, degree.toFloat())
                // 크기를 조정한 이미지를 구한다.
                val bitmap3 = resizeBitmap(bitmap2, 1024)

                fragmentInputMemoBinding.imageView.setImageBitmap(bitmap3)

                isAddPicture = true


                //사진 파일을 삭제한다
                var file = File(contentUri.path)
                file.delete()

            }

        }
    }


    //카메라 런처를 실행하는 메서드
    private fun startCamera(){
        contentUri = Data.getPicture(loginActivity, "kr.co.lion.android01.firstusemvvmproject.file_provider")

        if (contentUri != null){
            //실행할 액티비티를 카메라 엑티비티로 지정한다
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //이미지가 저장될 경로를 가지고 있는 Uri 객체를 인텐트에 담아준다
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
            //카메라 엑티비티 실행
            cameraLauncher.launch(cameraIntent)
        }
    }


    //엘범 런처 설정
    private fun settingAlbumLauncher(){
        val contract = ActivityResultContracts.StartActivityForResult()
        albumLauncher = registerForActivityResult(contract){
            // 사진 선택을 완료한 후 돌아왔다면
            if(it.resultCode == AppCompatActivity.RESULT_OK){
                // 선택한 이미지의 경로 데이터를 관리하는 Uri 객체를 추출한다.
                val uri = it.data?.data
                if(uri != null){
                    // 안드로이드 Q(10) 이상이라면
                    val bitmap = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        // 이미지를 생성할 수 있는 객체를 생성한다.
                        val source = ImageDecoder.createSource(loginActivity.contentResolver, uri)
                        // Bitmap을 생성한다.
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        // 컨텐츠 프로바이더를 통해 이미지 데이터에 접근한다.
                        val cursor = loginActivity.contentResolver.query(uri, null, null, null, null)
                        if(cursor != null){
                            cursor.moveToNext()

                            // 이미지의 경로를 가져온다.
                            val idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                            val source = cursor.getString(idx)

                            // 이미지를 생성한다
                            BitmapFactory.decodeFile(source)
                        }  else {
                            null
                        }
                    }

                    // 회전 각도값을 가져온다.
                    val degree = getDegree(loginActivity, uri)
                    // 회전 이미지를 가져온다
                    val bitmap2 = rotateBitmap(bitmap!!, degree.toFloat())
                    // 크기를 줄인 이미지를 가져온다.
                    val bitmap3 = resizeBitmap(bitmap2, 1024)

                    fragmentInputMemoBinding.imageView.setImageBitmap(bitmap3)
                    isAddPicture = true
                }
            }
        }
    }

    //엘범 런처를 실행하는 메서드
    private fun startAlbumLauncher(){
        //사진가져오기
        // 앨범에서 사진을 선택할 수 있도록 셋팅된 인텐트를 생성한다.
        val albumIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // 실행할 액티비티의 타입을 설정(이미지를 선택할 수 있는 것이 뜨게 한다)
        albumIntent.setType("image/*")
        // 선택할 수 있는 파들의 MimeType을 설정한다.
        // 여기서 선택한 종류의 파일만 선택이 가능하다. 모든 이미지로 설정한다.
        val mimeType = arrayOf("image/*")
        albumIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)

        // 액티비티를 실행한다.
        albumLauncher.launch(albumIntent)
    }
}






































