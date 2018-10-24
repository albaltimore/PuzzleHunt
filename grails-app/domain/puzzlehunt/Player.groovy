package puzzlehunt

class Player {
    String name
    String password
    String role
    Long lastHint = 0
    long hintRegen = 1000 * 60 * 20
    int hintCount = 0
    int hintMaxCount = 1
    PlayerStatus playerStatus
    String contactInfo
    String email
    String room
    String description
    Long firstLoginTime

    static constraints = {
        name unique: true
        role nullable: true
        lastHint nullable: true
        playerStatus nullable: true
        contactInfo nullable: true
        email nullable: true
        room nullable: true
        description nullable: true
        firstLoginTime nullable: true
    }

    def getSolvedPuzzles() {
        Attempt.findAllByPlayerAndIsCorrect(this, true)*.puzzle.unique()
    }

    def hasSolved(Puzzle puz) {
        Attempt.where { player == this && puzzle == puz }*.isCorrect.contains true
    }

    def firstSolution(Puzzle p) {
        Attempt.findAllByPlayerAndPuzzleAndIsCorrect(this, p, true)*.timestamp.min()
    }

    def getUnlockTime(Puzzle p) {
        //TODO handle all other cases

        if (!p.requiredCount) return firstLoginTime
        def times = Attempt.findAllByPlayerAndIsCorrect(this, true).collect {
            firstSolution(it.puzzle)
        }.findAll().sort()

        if (times.size() < p.requiredCount) return null

        times[p.requiredCount - 1]
    }

    def getSolvablePuzzles() {
        def solved = getSolvedPuzzles()*.id
        Puzzle.list().findAll { p -> p.id in solved || (((!p.requiredPuzzles && (p.round.unlocked || !p.round.requiredPuzzles.size() || p.round.requiredPuzzles*.puzzle*.id.findAll { rp -> rp in solved }.size())) || p.requiredPuzzles*.puzzle.findAll { rp -> rp.id in solved }.size()) && p.requiredCount <= solved.size()) }.unique()
    }

    def isSolvable(Puzzle puzzle) {
        def solved = getSolvedPuzzles()*.id
        hasSolved(puzzle) || (((!puzzle.requiredPuzzles && (puzzle.round.unlocked || !puzzle.round.requiredPuzzles.size() || puzzle.round.requiredPuzzles*.puzzle.findAll { rp -> hasSolved(rp) }.size())) || puzzle.requiredPuzzles*.puzzle.findAll { p -> hasSolved(p) }.size()) && puzzle.requiredCount <= solved.size())
    }

    def getLastSubmission() {
        def item = Attempt.where { timestamp == max(timestamp).of { player == this } && player == this }.list()
        item.size() ? item.first().timestamp : 0
    }

    def getStatus() {
        def cid = getStatusPoints()
        def stati = PlayerStatus.where { statusLevel == max(statusLevel).of { statusLevel <= cid } }.list()
        stati.size() ? stati.first() : null
    }

    def getStatusPoints() {
        def cid = (getSolvedPuzzles()*.statusBoost ?: [0]).sum()
        cid + (ActivityAttempt.where { player == this }*.statusPoints ?: [0]).sum()
    }

    static hasMany = [:]
}
