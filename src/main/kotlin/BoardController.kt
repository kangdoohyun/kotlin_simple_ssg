class BoardController{
    fun list(rq: Rq) {
        println("번호 / 생성 날짜 / 게시판이름 / 게시판코드")
        for (board in boardRepository.boards){
            println("${board.id} / ${board.regDate} / ${board.name} / ${board.code}")
        }
    }

    fun add(rq: Rq) {
        print("게시판 이름 : ")
        val name = readLineTrim()
        val boardNameDuplicateCheck = boardRepository.boardNameDuplicateCheck(name)
        if(boardNameDuplicateCheck == false){
            println("이미 존재하는 게시판입니다")
            return
        }
        print("게시판 코드 : ")
        val code = readLineTrim()
        val boardCodeDuplicateCheck = boardRepository.boardCodeDuplicateCheck(code)
        if(boardCodeDuplicateCheck == false){
            println("이미 존재하는 게시판입니다")
            return
        }

        boardRepository.addBoard(name, code)
        println("${name}게시판 생성 완료")
    }

}