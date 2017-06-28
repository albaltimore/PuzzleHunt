package puzzlehunt

class Hint {  
    Player player
    Puzzle puzzle
    String question
    String notes // notes from owner on how hint went
    Player owner
    Player lastOwner
    Long nexi
    Long phone
    Boolean closed
    Date createTime

    static constraints = {
        question nullable: true
        notes nullable: true
        owner nullable: true
        lastOwner nullable: true
        closed nullable: true
        nexi nullable: true
        phone nullable: true
    }
    
    static belongsTo = [puzzle: "puzzle", player: "player", owner: "player"]
}
