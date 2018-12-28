package puzzlehunt

class Team {
    String name
    boolean isFinalized = false
    boolean isPublic = false
    String teamKey = UUID.randomUUID().toString()
    boolean hasStarted = false
    Hunt hunt

    Long lastHint = 0
    long hintRegen = 1000 * 60 * 20
    int hintCount = 0
    int hintMaxCount = 1
    String contactInfo
    Long unlockTime

    static constraints = {
        name unique: true
        lastHint nullable: true
        contactInfo nullable: true
        unlockTime nullable: true
    }

    def getSolvedPuzzles() {
        Attempt.findAllByTeamAndIsCorrect(this, true)*.puzzle.unique()
    }

    def hasSolved(Puzzle puz) {
        Attempt.where { team == this && puzzle == puz }*.isCorrect.contains true
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
        def item = Attempt.where { timestamp == max(timestamp).of { team == this } && team == this }.list()
        item.size() ? item.first().timestamp : 0
    }

    def getStatus() {
        def cid = getStatusPoints()
        def stati = TeamStatus.where { statusLevel == max(statusLevel).of { statusLevel <= cid } }.list()
        stati.size() ? stati.first() : null
    }

    def getStatusPoints() {
        def cid = (getSolvedPuzzles()*.statusBoost ?: [0]).sum()
        cid + (ActivityAttempt.where { team == this }*.statusPoints ?: [0]).sum()
    }
}
