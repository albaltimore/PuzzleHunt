package puzzlehunt

class Puzzle {

    String name
    String solution

    int xCor, yCor

    static hasMany = [requiredPuzzles: Puzzle]

    static constraints = {
        name unique:true

    }
}
