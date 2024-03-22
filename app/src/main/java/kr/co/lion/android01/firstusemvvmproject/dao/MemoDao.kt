package kr.co.lion.android01.firstusemvvmproject.dao

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kr.co.lion.android01.firstusemvvmproject.model.MemoModel
import kr.co.lion.android01.firstusemvvmproject.model.UserModel

class MemoDao {

    companion object{
        //사용자 번호 시퀀스 값을 가져온다
        suspend fun getUserSequence():Int {

            //값 초기화
            var userSequence = 0

            var job1 = CoroutineScope(Dispatchers.IO).launch {
                //컬렉션에 접근할 수 있는 객체
                val collectionReference = Firebase.firestore.collection("Sequence")
                //사용자 번호 시퀀스 값을 가지고 있는 문서에 접근할 수 있는 객체
                val documentReference = collectionReference.document("MemoSequence")
                //문서 내에 있는 데이터를 가져올 수 있는 객체를 가져온다
                val documentSnapshot = documentReference.get().await()
                userSequence = documentSnapshot.getLong("value")?.toInt()!!

            }
            job1.join()
            return userSequence
        }

        //사용자 번호 시퀀스 업데이트 하는 메서드를 만들어준다
        suspend fun updateSequence(userSequence:Int){
            val job1 = CoroutineScope(Dispatchers.IO).launch {
                //컬렉션에 접근할 수 있는 객체 생성
                val collectionReference = Firebase.firestore.collection("Sequence")
                //사용자 번호 시퀀스 값을 가지고 있는 문서에 접근할 수 있는 객체를 가져온다
                val documentReference = collectionReference.document("MemoSequence")
                //저장할 데이터를 담을 HashMap을 만들어준다
                val map = mutableMapOf<String, Long>()
                map["value"] = userSequence.toLong()
                //저장한다
                documentReference.set(map)
            }
            job1.join()
        }

        //사용자 정보를 저장하는 메서드를 만들어준다
        suspend fun insertUserData(memoModel: MemoModel){
            val job1 = CoroutineScope(Dispatchers.IO).launch {
                //컬렉션에 접근할 수 있는 객체를 가져온다
                val collectionReference = Firebase.firestore.collection("MemoData")
                collectionReference.add(memoModel)
            }
            job1.join()
        }
    }
}
















































