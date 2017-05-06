package puzzlehunt

class Puzzle {

    String name
    String solution
    Resource introResource
    Resource solvedResource

    int xCor, yCor

    static hasMany = [requiredPuzzles: Puzzle]

    static constraints = {
        name unique:true
        introResource nullable: true
        solvedResource nullable: true
    }
}
