package puzzlehunt

class Player {
    String name
    String password

    static constraints = {
        name unique: true
    }

    def getSolvedPuzzles() {
        Attempt.where { def pz = puzzle; player == this && answer == pz.solution }  list null collect { it.puzzle }
    }

    static hasMany = []
}
