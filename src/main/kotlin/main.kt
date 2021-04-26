fun main() {
    println("==simple ssg 시작 ==")
    while (true){
        print("명령어 : ")
        val command = readLineTrim()
        val commandBits = command.split("?", limit = 2)
        val URL = commandBits[0]
        val param = commandBits[1]
        val paramMap = mutableMapOf<String, String>()
        // 제목=1&내용=1
        val paramBits = param.split("&")
        for (paramBit in paramBits){
            val paramBitsBits = paramBit.split("=")
            val key = paramBitsBits[0]
            val value = paramBitsBits[1]
            paramMap[key] = value
        }

        when(URL){
            "/system/exit" ->{
                println("프로그램 종료")
                break
            }
            "/article/detail" ->{
                val id = paramMap["id"]!!.toInt()
                println("$id 번 게시물")
            }
        }
    }
    println("==simple ssg 종료 ==")
}
fun readLineTrim() = readLine()!!.trim()