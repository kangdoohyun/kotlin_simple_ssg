import java.lang.IndexOutOfBoundsException

class BoardRepository {
    fun getBoards(): MutableList<Board> {
        val lastId = getLastId()
        val boards = mutableListOf<Board>()
        for (id in 1..lastId) {
            val board = boardFromFile("data/board/$id.json")
            if (board != null) {
                boards.add(board)
            }
        }
        return boards
    }

    private fun boardFromFile(jsonFilePath: String): Board? {
        val jsonStr = readStrFromFile(jsonFilePath)
        if (jsonStr == "") {
            return null
        }
        val map = mapFromJson(jsonStr)

        val id = map["id"].toString().toInt()
        val regDate = map["regDate"].toString()
        val updateDate = map["updateDate"].toString()
        val name = map["name"].toString()
        val code = map["code"].toString()

        return Board(id, regDate, updateDate, name, code)
    }

    private fun getLastId(): Int {
        return readIntFromFile("data/board/lastId.txt")
    }

    fun addBoard(name: String, code: String) {
        val id = getLastId() + 1
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()
        val board = Board(id, regDate, updateDate, name, code)
        writeStrFile("data/board/$id.json", board.toJson())
        writeIntFile("data/board/lastId.txt", id)
    }

    fun makeTestBoard() {
        addBoard("공지", "notice")
        addBoard("자유", "free")
    }

    fun boardNameDuplicateCheck(name: String): Boolean {
        val board = getBoardByName(name)
        return board == null
    }

    private fun getBoardByName(name: String): Board? {
        val boards = getBoards()
        for (i in 0..boards.size) {
            try {
                if (boards[i].name == name) {
                    return boards[i]
                }
            } catch (e: IndexOutOfBoundsException) {
                return null
            }
        }
        return null
    }

    fun boardCodeDuplicateCheck(code: String): Boolean {
        val board = getBoardByCode(code)
        return board == null
    }

    fun getBoardByCode(code: String): Board? {
        val boards = getBoards()
        for (i in 0..boards.size) {
            try {
                if (boards[i].code == code) {
                    return boards[i]
                }
            } catch (e: IndexOutOfBoundsException) {
                return null
            }
        }
        return null
    }

    fun getBoardById(id: Int): Board? {
        return boardFromFile("data/board/$id.json")
    }

    fun deleteBoard(board: Board) {
        deleteFile("data/board/${board.id}.json")
    }
}