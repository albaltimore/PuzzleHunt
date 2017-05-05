package puzzlehunt

class Player {
    String name
    String password

    static constraints = {
        name unique: true
    }

    def getSolvedPuzzles() {
        Attempt.where { def pz = puzzle; player == this } findAll {it.isCorrect} collect { it.puzzle }
    }

    def hasSolved(Puzzle puzzle) {
        Attempt.findByPlayerAndPuzzle this, puzzle collect {it.isCorrect} contains true
    }

    static hasMany = []
}
