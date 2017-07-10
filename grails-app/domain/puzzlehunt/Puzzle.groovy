package puzzlehunt

class Puzzle {
    static final def Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray()
    static final def cleanSolution = { it.toUpperCase().toCharArray().findAll {c -> c in Alphabet} .join()}

    String name
    String solution
    Resource introResource
    Resource solvedResource
    int xCor, yCor
    Round round
    Long timeLimit
    Set<RequiredPuzzle> requiredPuzzles

    def getPartialSolution(input) {
        def ret = null
        partialSolutions.each {if (cleanSolution(it.partialSolution) == cleanSolution(input)) ret = it.hint}
        return ret
    }

    static hasMany = [requiredPuzzles: RequiredPuzzle, partialSolutions: PartialSolution]

    static constraints = {
        name unique: true
        introResource nullable: true
        solvedResource nullable: true
        timeLimit nullable: true
    }

    static mappedBy = [
        requiredPuzzles : "none"
    ]
}
