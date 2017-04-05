package puzzlehunt

class Player {
    String name
    String password

    static constraints = {
        name unique: true
    }

    static hasMany = [solvedPuzzles: Puzzle]
}
