package puzzlehunt

class Hint {  
    Player player
    Puzzle puzzle
    String question
    String notes // notes from owner on how hint went
    Player owner
    Long nexi
    Long phone
    Boolean closed

    static constraints = {
        question nullable: true
        notes nullable: true
        owner nullable: true
        closed nullable: true
        nexi nullable: true
        phone nullable: true
    }
    
    static belongsTo = [puzzle: "puzzle", player: "player", owner: "player"]
}
