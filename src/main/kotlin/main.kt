import java.text.SimpleDateFormat

fun main() {
    println("== SIMPLE SSG 시작 ==")
    articleRepository.makeTestArticles()
    while (true) {
        print("명령어) ")
        val command = readLineTrim() // 입력 : /article/detail?id=1&title=제목1

        val rq = Rq(command)

        when(rq.actionPath){
            "/system/exit" ->{
                println("프로그램 종료")
                break
            }
            "/article/detail"->{
                val id = rq.getIntParam("id", 0)
                var articleToDetail = articleRepository.getArticleById(id)

                if (articleToDetail == null){
                    println("${id}번 게시물은 존재하지 않습니다")
                    continue
                }
                println("번호 : ${articleToDetail.id}")
                println("작성 날짜 : ${articleToDetail.regDate}")
                println("수정 날짜 : ${articleToDetail.updateDate}")
                println("제목 : ${articleToDetail.title}")
                println("내용 : ${articleToDetail.body}")
            }
            "/article/modify" ->{
                val id = rq.getIntParam("id", 0)
                var articleToModify = articleRepository.getArticleById(id)

                if (articleToModify == null){
                    println("${id}번 게시물은 존재하지 않습니다")
                    continue
                }
                print("수정할 제목 :")
                articleToModify.title = readLineTrim()
                print("수정할 내용 :")
                articleToModify.body = readLineTrim()
                articleToModify.updateDate = Util.getNowDateStr()
                println("${id}번 게시물을 수정하였습니다")
            }
            "/article/delete" ->{
                val id = rq.getIntParam("id", 0)
                val articleToDelete = articleRepository.getArticleById(id)

                if (articleToDelete == null) {
                    println("${id}번 게시물은 존재하지 않습니다")
                    continue
                }
                    articleRepository.articles.remove(articleToDelete)
                println("${id}번 게시물을 삭제하였습니다")
            }
            "/article/write" ->{
                print("제목 : ")
                val title = readLineTrim()
                print("내용 : ")
                val body = readLineTrim()

                val id = articleRepository.addArticle(title, body)
                println("${id}번 게시물이 작성되었습니다")
            }
            "/article/list" ->{
                val commandBits = command.trim().split(" ")
                var page = rq.getIntParam("page", 1)
                var searchKeyword = rq.getStringParam("searchKeyword", "")
                if (commandBits.size == 4) {
                    searchKeyword = commandBits[2]
                    page = commandBits[3].toInt()
                } else if (commandBits.size == 3) {
                    page = commandBits[2].toInt()
                }
                val itemsCountInAPage = 10
                val offsetCount = (page - 1) * itemsCountInAPage
                val filteredArticles = articleRepository.getFilteredArticles(searchKeyword, offsetCount, itemsCountInAPage)

                println("번호 / 작성날짜 / 제목")
                for (article in filteredArticles) {
                    println("${article.id} / ${article.regDate} / ${article.title}")
                }
            }
        }
    }

    println("== SIMPLE SSG 끝 ==")
}

class Rq(command: String) {

    val actionPath: String
    val paramMap: Map<String, String>

    init {
        val commandBits = command.split("?", limit = 2)

        actionPath = commandBits[0].trim()
        val queryStr = if (commandBits.lastIndex == 1 && commandBits[1].isNotEmpty()) {
            commandBits[1].trim()
        } else {
            ""
        }

        paramMap = if (queryStr.isEmpty()) {
            mapOf()
        } else {
            val paramMapTemp = mutableMapOf<String, String>()
            val queryStrBits = queryStr.split("&")

            for (queryStrBit in queryStrBits) {
                val queryStrBitBits = queryStrBit.split("=", limit = 2)
                val paramName = queryStrBitBits[0]
                val paramValue = if (queryStrBitBits.lastIndex == 1 && queryStrBitBits[1].isNotEmpty()) {
                    queryStrBitBits[1].trim()
                } else {
                    ""
                }

                if (paramValue.isNotEmpty()) {
                    paramMapTemp[paramName] = paramValue
                }
            }

            paramMapTemp.toMap()
        }
    }
    fun getStringParam(paramValue : String, default : String): String{
        return paramMap[paramValue] ?: default
    }
    fun getIntParam(paramValue : String, default : Int): Int {
        return try {
            paramMap[paramValue]?.toInt()?:default
        }
        catch (e: NumberFormatException) {
            default
        }
    }

}
data class Article(val id: Int, val regDate: String, var updateDate: String, var title: String, var body: String){}

object articleRepository{
    val articles = mutableListOf<Article>()
    var articleLastId = 0

    fun addArticle(title : String, body: String) : Int{
        val id = ++articleLastId
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()

        val article = Article(id, regDate, updateDate, title, body)
        articles.add(article)

        articleLastId = id

        return id
    }
    fun getArticleById(id: Int): Article? {
        for (article in articles) {
            if (article.id == id) {
                return article
            }
        }
        return null
    }
    fun makeTestArticles() {
        for (id in 1..100) {
            val title = "제목_$id"
            val body = "내용_$id"

            addArticle(title, body)
        }
    }
    fun getFilteredArticles(searchKeyword: String, jumpIndex: Int, itemsInAPage: Int): MutableList<Article> {
        var filtered1Articles = articles
        if (searchKeyword.isNotEmpty()) {
            filtered1Articles = mutableListOf<Article>()

            for (article in articles) {
                if (article.title.contains(searchKeyword)) {
                    filtered1Articles.add(article)
                }
            }
        }
        val filtered2Articles = mutableListOf<Article>()

        val startIndex = filtered1Articles.lastIndex - jumpIndex
        var endIndex = startIndex - itemsInAPage + 1

        if (endIndex < 0) {
            endIndex = 0
        }
        for (i in startIndex downTo endIndex) {
            filtered2Articles.add(filtered1Articles[i])
        }

        return filtered2Articles
    }
}

fun readLineTrim() = readLine()!!.trim()

object Util {

    fun getNowDateStr(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        return dateFormat.format(System.currentTimeMillis())
    }
}
