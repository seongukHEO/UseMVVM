package kr.co.lion.android01.firstusemvvmproject.model

data class MemoModel (
    var memoIdx:Int, var memoTitle:String, var date:String, var memoContents:String
){
    constructor() : this(0, "", "", "")
}