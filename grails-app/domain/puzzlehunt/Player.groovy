package puzzlehunt

class Player {
    String name
    String password

    static constraints = {
        name unique: true
    }

    def getSolvedPuzzles() {
        Attempt.where { player == this } findAll {it.isCorrect} collect { it.puzzle }
    }

    def hasSolved(Puzzle puz) {
        Attempt.where { player == this && puzzle == puz } collect {println it.answer; println it.isCorrect; it.isCorrect} contains true
    }

    def getLastSubmission() {
        def item = Attempt.where {timestamp == max(timestamp).of{ player==this } && player==this }.list()
        item.size() ? item.first().timestamp : 0
    }

    static hasMany = []
}
