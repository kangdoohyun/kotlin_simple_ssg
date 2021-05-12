class ArticleRepository {
    private val articles = mutableListOf<Article>()

    fun getArticles(): MutableList<Article> {
        val lastId = getLastId()
        val articles = mutableListOf<Article>()
        for (id in 1..lastId) {
            val jsonStr = readStrFromFile("data/article/$id.json")
            val jsonMap = mapFromJson(jsonStr)
            articles.add(
                Article(
                    jsonMap["id"].toString().toInt(),
                    jsonMap["regDate"].toString(),
                    jsonMap["updateDate"].toString(),
                    jsonMap["memberId"].toString().toInt(),
                    jsonMap["boardId"].toString().toInt(),
                    jsonMap["title"].toString(),
                    jsonMap["body"].toString()
                )
            )
        }
        return articles
    }

    private fun getLastId(): Int {
        return readIntFromFile("data/article/lastId.txt")
    }

    fun deleteArticle(article: Article) {
        articles.remove(article)
    }

    fun getArticleById(id: Int): Article? {
        for (article in articles) {
            if (article.id == id) {
                return article
            }
        }

        return null
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