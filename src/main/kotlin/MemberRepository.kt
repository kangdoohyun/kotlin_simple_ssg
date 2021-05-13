import java.lang.IndexOutOfBoundsException

class MemberRepository {
    fun getMembers(): MutableList<Member> {
        val lastId = getLastId()
        val members = mutableListOf<Member>()
        for (id in 1..lastId) {
            val member = memberFromFile("data/member/$id.json")
            if (member != null) {
                members.add(member)
            }
        }
        return members
    }

    private fun memberFromFile(jsonFilePath: String): Member? {
        val jsonStr = readStrFromFile(jsonFilePath)
        if (jsonStr == "") {
            return null
        }
        val map = mapFromJson(jsonStr)

        val id = map["id"].toString().toInt()
        val regDate = map["regDate"].toString()
        val updateDate = map["updateDate"].toString()
        val loginId = map["loginId"].toString()
        val loginPw = map["loginPw"].toString()
        val name = map["name"].toString()
        val nickname = map["nickname"].toString()
        val cellphoneNo = map["cellphoneNo"].toString()
        val email = map["email"].toString()

        return Member(id, regDate, updateDate, loginId, loginPw, name, nickname, cellphoneNo, email)
    }

    private fun getLastId(): Int {
        return readIntFromFile("data/member/lastId.txt")
    }

    fun addMember(
        loginId: String,
        loginPw: String,
        name: String,
        nickname: String,
        cellphoneNo: String,
        email: String
    ) {
        val id = getLastId() + 1
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()
        val member = Member(id, regDate, updateDate, loginId, loginPw, name, nickname, cellphoneNo, email)
        writeStrFile("data/member/$id.json", member.toJson())
        writeIntFile("data/member/lastId.txt", id)
    }

    fun makeTestMember() {
        for (i in 1..5) {
            addMember("user$i", "user$i", "name$i", "nickname$i", "cellphoneNo$i", "email$i")
        }
    }

    fun loginIdOverlap(loginId: String): Boolean {
        val member = getMemberByLoginId(loginId)
        return member == null
    }

    fun getMemberByLoginId(loginId: String): Member? {
        val members = getMembers()
        for (i in 0 .. members.size){
            try {
                if (members[i].loginId == loginId){
                    return members[i]
                }
            }
            catch (e:IndexOutOfBoundsException){
                return null
            }
        }
        return null
    }

    fun getMemberById(memberId: Int): Member? {
        return memberFromFile("data/member/$memberId.json")
    }

}