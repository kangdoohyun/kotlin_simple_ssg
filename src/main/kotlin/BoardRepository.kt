class BoardRepository {
    var boards = mutableListOf<Board>()
    var lastId = 0

    fun addBoard(name: String, code: String) {
        val id = ++lastId
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()
        boards.add(Board(id, regDate, updateDate, name, code))
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
        for (board in boards) {
            if (board.name == name) {
                return board
            }
        }
        return null
    }

    fun boardCodeDuplicateCheck(code: String): Boolean {
        val board = getBoardByCode(code)
        return board == null
    }

    fun getBoardByCode(code: String): Board? {
        for (board in boards) {
            if (board.code == code) {
                return board
            }
        }
        return null
    }

    fun getBoardById(boardId: Int): Board? {
        for (board in boards) {
            if (board.id == boardId) {
                return board
            }
        }
        return null
    }
}