val systemController = SystemController()
val memberController = MemberController()
val articleController = ArticleController()
val boardController = BoardController()

var loginedMember: Member? = null

val articleRepository = ArticleRepository()
val memberRepository = MemberRepository()
val boardRepository = BoardRepository()
fun main() {
    println("== SIMPLE SSG 시작 ==")

//    memberRepository.makeTestMember()
    boardRepository.makeTestBoard()

    loginedMember = memberRepository.getMemberById(1)

    while (true) {
        val prompt = if (loginedMember != null) {
            "${loginedMember!!.nickname} : "
        } else {
            "명령어 : "
        }
        print(prompt)
        val command = readLineTrim()

        val rq = Rq(command)

        when (rq.actionPath) {
            "/system/exit" -> {
                systemController.exit(rq)
                break
            }
            "/article/write" -> {
                articleController.write(rq)
            }
            "/article/list" -> {
                articleController.list(rq)
            }
            "/article/detail" -> {
                articleController.detail(rq)
            }
            "/article/modify" -> {
                articleController.modify(rq)
            }
            "/article/delete" -> {
                articleController.delete(rq)
            }
            "/member/join" -> {
                memberController.join(rq)
            }
            "/member/login" -> {
                memberController.login(rq)
            }
            "/member/logout" -> {
                memberController.logout(rq)
            }
            "/member/list" -> {
                memberController.list()
            }
            "/member/info" -> {
                memberController.info(rq)
            }
            "/board/list" -> {
                boardController.list(rq)
            }
            "/board/make" -> {
                boardController.make(rq)
            }
            "/board/modify" -> {
                boardController.modify(rq)
            }
            "/board/delete" -> {
                boardController.delete(rq)
            }
            else -> {
                systemController.nonExistentCommand(rq)
            }
        }
    }

    println("== SIMPLE SSG 끝 ==")
}

