class ArticleRepository {
    fun getArticles(): MutableList<Article> {
        val lastId = getLastId()
        val articles = mutableListOf<Article>()
        for (id in 1..lastId) {
            val article = articleFromFile("data/article/$id.json")
            if (article != null) {
                articles.add(article)
            }
        }
        return articles
    }

    private fun getLastId(): Int {
        return readIntFromFile("data/article/lastId.txt")
    }

    fun articleFromFile(jsonFilePath: String): Article? {
        val jsonStr = readStrFromFile(jsonFilePath)
        if (jsonStr == "") {
            return null
        }
        val map = mapFromJson(jsonStr)

        val id = map["id"].toString().toInt()
        val regDate = map["regDate"].toString()
        val updateDate = map["updateDate"].toString()
        val boardId = map["boardId"].toString().toInt()
        val memberId = map["memberId"].toString().toInt()
        val title = map["title"].toString()
        val body = map["body"].toString()

        return Article(id, regDate, updateDate, boardId, memberId, title, body)
    }


    fun deleteArticle(article: Article) {
        deleteFile("data/article/${article.id}.json")
    }

    fun getArticleById(id: Int): Article? {
        return articleFromFile("data/article/${id}.json")
    }

    fun addArticle(boardId: Int, memberId: Int, title: String, body: String): Int {
        val id = getLastId() + 1
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()
        val article = Article(id, regDate, updateDate, memberId, boardId, title, body)
        writeStrFile("data/article/$id.json", article.toJson())
        writeIntFile("data/article/lastId.txt", id)
        return id
    }

    fun modifyArticle(id: Int, title: String, body: String) {
        val article = getArticleById(id)!!

        article.title = title
        article.body = body
        article.updateDate = Util.getNowDateStr()
        val newArticle = Article(
            article.id,
            article.regDate,
            article.updateDate,
            article.memberId,
            article.boardId,
            article.title,
            article.body
        )
        writeStrFile("data/article/$id.json", newArticle.toJson())
    }

    fun getFilteredArticles(
        memberId: Int,
        boardId: Int,
        searchKeyword: String,
        page: Int,
        itemsCountInAPage: Int
    ): List<Article> {
        val filtered1Articles = getSearchKeywordFilteredArticles(getArticles(), searchKeyword)
        val filtered2Articles = getBoardIdFilteredArticles(filtered1Articles, boardId)
        val filtered3Articles = getMemberIdFilteredArticles(filtered2Articles, memberId)
        val filtered4Articles = getPageFilteredArticles(filtered3Articles, page, itemsCountInAPage)

        return filtered4Articles
    }

    private fun getMemberIdFilteredArticles(articles: List<Article>, memberId: Int): List<Article> {
        if (memberId == 0) {
            return articles
        }

        val filteredArticles = mutableListOf<Article>()

        for (article in articles) {
            if (article.memberId == memberId) {
                filteredArticles.add(article)
            }
        }
        return filteredArticles
    }

    private fun getSearchKeywordFilteredArticles(articles: List<Article>, searchKeyword: String): List<Article> {
        if (searchKeyword.isEmpty()) {
            return articles
        }

        val filteredArticles = mutableListOf<Article>()

        for (article in articles) {
            if (article.title.contains(searchKeyword)) {
                filteredArticles.add(article)
            }
        }

        return filteredArticles
    }

    private fun getBoardIdFilteredArticles(articles: List<Article>, boardId: Int): List<Article> {
        if (boardId == 0) {
            return articles
        }

        val filteredArticles = mutableListOf<Article>()

        for (article in articles) {
            if (article.boardId == boardId) {
                filteredArticles.add(article)
            }
        }
        return filteredArticles
    }

    private fun getPageFilteredArticles(articles: List<Article>, page: Int, itemsCountInAPage: Int): List<Article> {
        val filteredArticles = mutableListOf<Article>()

        val offsetCount = (page - 1) * itemsCountInAPage

        val startIndex = articles.lastIndex - offsetCount
        var endIndex = startIndex - (itemsCountInAPage - 1)

        if (endIndex < 0) {
            endIndex = 0
        }

        for (i in startIndex downTo endIndex) {
            filteredArticles.add(articles[i])
        }

        return filteredArticles
    }
}