package puzzlehunt

class Player {
    String name
    String password
    String role
    Long lastHint = 0
    long hintRegen = 1000 * 60 * 20
    int hintCount = 0
    int hintMaxCount = 1
    int playerStatusBonus = 0
    PlayerStatus playerStatus
    String contactInfo

    static constraints = {
        name unique: true
        role nullable: true
        lastHint nullable: true
        playerStatus nullable: true
        contactInfo nullable: true
    }

    def getSolvedPuzzles() {
        Attempt.where { player == this } findAll {it.isCorrect} *.puzzle .unique()
    }

    def hasSolved(Puzzle puz) {
        Attempt.where { player == this && puzzle == puz } *.isCorrect .contains true
    }

    def getSolvablePuzzles() {
        def solved = getSolvedPuzzles()*.id
        Puzzle.list().findAll { p-> p.id in solved || (!p.requiredPuzzles && (p.round.unlocked || !p.round.requiredPuzzles.size() || p.round.requiredPuzzles*.id.findAll {rp -> rp in solved} .size() )) || p.requiredPuzzles*.puzzle.findAll {rp -> rp.id in solved}.size() } .unique()
    }

    def isSolvable(Puzzle puzzle) {
        hasSolved(puzzle) || (!puzzle.requiredPuzzles && (puzzle.round.unlocked || !puzzle.round.requiredPuzzles.size() || puzzle.round.requiredPuzzles*.id.findAll {rp -> hasSolved(rp)} .size())) || puzzle.requiredPuzzles*.puzzle.findAll { p-> hasSolved(p) } .size()
    }

    def getLastSubmission() {
        def item = Attempt.where {timestamp == max(timestamp).of{ player==this } && player==this }.list()
        item.size() ? item.first().timestamp : 0
    }

    def getStatus() {
        def cid = Attempt.where { player == this } findAll {it.isCorrect} *.puzzle findAll {it.statusBoost} .unique() .size()
        cid+= playerStatusBonus

        def stati = PlayerStatus.where { statusLevel == max(statusLevel).of{ statusLevel <= cid } } .list()
        println "ps ${stati}"
        stati.size() ? stati.first() : null
    }

    static hasMany = []
}
