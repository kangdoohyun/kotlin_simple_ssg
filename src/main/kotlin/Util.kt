import java.io.File
import java.text.SimpleDateFormat

fun readLineTrim() = readLine()!!.trim()

fun mapFromJson(jsonStr: String): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    // json 형식에 {} 중가로를 제거한다 .drop()
    var jsonStr = jsonStr.drop(1)
    jsonStr = jsonStr.dropLast(1)
    // ",\r\n" 기준으로 파싱한다 "," 만으로 할 경우엔 "안녕하세요, 저는" 이런입력값이 있으면 오류날 수 있기때문에 줄바꿈까지 기준으로잡는다
    val jsonItems = jsonStr.split(",\r\n")
    // 나눠진 jsonItems List를 jsonItem에 하나씩 담아서 반복문을 돌린다
    for (jsonItem in jsonItems) {
        // json 형식에서는 : 로 key와 value 를 구분하기 때문에 : 를 기준으로 스플릿하며 key,value 두개만 있으면 되기때문에 리미트를 걸러준다
        val jsonItemBits = jsonItem.trim().split(":", limit = 2)
        // key 값은 "번호" 이런식으로 쌍따옴표로 감싸져 있기때문에 그걸 제거해주기위해 drop을 사용한다
        val key = jsonItemBits[0].trim().drop(1).dropLast(1)
        // value 값은 Any 이기 때문에 어떤형태가 올지 모른다 조건문을 걸어서 형변환을 해준다
        val value = jsonItemBits[1].trim()

        when {
            // 파일은 String 으로만 저장되기 때문에 Boolean 값이어도 문자열로 저장된다 그러니 Boolean 값인 true, false로 변환해준다
            value == "true" -> {
                map[key] = true
            }
            value == "false" -> {
                map[key] = false
            }
            // \" 로 시작하는 값은 쌍따옴표로 감싸져있는거기 떄문에 String 값이다 drop 을 사용해서 쌍따옴표를 제거하고 넣어준다
            value.startsWith("\"") -> {
                map[key] = value.drop(1).dropLast(1)
            }
            // . 이 붙어있을경우 0.141592 같이 Double 이기때문에 형변환해준다
            value.contains(".") -> {
                map[key] = value.toDouble()
            }
            // 아무것도 없으면 정수다 기본적으로 문자열로 저장된 파일이기때문에 .toInt() 로 정수변환해준다
            else -> {
                map[key] = value.toInt()
            }
        }
    }

    return map.toMap()
}

fun deleteFile(filePath: String) {
    File(filePath).delete()
}

fun readStrFromFile(filePath: String): String {
    if (!File(filePath).isFile){
        return ""
    }
    return File(filePath).readText(Charsets.UTF_8)
}

fun writeStrFile(filePath: String, fileContent: String) {
    // .mkdirs() 경로폴더가 없을경우에 경로폴더 생성
    // parentFile : "data/article/1.json 일 경우 1.json인 마지막 파일의 부모까지만 폴더로 생성
    File(filePath).parentFile.mkdirs()
    File(filePath).writeText(fileContent)
}

fun readIntFromFile(filePath: String): Int {
    return readStrFromFile(filePath).toInt()
}

fun writeIntFile(filePath: String, fileContent: Int) {
    writeStrFile(filePath, fileContent.toString())
}

object Util {
    fun getNowDateStr(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        return format.format(System.currentTimeMillis())
    }
}