package puzzlehunt

class Attempt {

    static final def Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray()
    static final def cleanSolution = { it.toUpperCase().toCharArray().findAll {c -> c in Alphabet} .join()}

    Team team
    Puzzle puzzle
    String answer
    boolean isSkip = false
    long timestamp
    boolean isCorrect

//    def getIsCorrect() {
//        isSkip || cleanSolution(puzzle.solution) == cleanSolution(answer)
//    }

    static belongsTo = [puzzle: "puzzle", team: "team"]

    static constraints = {
    }

    static boolean checkIfCorrect(Puzzle puzzle, String answer) {
        cleanSolution(puzzle.solution) == cleanSolution(answer)
    }
}
