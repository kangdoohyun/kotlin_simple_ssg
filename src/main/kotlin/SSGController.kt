class SSGController {
    fun build() {
        makeArticleDetailPages()
        makeArticleListPages()
    }

    private fun makeArticleListPages() {
        val boards = boardRepository.getBoards()

        for (board in boards){
            makeArticleListPage(board)
        }
    }

    private fun makeArticleListPage(board: Board) {
        val articles = articleRepository.getArticles()
        val boards = boardRepository.getBoards()

        val filePath = "html/list/article_list_${board.code}.html"
        var fileContent = "<meta charset=\"UTF-8\">\n"
        fileContent += "<div><h1>${board.name}게시판</h1></div>\n"
        fileContent += "<div><h2>게시판 리스트</h2></div>\n"

        for (board1 in boards){
            fileContent += "<div><a href=\"article_list_${board1.code}.html\">${board1.name}게시판</a></div>\n"
        }
        fileContent += "<div><h2>${board.name}게시글</h2></div>\n"
        for (article in articles){
            if (article.boardId == board.id){
                val member = memberRepository.getMemberById(article.memberId)!!
                fileContent += "<div>번호 : ${article.id}    제목 : <a href=\"../detail/article_detail_${article.id}.html\">${article.title}</a>    작성자 : ${member.nickname}</div>\n"
            }
        }
        writeStrFile(filePath, fileContent)
        println("${filePath}파일이 생성되었습니다")
    }

    private fun makeArticleDetailPages() {
        val articles = articleRepository.getArticles()

        for (article in articles){
            makeArticleDetailPage(article)
        }
    }

    private fun makeArticleDetailPage(article: Article) {
        val member = memberRepository.getMemberById(article.memberId)!!
        val board = boardRepository.getBoardById(article.boardId)!!

        val filePath = "html/detail/article_detail_${article.id}.html"

        var fileContent = "<meta charset=\"UTF-8\">\n"
        fileContent += "<div><h1>${article.title}</h1></div>\n"
        fileContent += "<div>번호 : ${article.id}</div>\n"
        fileContent += "<div>작성 날짜 : ${article.regDate}</div>\n"
        fileContent += "<div>수정 날짜 : ${article.updateDate}</div>\n"
        fileContent += "<div>작성자 : ${member.nickname}</div>\n"
        fileContent += "<div>게시판 : ${board.name}</div>\n"
        fileContent += "<div>제목 : ${article.title}</div>\n"
        fileContent += "<div>내용 : ${article.body}</div>\n"
        fileContent += "<div><a href=\"#\" onclick=\"history.back();\">뒤로가기</a></div>\n"

        writeStrFile(filePath, fileContent)
        println("${filePath}파일이 생성되었습니다")
    }


}