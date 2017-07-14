package puzzlehunt

class Attempt {

    static final def Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray()
    static final def cleanSolution = { it.toUpperCase().toCharArray().findAll {c -> c in Alphabet} .join()}

    Player player
    Puzzle puzzle
    String answer
    boolean isSkip = false
    long timestamp

    def getIsCorrect() {
        isSkip || cleanSolution(puzzle.solution) == cleanSolution(answer)
    }

    static belongsTo = [puzzle: "puzzle", player: "player"]

    static constraints = {
    }
}
