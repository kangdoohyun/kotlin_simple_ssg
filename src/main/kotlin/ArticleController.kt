class ArticleController {
    fun delete(rq: Rq) {
        if (loginedMember == null) {
            println("로그인 후 이용해주세요")
            return
        }
        val id = rq.getIntParam("id", 0)

        if (id == 0) {
            println("id를 입력해주세요.")
            return
        }

        val article = articleRepository.getArticleById(id)

        if (article == null) {
            println("${id}번 게시물은 존재하지 않습니다.")
            return
        }
        if (article.memberId != loginedMember!!.id) {
            println("본인의 게시물만 삭제할수 있습니다")
            return
        }
        articleRepository.deleteArticle(article)
        println("${id}번 게시물이 삭제되었습니다.")
    }

    fun modify(rq: Rq) {
        if (loginedMember == null) {
            println("로그인 후 이용해주세요")
            return
        }
        val id = rq.getIntParam("id", 0)

        if (id == 0) {
            println("id를 입력해주세요.")
            return
        }
        val article = articleRepository.getArticleById(id)

        if (article == null) {
            println("${id}번 게시물은 존재하지 않습니다.")
            return
        }
        if (article.memberId != loginedMember!!.id) {
            println("본인의 게시물만 수정할수 있습니다")
            return
        }
        print("${id}번 게시물 새 제목 : ")
        val title = readLineTrim()
        print("${id}번 게시물 새 내용 : ")
        val body = readLineTrim()

        articleRepository.modifyArticle(id, title, body)

        println("${id}번 게시물이 수정되었습니다.")
    }

    fun detail(rq: Rq) {
        val id = rq.getIntParam("id", 0)

        if (id == 0) {
            println("id를 입력해주세요.")
            return
        }

        val article = articleRepository.getArticleById(id)

        if (article == null) {
            println("${id}번 게시물은 존재하지 않습니다.")
            return
        }

        val writer = memberRepository.getMemberById(article.memberId)!!
        val board = boardRepository.getBoardById(article.boardId)!!
        println("번호 : ${article.id}")
        println("작성날짜 : ${article.regDate}")
        println("갱신날짜 : ${article.updateDate}")
        println("게시판 : ${board.name}")
        println("작성자 : ${writer.nickname}")
        println("제목 : ${article.title}")
        println("내용 : ${article.body}")
    }

    fun list(rq: Rq) {
        val page = rq.getIntParam("page", 1)
        val searchKeyword = rq.getStringParam("searchKeyword", "")
        val boardId = rq.getIntParam("boardId", 0)
        val memberId = rq.getIntParam("memberId", 0)

        val filteredArticles = articleRepository.getFilteredArticles(memberId, boardId, searchKeyword, page, 5)

        println("번호 / 작성날짜 / 게시판 / 작성자 / 제목")

        for (article in filteredArticles) {
            val writer = memberRepository.getMemberById(article.memberId)!!
            val board = boardRepository.getBoardById(article.boardId)!!

            println("${article.id} / ${article.regDate} / ${board.name} / ${writer.nickname} / ${article.title}")
        }
    }

    fun write(rq: Rq) {
        if (loginedMember == null) {
            println("로그인 후 이용해주세요")
            return
        }
        print("게시판 종류")
        val boards: List<Board> = boardRepository.getBoards()
        var boardSelectStr = ""
        for (board in boards){
            if (boardSelectStr.isNotEmpty()){
                boardSelectStr += ", "
            }
            boardSelectStr += "${board.name}=${board.id}"
        }
        println("(${boardSelectStr})")

        print("게시판 번호 : ")
        val boardId = readLineTrim().toInt()
        val searchExistBoard = boardRepository.getBoardById(boardId)
        if (searchExistBoard == null) {
            println("존재하지 않는 게시판입니다")
            return
        }
        print("제목 : ")
        val title = readLineTrim()
        print("내용 : ")
        val body = readLineTrim()



        val id = articleRepository.addArticle(loginedMember!!.id,boardId, title, body)

        println("${id}번 게시물이 추가되었습니다.")
    }
}