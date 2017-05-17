package puzzlehunt

class Hint {  
    Player player
    Puzzle puzzle
    String question
    String notes // notes from owner on how hint went
    Player owner

    static constraints = {
        question nullable: true
        notes nullable: true
        owner nullable: true
    }
    
    static belongsTo = [puzzle: "puzzle", player: "player", owner: "player"]
}
