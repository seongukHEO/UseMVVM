package kr.co.lion.android01.firstusemvvmproject.model

data class MemoModel (
    var memoIdx:Int, var userId:String, var memoTitle:String, var image:String?, var date:String, var memoContents:String
){
    constructor() : this(0, "","","", "", "")
}