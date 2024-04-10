package kr.co.lion.android01.firstusemvvmproject.fragment

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.lion.android01.firstusemvvmproject.Data
import kr.co.lion.android01.firstusemvvmproject.FragmentMemoName
import kr.co.lion.android01.firstusemvvmproject.R
import kr.co.lion.android01.firstusemvvmproject.activity.LoginActivity
import kr.co.lion.android01.firstusemvvmproject.dao.MemoDao
import kr.co.lion.android01.firstusemvvmproject.databinding.FragmentModifyMemoBinding
import kr.co.lion.android01.firstusemvvmproject.hideSoftInput
import kr.co.lion.android01.firstusemvvmproject.model.MemoModel
import kr.co.lion.android01.firstusemvvmproject.showDialog
import kr.co.lion.android01.firstusemvvmproject.showSoftInput
import kr.co.lion.android01.firstusemvvmproject.viewModel.ModifyMemoViewModel
import java.io.File

class ModifyMemoFragment : Fragment() {

    lateinit var fragmentModifyMemoBinding: FragmentModifyMemoBinding
    lateinit var loginActivity: LoginActivity

    lateinit var modifyMemoViewModel: ModifyMemoViewModel

    lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    lateinit var albumLauncher: ActivityResultLauncher<Intent>

    //촬영된 사진이 저장된 경로 정보를 가지고 있는 Uri 객체
    lateinit var contentUri: Uri

    //이미지를 첨부한 적이 있는지
    var isAddPicture = false

    var memoIdx = 0

    // 사용자에 의해서 이미지가 변경되었는지..
    var isChangeImage = false
    // 사용자가 이미지를 삭제했는지
    var isRemoveImage = false

    var resetTitle = ""
    var resetContent = ""
    var date = ""
    var resetImage: Bitmap? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentModifyMemoBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_modify_memo, container, false)
        modifyMemoViewModel = ModifyMemoViewModel()
        fragmentModifyMemoBinding.modifyMemoViewModel = modifyMemoViewModel
        fragmentModifyMemoBinding.lifecycleOwner = this

        memoIdx = arguments?.getInt("memoIdx")!!

        loginActivity = activity as LoginActivity
        settingToolBar()
        settingView()
        setEvent()
        settingCameraLauncher()
        settingAlbumLauncher()

        return fragmentModifyMemoBinding.root
    }

    //툴바 설정
    private fun settingToolBar(){
        fragmentModifyMemoBinding.apply {
            materialToolbar8.apply {
                title = "메모 수정"
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    loginActivity.removeFragment(FragmentMemoName.MODIFY_MEMO_FRAGMENT)
                }

                inflateMenu(R.menu.modify_menu)
                setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.camera_modify_menu -> {
                            startCamera()

                        }
                        R.id.album_modify_menu -> {
                            startAlbumLauncher()
                        }
                        R.id.reset_modify_menu -> {
                            loginActivity.showDialog("메모 초기화", "메모를 초기화 하시겠습니까?"){ dialogInterface: DialogInterface, i: Int ->
                                resetInputForm()

                                // 사용자가 이미지를 변경했는지의 값을 초기화한다.
                                isChangeImage = false
                                isRemoveImage = false
                            }
                        }
                    }

                    true
                }
            }
        }
    }

    //뷰설정
    private fun settingView(){
        fragmentModifyMemoBinding.apply {

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main){
                var memoInfo = MemoDao.gettingMemoByMemoIdx(memoIdx)

                resetTitle = memoInfo?.memoTitle!!
                resetContent = memoInfo?.memoContents!!
                date = memoInfo?.date!!

                //가져온 데이터를 보여준다
                modifyMemoViewModel!!.userId.value = memoInfo.userId
                modifyMemoViewModel!!.memoTitle.value = resetTitle
                modifyMemoViewModel!!.memoContents.value = resetContent
                modifyMemoViewModel!!.memoDate.value = date

                //이미지 데이터를 불러온다
                if (memoInfo.image != null){
                    MemoDao.gettingContentImage(loginActivity, memoInfo.image!!, fragmentModifyMemoBinding.imageView4)

                    //이미지 뷰로부터 이미지를 가져와 초기화를 위하누프러퍼티에 담아준다
                    val bitmapDrawable = fragmentModifyMemoBinding.imageView4.drawable as BitmapDrawable
                    resetImage = bitmapDrawable.bitmap
                }
            }
        }
    }

    private fun resetInputForm(){
        //가져온 데이터를 보여준다
        modifyMemoViewModel.memoTitle.value = resetTitle
        modifyMemoViewModel.memoContents.value = resetContent

        //이미지 데이터를 불러온다
        if (resetImage != null){
            fragmentModifyMemoBinding.imageView4.setImageBitmap(resetImage)
        }
    }

    //이벤트 설정
    private fun setEvent(){
        fragmentModifyMemoBinding.apply {
            buttonModify.setOnClickListener {
                val chk = checkOK()
                if (chk == true){
                    loginActivity.showDialog("메모 수정", "메모를 수정하시겠습니까?"){ dialogInterface: DialogInterface, i: Int ->
                        updateMemoData()
                        loginActivity.removeFragment(FragmentMemoName.MODIFY_MEMO_FRAGMENT)
                        loginActivity.removeFragment(FragmentMemoName.SHOW_MEMO_FRAGMENT)
                        loginActivity.hideSoftInput(loginActivity)
                    }
                }
            }
            buttonDeletePicture.setOnClickListener {
                fragmentModifyMemoBinding.imageView4.setImageResource(R.drawable.panorama_24px)
                isChangeImage = true
                isRemoveImage = true
            }
        }
    }

    //유효성 검사
    private fun checkOK():Boolean{
        fragmentModifyMemoBinding.apply {
            val title = modifyMemoViewModel!!.memoTitle.value!!
            val contents = modifyMemoViewModel!!.memoContents.value!!

            if (title.trim().isEmpty()){
                loginActivity.showDialog("제목 입력 오류", "제목을 입력해주세요"){ dialogInterface: DialogInterface, i: Int ->
                    loginActivity.showSoftInput(textModifyTitle, loginActivity)
                }
                return false
            }

            if (contents.trim().isEmpty()){
                loginActivity.showDialog("내용 입력 오류", "내용을 입력해주세요"){ dialogInterface: DialogInterface, i: Int ->
                    loginActivity.showSoftInput(textModifyContents, loginActivity)
                }
                return false
            }
            return true
        }
    }

    //메모 정보를 업데이트 한다
    private fun updateMemoData(){
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {

                var serverFileName:String? = null

                if (isChangeImage == true && isRemoveImage == false){
                    Data.saveImageViewData(loginActivity, fragmentModifyMemoBinding.imageView4, "uploadTemp.jpg")

                    //서버에서의 파일 이름
                    serverFileName = "image_${System.currentTimeMillis()}.jpg"
                    //서버에 업로드한다
                    MemoDao.uploadImage(loginActivity, "uploadTemp.jpg", serverFileName)
                }

                var userId = modifyMemoViewModel!!.userId.value!!
                var memoTitle = modifyMemoViewModel!!.memoTitle.value!!
                var memoContents = modifyMemoViewModel!!.memoContents.value!!

                var memoModel = MemoModel(memoIdx, userId ,memoTitle, null, "", memoContents)

                //업로드된 이미지가 있다면
                if (serverFileName != null){
                    memoModel.image = serverFileName
                }

                MemoDao.updateMemoData(memoModel, isRemoveImage)
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
                val degree = Data.getDegree(loginActivity, contentUri)
                // 회전된 이미지를 구한다.
                val bitmap2 = Data.rotateBitmap(bitmap, degree.toFloat())
                // 크기를 조정한 이미지를 구한다.
                val bitmap3 = Data.resizeBitmap(bitmap2, 1024)

                fragmentModifyMemoBinding.imageView4.setImageBitmap(bitmap3)

                isChangeImage = true


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
                    val degree = Data.getDegree(loginActivity, uri)
                    // 회전 이미지를 가져온다
                    val bitmap2 = Data.rotateBitmap(bitmap!!, degree.toFloat())
                    // 크기를 줄인 이미지를 가져온다.
                    val bitmap3 = Data.resizeBitmap(bitmap2, 1024)

                    fragmentModifyMemoBinding.imageView4.setImageBitmap(bitmap3)
                    isChangeImage = true
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











































