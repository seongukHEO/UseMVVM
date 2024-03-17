package kr.co.lion.android01.firstusemvvmproject

import android.content.Context
import android.content.DialogInterface
import android.os.SystemClock
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.concurrent.thread

class Data {

}

enum class FragmentName(var str: String){
    LOGIN_FRAGMENT("LoginFragment"),
    JOIN_FRAGMENT("JoinFragment"),
    SEARCH_ID_FRAGMENT("SearchIdFragment"),
    SEARCH_PW_FRAGMENT("SearchPwFragment")
}
enum class FragmentMemoName(var str: String){
    ALL_MEMO_FRAGMENT("AllMemoFragment"),
    INPUT_MEMO_FRAGMENT("InputMemoFragment"),
    SHOW_MEMO_FRAGMENT("ShowMemoFragment"),
    MODIFY_MEMO_FRAGMENT("ModifyMemoFragment")
}




//Dialog를 보여주는 확장함수
fun Context.showDialog(title:String, message:String, listener: (DialogInterface, Int) -> Unit){
    var dialog = MaterialAlertDialogBuilder(this)
    dialog.setTitle(title)
    dialog.setMessage(message)
    dialog.setPositiveButton("확인", listener)
    dialog.setNegativeButton("취소", null)
    dialog.show()
}

//키보드를 올려주는 확장함수
fun Context.showSoftInput(view:View, context: Context){
    view.requestFocus()

    thread {
        SystemClock.sleep(1000)
        val inputMethodManager = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, 0)
    }
}

//키보드를 내려주는 확장함수
fun Context.hideSoftInput(activity: AppCompatActivity){
    if (activity.window.currentFocus != null){
        val inputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.window.currentFocus?.windowToken, 0)
    }
}