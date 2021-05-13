class SSGController {
    fun build() {
        val boards = boardRepository.getBoards()
        val articles = articleRepository.getArticles()


        for (board in boards){
            writeStrFile("build/article_list_${board.code}.html", "")
        }

        for (article in articles){
            val html = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Document</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div>${article.id}</div>\n" +
                    "    <div>${article.regDate}</div>\n" +
                    "    <div>${article.updateDate}</div>\n" +
                    "    <div>${article.memberId}</div>\n" +
                    "    <div>${article.boardId}</div>\n" +
                    "    <div>${article.title}</div>\n" +
                    "    <div>${article.body}</div>\n" +
                    "</body>\n" +
                    "</html>"
            writeStrFile("build/article_detail_${article.id}.html", html)
        }
    }

}