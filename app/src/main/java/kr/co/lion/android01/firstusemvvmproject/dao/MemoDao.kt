package kr.co.lion.android01.firstusemvvmproject.dao

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
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

        //사용자 메모를 삭제하는 메서드
        suspend fun deleteUserMemo(memoIdx: Int){
            val job1 = CoroutineScope(Dispatchers.IO).launch {
                //컬렉션에 접근
                val collectionReference = Firebase.firestore.collection("MemoData")
                var querySnapshot = collectionReference.whereEqualTo("memoIdx", memoIdx).get().await()
                for (document in querySnapshot.documents) {
                    document.reference.delete()
                }
            }
        }

        //사용자 메모를 수정하는 메서드
        suspend fun updateUserMemo(memoIdx: Int, memoModel: MemoModel){
            val job1 = CoroutineScope(Dispatchers.IO).launch {
                val collectionReference = Firebase.firestore.collection("MemoData")
                val querySnapshot = collectionReference.whereEqualTo("memoIdx", memoIdx).get().await()
                for (document in querySnapshot.documents){
                    document.reference.set(memoModel)
                }
            }
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

        //사용자 아이디로 유저 정보 가져오기
        suspend fun getUserMemoByUserId(userId:String) : MemoModel?{
            //사용자 정보를 담을 객체
            var memoModel:MemoModel? = null


            val job1 = CoroutineScope(Dispatchers.IO).launch {
                //컬렉션에 접근하는 객체
                val collectionReference = Firebase.firestore.collection("MemoData")
                //userId 필드가 매개변수로 들어오는 userId와 같은 문서들을 가져온다
                val querySnapshot = collectionReference.whereEqualTo("userId", userId).get().await()
                //만약 가져온 것이 있다면
                if (querySnapshot.isEmpty == false){
                    //가져온 문서 객체들이 들어있는 리스트에서 첫 번째 객체를 추출
                    memoModel = querySnapshot.documents[0].toObject(MemoModel::class.java)
                }
            }
            job1.join()

            return memoModel
        }


        //게시글 목록을 가져온다
        suspend fun gettingMemoList(userId: String):MutableList<MemoModel>{
            //게시글 정보를 담을 리스트
            val memoList = mutableListOf<MemoModel>()

            val job1 = CoroutineScope(Dispatchers.IO).launch {
                //컬렉션에 접근한다
                val collectionReference = Firebase.firestore.collection("MemoData")
                var query = collectionReference.whereEqualTo("userId", userId)
                query = query.orderBy("memoIdx")
                val querySnapshot = query.get().await()
                //가져온 문서의 수 만큼 반복한다
                querySnapshot.forEach {
                    val memoModel = it.toObject(MemoModel::class.java)
                    memoList.add(memoModel)
                }
            }
            job1.join()

            return memoList
        }

        //idx값으로 메모 정보를 가져온다
        suspend fun gettingMemoByMemoIdx(memoIdx:Int): MemoModel?{
            //객체를 담을 변수
            var memoModel:MemoModel? = null

            val job1 = CoroutineScope(Dispatchers.IO).launch {
                //컬렉션 접근 객체
                val collectionReference = Firebase.firestore.collection("MemoData")
                val querySnapshot = collectionReference.whereEqualTo("memoIdx", memoIdx).get().await()
                memoModel = querySnapshot.documents[0].toObject(MemoModel::class.java)

            }
            job1.join()

            return memoModel
        }

    }
}
















































