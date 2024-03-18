package kr.co.lion.android01.firstusemvvmproject.model

data class UserModel(
    var userIdx: Int, var name: String, var number: String, var userId: String, var userPw: String
) {
    // 매개 변수가 없는 생성자
// fireStore 를 사용할 때 데이터를 담을 클래스 타입을 지정하게 되면
// 매개 변수가 없는 생성자를 사용해 객체 생성해주기 때문에 만들어줘야 한다.
    constructor() : this(0, "", "", "", "")
}

