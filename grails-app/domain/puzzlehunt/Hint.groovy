package puzzlehunt

class Hint {
    Team team
    Puzzle puzzle
    String question
    String notes // notes from owner on how hint went
    Player owner
    String contactInfo
    boolean closed = false
    long createTime = System.currentTimeMillis()

    static constraints = {
        notes nullable: true, maxSize: 1200
        owner nullable: true
        closed nullable: true
        question maxSize: 1200
    }

    static belongsTo = [puzzle: "puzzle", team: "team", owner: "player"]
}
