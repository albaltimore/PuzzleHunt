package puzzlehunt

class PlayerStatus {
    int statusLevel
    long hintTime = 0
    long puzzleTime = 0
    int hintCount = 0
    boolean priorityLine = false
    Resource resource
    String name

    static constraints = {
        resource nullable: true
        statusLevel unique: true
    }
}
