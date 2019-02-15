package puzzlehunt

class Resource {
    Puzzle puzzle
    Round round
    String filename
    String linkUri
    String accessor
    boolean mustSolve
    String role
    String textContent

    static constraints = {
        puzzle nullable: true
        round nullable: true
        role nullable: true
        linkUri nullable: true
        filename nullable: true
        textContent nullable: true
    }
}
