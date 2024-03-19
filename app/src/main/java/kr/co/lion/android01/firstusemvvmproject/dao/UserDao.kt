package kr.co.lion.android01.firstusemvvmproject.dao

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kr.co.lion.android01.firstusemvvmproject.model.UserModel

class UserDao {

    companion object{

        //사용자 번호 시퀀스 값을 가져온다
        //약간 SQLite에서 select?느낌인건가?
        suspend fun getUserSequence():Int{
            //값 초기화
            var userSequence = 0

            var job1 = CoroutineScope(Dispatchers.IO).launch {
                //컬렉션(테이블)에 접근할 수 있는 객체를 가져온다
                val collectionReference = Firebase.firestore.collection("Sequence")
                //사용자 번호 시퀀스 값을 가지고 있는 문서에 접근할 수 있는 객체를 가져온다
                val documentReference = collectionReference.document("UserSequence")
                //문서 내에 있는 데이터를 가져올 수 있는 객체를 가져온다
                val documentSnapshot = documentReference.get().await()
                userSequence = documentSnapshot.getLong("value")?.toInt()!!
            }
            job1.join()
            return userSequence

        }

        //사용자 번호 시퀀스를 업데이트 하는 메서드를 만들어준다
        suspend fun updateUserSequence(userSequence:Int){
            val job1 = CoroutineScope(Dispatchers.IO).launch {
                //컬렉션에 접근할 수 있는 객체 생성
                val collectionReference = Firebase.firestore.collection("Sequence")
                //사용자 번호 시퀀스 값을 가지고 있는 문서에 접근할 수 있는 객체를 가져온다
                val documentReference = collectionReference.document("UserSequence")
                //저장할 데이터를 담을 HashMap을 만들어준다
                val map = mutableMapOf<String, Long>()
                map["value"] = userSequence.toLong()
                //저장한다
                documentReference.set(map)
            }
            job1.join()
        }

        //사용자 정보를 저장하는 메서드를 만들어준다
        suspend fun insertUserData(userModel: UserModel){
            val job1 = CoroutineScope(Dispatchers.IO).launch {
                //컬렉션에 접근할 수 있는 객체를 가져온다
                val collectionReference = Firebase.firestore.collection("Sequence")
                //컬렉션에 문서를 추가한다
                //문서를 추가할 때 객체나 맵을 지정한다
                //추가된 문서 내부의 필드는 객체가 가진 프러퍼티의 이름이나
                //맵에 있는 데이터의 이름과 동일하게 결정한다
                collectionReference.add(userModel)
            }
            job1.join()
        }

        //입력한 아이디가 이미 저장이 되어있는지 확인
        suspend fun checkUserIdExist(joinUserId:String) : Boolean{

            var chk = false

            val job1 = CoroutineScope(Dispatchers.IO).launch {
                //컬렉션에 접근할 수 있는 객체를 가져온다
                val collectionReference = Firebase.firestore.collection("Sequence")
                //필드의 이름 값 형태로 넣어준다
                //WhereEqualTo : 같은 것
                val queryShapshot = collectionReference.whereEqualTo("userId", joinUserId).get().await()
                //반환되는 리스트에 담긴 문서 객체가 없다면 존재하는 아이디로 취급한다
                chk = queryShapshot.isEmpty
            }
            job1.join()
            return chk

        }

        //사용자의 전화번호를 통해 사용자 정보를 가져오는 메서드
        //null을 허용하는 이유는 null이 나올경우 그 아이디에 맞는 정보가 없는 것이다
        suspend fun getUserDataByNumber(number:String): UserModel? {
            //사용자 객체를 담을 변수
            var userModel:UserModel? = null

            val job1 = CoroutineScope(Dispatchers.IO).launch {
                //UserData 컬렉션 접근 객체
                val collectionReference = Firebase.firestore.collection("Sequence")
                //number 필드가 매개변수로 들어오는 Number와 같은 문서들을 가져온다
                val querySnapshot = collectionReference.whereEqualTo("number", number).get().await()
                //만약 가져온 것이 있다면?
                if (querySnapshot.isEmpty == false){
                    //가져온 문서 객체들이 들어있는 리스트에서 첫 번째 객체를 추출
                    userModel = querySnapshot.documents[0].toObject(UserModel::class.java)
                }
            }
            job1.join()

            return userModel
        }

    }

}










































