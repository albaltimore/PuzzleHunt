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

    def getPartialSolution(input) {
        def ret = null
        partialSolutions.each {println it; if (cleanSolution(it.trigger) == cleanSolution(input)) ret = it.hint}
        println ret
        return ret
    }

    static hasMany = [requiredPuzzles: Puzzle, partialSolutions: PartialSolution]

    static constraints = {
        name unique: true
        introResource nullable: true
        solvedResource nullable: true
    }
}
