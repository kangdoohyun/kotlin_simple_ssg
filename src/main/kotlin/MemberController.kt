class MemberController{
    fun logout(rq:Rq){
        loginedMember = null
        println("로그아웃 되었습니다")
        return
    }

    fun login(rq: Rq){
        print("로그인 아이디 : ")
        val loginId = readLineTrim()
        val loginCheck = memberRepository.getMemberByLoginId(loginId)
        if(loginCheck == null){
            println("아이디를 확인해주세요")
            return
        }
        print("로그인 패스워드 : ")
        val loginPw = readLineTrim()
        if (loginCheck.loginPw != loginPw){
            println("패스워르들 확인해주세요")
            return
        }

        loginedMember = loginCheck
        println("${loginedMember!!.nickname}님 환영합니다")
    }

    fun join(rq: Rq){
        print("로그인 아이디 : ")
        val loginId = readLineTrim()
        val loginIdOverlap = memberRepository.loginIdOverlap(loginId)
        if(!loginIdOverlap){
            println("이미 누군가 사용중인 로그인 아이디 입니다")
            return
        }
        print("로그인 패스워드 : ")
        val loginPw = readLineTrim()
        print("이름 : ")
        val name = readLineTrim()
        print("닉네임 : ")
        val nickname = readLineTrim()
        print("전화번호 : ")
        val cellphoneNo = readLineTrim()
        print("이메일 : ")
        val email = readLineTrim()

        memberRepository.addMember(loginId, loginPw, name, nickname, cellphoneNo, email)
        println("${nickname}님 환영합니다")
    }
}