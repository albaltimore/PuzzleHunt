package puzzlehunt

class Attempt {

    static final def Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray()
    static final def cleanSolution = { it.toUpperCase().toCharArray().findAll {c -> c in Alphabet} .join()}

    Player player
    Puzzle puzzle
    String answer
    long timestamp

    def getIsCorrect() {
        cleanSolution(puzzle.solution) == cleanSolution(answer)
    }

    static belongsTo = [puzzle: "puzzle", player: "player"]

    static constraints = {
    }
}
