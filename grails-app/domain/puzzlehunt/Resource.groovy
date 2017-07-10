package puzzlehunt

class Resource {
    Puzzle puzzle
    Round round
    String filename
    String accessor
    boolean mustSolve

    static constraints = {
        puzzle nullable: true
        round nullable: true
    }
}
