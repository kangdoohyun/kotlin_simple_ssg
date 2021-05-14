class SSGController {
    fun build() {
        val boards = boardRepository.getBoards()
        val articles = articleRepository.getArticles()


        for (board in boards){
            val fileName = "build/article_list_${board.code}.html"
            var fileContent = """
                <meta charset="UTF-8">
            """.trimIndent()
            fileContent += "<h1>${board.name}게시판</h1>\n"
            fileContent += "<h2>게시판 리스트</h2>\n"
            for (board in boards) {
                fileContent += "<div><a href=\"article_list_${board.code}.html\">${board.name}게시판</a></div>\n"
            }
            fileContent += "<h2>${board.name}게시판 게시글</h2>\n"
            for (article in articles){
                if (article.boardId == board.id){
                    fileContent += "<div>번호 : ${article.id} 제목 : <a href=\"article_detail_${article.id}.html\">${article.title}</a></div>\n"
                }
            }
            writeStrFile(fileName, fileContent)
            println("${fileName}파일이 생성되었습니다")
        }

        for (article in articles){
            val fileName = "build/article_detail_${article.id}.html"
            var fileContent = """
                <meta charset="UTF-8">
            """.trimIndent()
            val writer = memberRepository.getMemberById(article.memberId)!!

            fileContent += "<div>번호 : ${article.id}</div>\n"
            fileContent += "<div>작성 날짜 : ${article.regDate}</div>\n"
            fileContent += "<div>수정 날짜 : ${article.updateDate}</div>\n"
            fileContent += "<div>작성자 : ${writer.nickname}</div>\n"
            fileContent += "<div>제목 : ${article.title}</div>\n"
            fileContent += "<div>내용 : ${article.body}</div>\n"

            fileContent += "<a href=\"#\" onclick=\"history.back();\">뒤로가기</a>"
            writeStrFile(fileName, fileContent)
            println("${fileName}파일이 생성되었습니다")
        }
    }

}