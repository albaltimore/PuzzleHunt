package puzzlehunt

class Attempt {

    Player player
    Puzzle puzzle
    String answer

    def getIsCorrect() {
        puzzle.solution == answer
    }

    static mappedBy = [
        puzzle: "attempt",
        player: "attempt"
    ]

    static belongsTo = [puzzle: "puzzle", player: "player"]

    static constraints = {
    }
}
