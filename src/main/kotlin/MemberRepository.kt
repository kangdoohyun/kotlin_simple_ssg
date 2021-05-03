class MemberRepository {
    private var members = mutableListOf<Member>()
    private var lastId = 0

    fun addMember(
        loginId: String,
        loginPw: String,
        name: String,
        nickname: String,
        cellphoneNo: String,
        email: String
    ) {
        val id = ++lastId
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()
        members.add(Member(id, regDate, updateDate, loginId, loginPw, name, nickname, cellphoneNo, email))
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
        for (member in members) {
            if (member.loginId == loginId) {
                return member
            }
        }
        return null
    }

    fun getMemberById(memberId: Int): Member? {
        for (member in members) {
            if (member.id == memberId) {
                return member
            }
        }
        return null
    }

}