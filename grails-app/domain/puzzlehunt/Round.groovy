package puzzlehunt

class Round {
    Resource background
    String name
    int floorId
    int width
    int height
    boolean unlocked = false
    Resource winningResource
    Resource gameoverResource

    static constraints = {
        background nullable: true
        winningResource nullable: true
        gameoverResource nullable: true
    }

    static hasMany = [requiredPuzzles: RequiredPuzzle]
    static mappedBy = [ requiredPuzzles: "none"]
}
