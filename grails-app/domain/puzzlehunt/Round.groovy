package puzzlehunt

class Round {
    Resource background
    String name
    int floorId
    int width
    int height
    boolean unlocked = false




    static constraints = {
        background nullable: true
    }

    static hasMany = [requiredPuzzles: RequiredPuzzle]
    static mappedBy = [ requiredPuzzles: "none"]
}
