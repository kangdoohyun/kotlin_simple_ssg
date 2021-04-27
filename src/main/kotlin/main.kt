import java.lang.NumberFormatException
import java.text.SimpleDateFormat

fun main() {
    articleRepository.makeTestArticles()

    while (true){
        print("명령어 : ")
        val command = readLineTrim()
        val rq = Rq(command)
        when(rq.actionPath){
            "/system/exit" ->{
                println("프로그램 종료")
                break
            }
            "/article/write" ->{
                print("제목 : ")
                val title = readLineTrim()
                print("내용 : ")
                val body = readLineTrim()
                val id = articleRepository.addArticle(title, body)
                println("${id}번 게시물 작성 완료")
            }
            "/article/detail" -> {
                val id = rq.getIntParam("id",0)
                val article = articleRepository.getArticleById(id)
                if(article == null){
                    println("${id}번 게시물이 존재하지 않습니다")
                    continue
                }
                println("번호 : ${article.id}")
                println("작성 날짜 : ${article.regDate}")
                println("수정 날짜 : ${article.updateDate}")
                println("제목 : ${article.title}")
                println("내용 : ${article.body}")
            }
            "/article/modify" ->{
                val id = rq.getIntParam("id", 0)
                val article = articleRepository.getArticleById(id)
                if(article == null){
                    println("${id}번 게시물이 존재하지 않습니다")
                    continue
                }
                print("수정할 제목 : ")
                article.title = readLineTrim()
                print("수정할 내용 : ")
                article.body = readLineTrim()
                article.updateDate = Util.getNowDateStr()
            }
            "/article/delete" ->{
                val id = rq.getIntParam("id", 0)
                val article = articleRepository.getArticleById(id)
                if(article == null){
                    println("${id}번 게시물이 존재하지 않습니다")
                    continue
                }
                articleRepository.articles.remove(article)
            }
            "/article/list" ->{
                val page = rq.getIntParam("page", 1)
                val searchKeyword = rq.getStringParam("searchKeyword", "")
                val itemsInAPage = 5
                val jumpIndex = (page -1) * itemsInAPage
                val articles = articleRepository.getFilteredArticles(searchKeyword, jumpIndex, itemsInAPage)
                println("번호 / 작성날짜 / 제목")
                for (article in articles){
                    println("${article.id}  /  ${article.regDate}  /  ${article.title}")
                }
            }
            else->{
                println("${rq.actionPath}는 존재하지 않는 명령어입니다")
            }
        }
    }
}
fun readLineTrim() = readLine()!!.trim()

data class Article(val id : Int, val regDate : String, var updateDate : String, var title : String, var body : String){}

object articleRepository{
    var articlesLastId = 0
    var articles = mutableListOf<Article>()

    fun addArticle(title: String, body: String) : Int{
        val id = ++articlesLastId
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()
        val article = Article(id,regDate,updateDate,title, body)
        articles.add(article)
        articlesLastId = id
        return id
    }

    fun makeTestArticles(){
        for (i in 1 .. 100){
            val title = "제목_$i"
            val body = "내용_$i"
            addArticle(title, body)
        }
    }

    fun getArticleById(id : Int) : Article?{
        for (article in articles){
            if(article.id == id){
                return article
            }
        }
        return null
    }

    fun getFilteredArticles(searchKeyword : String, jumpIndex : Int, itemsInAPage : Int): List<Article>{
        var filteredArticles1 = articles
        if(searchKeyword.isNotEmpty()){
            filteredArticles1 = mutableListOf<Article>()
            for (article in articles){
                if(article.title.contains(searchKeyword)){
                    filteredArticles1.add(article)
                }
            }
        }
        val startIndex = filteredArticles1.lastIndex - jumpIndex
        val endIndex = startIndex - itemsInAPage + 1
        val filteredArticles2 = mutableListOf<Article>()
        for (i in startIndex downTo endIndex){
            filteredArticles2.add(filteredArticles1[i])
        }
        return filteredArticles2
    }
}

object Util{
    fun getNowDateStr(): String {
        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format1.format(System.currentTimeMillis())
    }
}
class Rq(command: String) {
    val actionPath : String
    private val paramMap : Map<String, String>

    init {
        val commandBits = command.split("?", limit = 2)
        actionPath = commandBits[0].trim()
        val queryStr = if(commandBits.lastIndex == 1 && commandBits[1].isNotEmpty()){
            commandBits[1].trim()
        }
        else{
            ""
        }
        paramMap = if(queryStr.isEmpty()){
            mapOf()
        }
        else{
            val queryStrBits = queryStr.split("&")
            val paramMapTemp = mutableMapOf<String, String>()
            for (queryStrBit in queryStrBits){
                val queryStrBitsBits = queryStrBit.split("=", limit = 2)
                val paramKey = queryStrBitsBits[0].trim()
                val paramValue = if(queryStrBitsBits.lastIndex == 1 && queryStrBitsBits[1].isNotEmpty()){
                    queryStrBitsBits[1].trim()
                }
                else{
                    ""
                }
                if(paramValue.isNotEmpty()){
                    paramMapTemp[paramKey] = paramValue
                }
            }
            paramMapTemp.toMap()
        }
    }
    fun getStringParam(paramValue : String, default : String): String {
        return paramMap[paramValue] ?: default
    }
    fun getIntParam(paramValue : String, default: Int) : Int {
        return try {
            paramMap[paramValue]?.toInt() ?: default
        }
        catch (e:NumberFormatException){
            default
        }
    }
}

