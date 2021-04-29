import java.text.SimpleDateFormat
fun main() {
    println("== SIMPLE SSG 시작 ==")

    articleRepository.makeTestArticles()
    memberRepository.makeTestMember()
    var loginedMember : Member? = null

    while (true) {
        val prompt = if(loginedMember != null){
            "${loginedMember.nickname} : "
        }
        else{
            "명령어 : "
        }
        print(prompt)
        val command = readLineTrim()

        val rq = Rq(command)

        when (rq.actionPath) {
            "/system/exit" -> {
                println("프로그램을 종료합니다.")
                break
            }
            "/article/write" -> {
                if(loginedMember == null){
                    println("로그인 후 이용해주세요")
                    continue
                }
                print("제목 : ")
                val title = readLineTrim()
                print("내용 : ")
                val body = readLineTrim()

                val id = articleRepository.addArticle(loginedMember.id, title, body)

                println("${id}번 게시물이 추가되었습니다.")
            }
            "/article/list" -> {
                val page = rq.getIntParam("page", 1)
                val searchKeyword = rq.getStringParam("searchKeyword", "")

                val filteredArticles = articleRepository.getFilteredArticles(searchKeyword, page, 10)

                println("번호 / 작성날짜 / 작성자 / 제목")

                for (article in filteredArticles) {
                    val writer = memberRepository.getMemberById(article.memberId)!!
                    println("${article.id} / ${article.regDate} / ${writer.nickname} / ${article.title}")
                }
            }
            "/article/detail" -> {
                val id = rq.getIntParam("id", 0)

                if (id == 0) {
                    println("id를 입력해주세요.")
                    continue
                }

                val article = articleRepository.getArticleById(id)

                if (article == null) {
                    println("${id}번 게시물은 존재하지 않습니다.")
                    continue
                }

                val writer = memberRepository.getMemberById(article.memberId)!!

                println("번호 : ${article.id}")
                println("작성날짜 : ${article.regDate}")
                println("갱신날짜 : ${article.updateDate}")
                println("작성자 : ${writer.nickname}")
                println("제목 : ${article.title}")
                println("내용 : ${article.body}")
            }
            "/article/modify" -> {
                if(loginedMember == null){
                    println("로그인 후 이용해주세요")
                    continue
                }
                val id = rq.getIntParam("id", 0)

                if (id == 0) {
                    println("id를 입력해주세요.")
                    continue
                }
                val article = articleRepository.getArticleById(id)

                if (article == null) {
                    println("${id}번 게시물은 존재하지 않습니다.")
                    continue
                }
                if(article.memberId != loginedMember.id){
                    println("본인의 게시물만 수정할수 있습니다")
                    continue
                }
                print("${id}번 게시물 새 제목 : ")
                val title = readLineTrim()
                print("${id}번 게시물 새 내용 : ")
                val body = readLineTrim()

                articleRepository.modifyArticle(id, title, body)

                println("${id}번 게시물이 수정되었습니다.")
            }
            "/article/delete" -> {
                if(loginedMember == null){
                    println("로그인 후 이용해주세요")
                    continue
                }
                val id = rq.getIntParam("id", 0)

                if (id == 0) {
                    println("id를 입력해주세요.")
                    continue
                }

                val article = articleRepository.getArticleById(id)

                if (article == null) {
                    println("${id}번 게시물은 존재하지 않습니다.")
                    continue
                }
                if(article.memberId != loginedMember.id){
                    println("본인의 게시물만 삭제할수 있습니다")
                    continue
                }
                articleRepository.deleteArticle(article)
                println("${id}번 게시물이 삭제되었습니다.")
            }
            "/member/join" -> {
                print("로그인 아이디 : ")
                val loginId = readLineTrim()
                val loginIdOverlap = memberRepository.loginIdOverlap(loginId)
                if(!loginIdOverlap){
                    println("이미 누군가 사용중인 로그인 아이디 입니다")
                    continue
                }
                print("로그인 패스워드 : ")
                val loginPw = readLineTrim()
                print("이름 : ")
                val name = readLineTrim()
                print("닉네임 : ")
                val nickname = readLineTrim()
                print("전화번호 : ")
                val cellphoneNo = readLineTrim()
                print("이메일 : ")
                val email = readLineTrim()

                memberRepository.addMember(loginId, loginPw, name, nickname, cellphoneNo, email)
                println("${nickname}님 환영합니다")
            }
            "/member/login" ->{
                print("로그인 아이디 : ")
                val loginId = readLineTrim()
                val loginCheck = memberRepository.getMemberByLoginId(loginId)
                if(loginCheck == null){
                    println("아이디를 확인해주세요")
                    continue
                }
                print("로그인 패스워드 : ")
                val loginPw = readLineTrim()
                if (loginCheck.loginPw != loginPw){
                    println("패스워르들 확인해주세요")
                    continue
                }

                loginedMember = loginCheck
                println("${loginedMember.nickname}님 환영합니다")
            }
            "/member/logout" ->{
                loginedMember = null
                println("로그아웃 되었습니다")
                continue
            }

        }
    }

    println("== SIMPLE SSG 끝 ==")
}

// Rq는 UserRequest의 줄임말이다.
// Request 라고 하지 않은 이유는, 이미 선점되어 있는 클래스명 이기 때문이다.
class Rq(command: String) {
    // 데이터 예시
    // 전체 URL : /artile/detail?id=1
    // actionPath : /artile/detail
    val actionPath: String

    // 데이터 예시
    // 전체 URL : /artile/detail?id=1&title=안녕
    // paramMap : {id:"1", title:"안녕"}
    private val paramMap: Map<String, String>

    // 객체 생성시 들어온 command 를 ?를 기준으로 나눈 후 추가 연산을 통해 actionPath와 paramMap의 초기화한다.
    // init은 객체 생성시 자동으로 딱 1번 실행된다.
    init {
        // ?를 기준으로 둘로 나눈다.
        val commandBits = command.split("?", limit = 2)

        // 앞부분은 actionPath
        actionPath = commandBits[0].trim()

        // 뒷부분이 있다면
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

    fun getStringParam(name: String, default: String): String {
        return paramMap[name] ?: default
    }

    fun getIntParam(name: String, default: Int): Int {
        return if (paramMap[name] != null) {
            try {
                paramMap[name]!!.toInt()
            } catch (e: NumberFormatException) {
                default
            }
        } else {
            default
        }
    }
}

// 게시물 관련 시작
data class Article(
    val id: Int,
    val regDate: String,
    var updateDate: String,
    val memberId: Int,
    var title: String,
    var body: String
)

object articleRepository {
    private val articles = mutableListOf<Article>()
    private var lastId = 0

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

    fun addArticle(memberId: Int, title: String, body: String): Int {
        val id = ++lastId
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()
        articles.add(Article(id, regDate, updateDate, memberId, title, body))

        return id
    }

    fun makeTestArticles() {
        for (id in 1..100) {
            addArticle(id % 5 + 1,"제목_$id", "내용_$id")
        }
    }

    fun modifyArticle(id: Int, title: String, body: String) {
        val article = getArticleById(id)!!

        article.title = title
        article.body = body
        article.updateDate = Util.getNowDateStr()
    }

    fun getFilteredArticles(searchKeyword: String, page: Int, itemsCountInAPage: Int): List<Article> {
        val filtered1Articles = getSearchKeywordFilteredArticles(articles, searchKeyword)
        val filtered2Articles = getPageFilteredArticles(filtered1Articles, page, itemsCountInAPage)

        return filtered2Articles
    }

    private fun getSearchKeywordFilteredArticles(articles: List<Article>, searchKeyword: String): List<Article> {
        val filteredArticles = mutableListOf<Article>()

        for (article in articles) {
            if (article.title.contains(searchKeyword)) {
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
// 게시물 관련 끝
// 멤버 관련 시작
data class Member(val id : Int, val regDate: String, var updateDate: String, val loginId : String, var loginPw : String, var name : String, var nickname : String, var cellphoneNo : String, var email : String){}

object memberRepository{
    var members = mutableListOf<Member>()
    var lastId = 0

    fun addMember(loginId: String, loginPw: String, name: String, nickname: String, cellphoneNo: String, email: String) {
        val id = ++lastId
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()
        members.add(Member(id, regDate, updateDate, loginId, loginPw, name, nickname, cellphoneNo, email))
    }

    fun makeTestMember(){
        for(i in 1 .. 5){
            addMember("user$i", "user$i", "name$i", "nickname$i", "cellphoneNo$i", "email$i")
        }
    }

    fun loginIdOverlap(loginId: String): Boolean {
        val member = getMemberByLoginId(loginId)
        return member == null
    }

    fun getMemberByLoginId(loginId: String): Member? {
        for (member in members){
            if (member.loginId == loginId){
                return member
            }
        }
        return null
    }

    fun getMemberById(memberId: Int): Member? {
        for (member in members){
            if(member.id == memberId){
                return member
            }
        }
        return null
    }

}

// 멤버 관련 끝
// 유틸 관련 시작
fun readLineTrim() = readLine()!!.trim()

object Util {
    fun getNowDateStr(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        return format.format(System.currentTimeMillis())
    }
}
// 유틸 관련 끝