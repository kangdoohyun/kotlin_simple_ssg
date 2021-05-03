class BoardController {
    fun list(rq: Rq) {
        println("번호 / 생성 날짜 / 게시판이름 / 게시판코드")
        for (board in boardRepository.boards) {
            println("${board.id} / ${board.regDate} / ${board.name} / ${board.code}")
        }
    }

    fun make(rq: Rq) {
        print("게시판 이름 : ")
        val name = readLineTrim()
        val boardNameDuplicateCheck = boardRepository.boardNameDuplicateCheck(name)
        if (!boardNameDuplicateCheck) {
            println("이미 존재하는 게시판입니다")
            return
        }
        print("게시판 코드 : ")
        val code = readLineTrim()
        val boardCodeDuplicateCheck = boardRepository.boardCodeDuplicateCheck(code)
        if (!boardCodeDuplicateCheck) {
            println("이미 존재하는 게시판입니다")
            return
        }

        boardRepository.addBoard(name, code)
        println("${name}게시판 생성 완료")
    }

    fun modify(rq: Rq) {
        val code = rq.getStringParam("code", "")
        val board = boardRepository.getBoardByCode(code)
        if (board == null) {
            println("존재하지 않는 게시판입니다")
            return
        }
        print("수정할 게시판 이름 : ")
        val newName = readLineTrim()
        val boardNameDuplicateCheck = boardRepository.boardNameDuplicateCheck(newName)
        if (!boardNameDuplicateCheck) {
            println("이미 존재하는 게시판입니다")
            return
        }
        print("수정할 게시판 코드 : ")
        val newCode = readLineTrim()
        val boardCodeDuplicateCheck = boardRepository.boardCodeDuplicateCheck(newCode)
        if (!boardCodeDuplicateCheck) {
            println("이미 존재하는 게시판입니다")
            return
        }

        board.name = newName
        board.code = newCode
        board.regDate = Util.getNowDateStr()

        println("게시판이 수정되었습니다")
    }

    fun delete(rq: Rq) {
        val code = rq.getStringParam("code", "")
        val board = boardRepository.getBoardByCode(code)
        if (board == null) {
            println("존재하지 않는 게시판입니다")
            return
        }

        boardRepository.boards.remove(board)
        println("${board.name}게시판이 삭제되었습니다")
    }

}