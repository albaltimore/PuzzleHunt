package puzzlehunt

class PartialSolution {
    String partialSolution
    String hint

    static constraints = {
        hint size: 0..<2048
    }
}
