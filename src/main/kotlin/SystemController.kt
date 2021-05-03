class SystemController {
    fun exit(rq: Rq) {
        println("프로그램을 종료합니다")
    }

    fun nonExistentCommand(rq: Rq) {
        println("${rq.actionPath}는 존재하지 않는 명령어입니다")
    }
}