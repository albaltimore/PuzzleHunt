package puzzlehunt

import grails.converters.JSON
import grails.gorm.transactions.Transactional

class PlayerController {

    private static final EXTENSION_TYPES = [
        "png": "image/png",
        "jpg": "image/jpg",
        "gif": "image/gif",
        "pdf": "application/pdf",
        "mp4": "video/mp4"
    ]

    def index() {}

    def getPuzzles() {
        def player = Player.get(session.playerId)
        def team = player.team
        if (!team.hasStarted) {
            team.hasStarted = true

            if (!team.save(flush: true)) {
                render status: 500
                return
            }

            println "MADE TEAM START $team.name"
        }

        def solved = team.solvedPuzzles*.id
        def timedStarted = PuzzleStart.findAllByTeam team collect { it.puzzle.id }

        def rounds = [:]
        def stat = team.status
        def statPoints = team.statusPoints

        def puzzles = team.solvablePuzzles.collect { p ->
            def started = p.timeLimit ? (p.id in timedStarted) : true
            def startTime = p.id in timedStarted ? PuzzleStart.findByTeamAndPuzzle(team, p).startTime : null
            def timeLimit = p.timeLimit ? p.timeLimit + (team.status?.puzzleTime ?: 0) : null

            def failed = p.timeLimit && started && (startTime + (timeLimit * 1000) < System.currentTimeMillis())

            if (!(p.round.id in rounds)) {
                rounds[p.round.id] = [
                    id: p.round.id,
                    name: p.round.name,
                    floorId: p.round.floorId,
                    background: p.round.background.accessor,
                    width: p.round.width,
                    height: p.round.height,
                ]
            }
            [
                id: p.id,
                xCor: p.xCor,
                yCor: p.yCor,
                name: p.name,
                roundId: p.round.id,
                requiredPuzzles: p.requiredPuzzles.collect { rp ->
                    [
                        id: rp.puzzle.id,
                        color: rp.color,
                        points: rp.coordinates.collect { c -> [xCor: c.xCor, yCor: c.yCor] },
                        pathResource: rp.pathResource?.collect { pr -> [resource: pr.resource.accessor, xCor: pr.xCor, yCor: pr.yCor] },
                    ]
                },
                pathResource: p.pathResource?.collect { pr -> [resource: pr.resource.accessor, xCor: pr.xCor, yCor: pr.yCor] },
                hintDisabled: p.disableHint,
                solved: p.id in solved,
                failed: failed,
                timeLimit: timeLimit,
                started: started,
                startTime: startTime,
//                introAccessor: started ? p?.introResource?.accessor : null,
//                introFilename: started ? p?.introResource?.filename : null,
//                solvedAccessor: p.id in solved ? p?.solvedResource?.accessor : null,
//                solvedFilename: p.id in solved ? p?.solvedResource?.filename : null,
                iconAccessor: p.id in solved ? p?.iconSolvedResource?.accessor : (failed ? p.iconFailedResource?.accessor : p?.iconReadyResource?.accessor),
                hasHintResources: p.hintResources.asBoolean()
            ]
        }

        def status = stat ? [
            resource: stat.resource.accessor,
            name: stat.name,
            hintTime: stat.hintTime,
            puzzleTime: stat.puzzleTime,
            hintCount: stat.hintCount,
            priorityLine: stat.priorityLine,
            level: stat.statusLevel,
            points: statPoints
        ] : null


        def ret = [puzzles: puzzles, rounds: rounds.values(), status: status, contactInfo: team.contactInfo]
        render ret as JSON
    }

    def getPuzzleResources() {
        def player = Player.get(session.playerId)
        def team = player.team

        Puzzle puzzle = Puzzle.findById(params.id)
        if (!puzzle || !team.isSolvable(puzzle)) {
            render status: 500
            return
        }

        def timeStarted = PuzzleStart.findByPuzzleAndTeam(puzzle, team)
        if (!timeStarted) {
            timeStarted = new PuzzleStart(puzzle: puzzle, team: team, startTime: System.currentTimeMillis())
            if (!timeStarted.save(flush: true)) {
                render status: 500
                return
            }
        }

        def started = puzzle.timeLimit ? (timeStarted as Boolean) : true
//        def startTime = timedStarted?.startTime
//        def timeLimit = puzzle.timeLimit ? puzzle.timeLimit + (team.status?.puzzleTime ?: 0) : null
        //def failed = p.timeLimit && started && (startTime + (timeLimit * 1000) < System.currentTimeMillis())
        def solved = team.hasSolved(puzzle)

        render([
            introAccessor: started ? puzzle?.introResource?.accessor : null,
            introFilename: started ? puzzle?.introResource?.filename : null,
            solvedAccessor: solved ? puzzle?.solvedResource?.accessor : null,
            solvedFilename: solved ? puzzle?.solvedResource?.filename : null,
        ] as JSON)

    }

    def getInstructions() {
        def instructions = Instruction.list(sort: 'orderNumber').collect {
            [name: it.name, resource: it.resource.accessor]
        }
        render instructions as JSON
    }

    def startTimedPuzzle() {
        def player = Player.findById(session.playerId)
        def team = player.team

        def puzzle = Puzzle.findById(params.id)
        if (team.hasSolved(puzzle) || !team.isSolvable(puzzle)) {
            render status: 404
            return
        }

        new PuzzleStart(puzzle: puzzle, team: team, startTime: System.currentTimeMillis()).save(flush: true)
    }

    def nextHintTime() {
        def player = Player.findById(session.playerId)
        def team = player.team
        def maxHints = team.hintMaxCount + (team.status?.hintCount ?: 0)
        def totalTime = team.hintRegen - (team.status?.hintTime ?: 0) * 1000

        def start = (Property.findByName('START')?.value ?: 0) as Long
        def left = (1..maxHints).findAll {
            def targetStart = System.currentTimeMillis() - totalTime * it
            (it - Hint.countByTeamAndCreateTimeGreaterThan(team, targetStart) - ((start > targetStart) ? 1 : 0)) > 0
        }.size()

        def recentHints = Hint.findAllByTeamAndCreateTimeGreaterThan(team, System.currentTimeMillis() - totalTime) ?: []
        if (start > System.currentTimeMillis() - totalTime) recentHints.add(new Hint(createTime: start))

        def ret = [
            max: maxHints,
            left: left,
            time: !recentHints.size() ? 0 : (recentHints*.createTime.max() ?: 0) + totalTime
        ]

        render ret as JSON
    }

    @Transactional
    def requestHint() {
        def player = Player.findById(session.playerId)
        def team = player.team

        def totalTime = team.hintRegen - (team.status?.hintTime ?: 0) * 1000
        def maxHints = team.hintMaxCount + (team.status?.hintCount ?: 0)

        def start = (Property.findByName('START')?.value ?: 0) as Long
        def left = (1..maxHints).findAll {
            def targetStart = System.currentTimeMillis() - totalTime * it
            (it - Hint.countByTeamAndCreateTimeGreaterThan(team, targetStart) - ((start > targetStart) ? 1 : 0)) > 0
        }.size()

        if (params.contactInfo) {
            team.contactInfo = params.contactInfo
            team.save(flush: true)
        }

        if (!left) {
            def ret = [error: "Hint requested too soon. Try later."]
            render ret as JSON
            return
        }

        def puzzle = Puzzle.findById(params.id)

        if (team.hasSolved(puzzle) || !team.isSolvable(puzzle) || puzzle.disableHint) {
            def ret = [error: "You can't request a hint for this puzzle."]
            render ret as JSON
            return
        }

        def hn = new Hint(team: team, puzzle: puzzle, question: params.question, contactInfo: params.contactInfo)
        hn.save()

        team.save(flush: true)

        def ret = [success: true]
        render ret as JSON
    }

    def checkPuzzle() {
        def player = Player.findById session.playerId
        def team = player.team
        if (System.currentTimeMillis() <= team.lastSubmission + (grailsApplication.config.puzzlehunt.puzzleTimeout as Long)) {
            def ret = [solved: false, message: "Too many submissions at once"]
            render ret as JSON
            return
        }

        Puzzle puzzle = Puzzle.findById(params.id)

        if (puzzle.timeLimit) {
            def pstart = PuzzleStart.findByPuzzleAndTeam(puzzle, team)
            if (!pstart) {
                def ret = [solved: false, message: "Puzzle not started"]
                render ret as JSON
                return
            }
            if (pstart.startTime + (puzzle.timeLimit + (team.status?.puzzleTime ?: 0)) * 1000 < System.currentTimeMillis()) {
                def ret = [solved: false, message: "You're out of time :("]
                render ret as JSON
                return
            }
        }

        def c = Attempt.checkIfCorrect(puzzle, params.solution)

        def attempt = new Attempt(team: team, puzzle: puzzle, answer: params.solution, isCorrect: c, timestamp: System.currentTimeMillis())
        attempt.save(flush: true)
        println "guess ${team.name} ${puzzle.name} ${puzzle.solution} ${params.solution}"

        def ret = [solved: c]
        if (!c) ret.message = puzzle.getPartialSolution(params.solution) ?: "Incorrect"
        render ret as JSON
    }

    def getHintResources() {
        Player player = Player.findById session.playerId
        Team team = player.team
        Puzzle puzzle = Puzzle.findById params.id


        def unlockTime = PuzzleStart.findByPuzzleAndTeam(puzzle, team)?.startTime
        println unlockTime


        if (!team.isSolvable(puzzle) || !unlockTime) {
            render status: 500
            return
        }

        def hasBlank = false
        def ret = puzzle.hintResources.sort { it.seconds }.findAll {
            if (hasBlank) return false
            def b = unlockTime + it.seconds * 1000 > System.currentTimeMillis()
            if (b) {
                hasBlank = true
                return true
            }
            return true

        } collect {
            println "$unlockTime, $it.seconds"
            if (unlockTime + it.seconds * 1000 > System.currentTimeMillis()) [unlockTime: unlockTime + it.seconds*1000]
            else [description: it.description, accessor: it.resource?.accessor, filename: it?.resource?.filename]
        }

        render ret as JSON
    }

    def getResource() {
        def bootstrapPath = grailsApplication.config.puzzlehunt.resourcePath
        def rs = Resource.findByAccessor(params.accessor)
        def player = Player.findById session.playerId
        def team = player.team

        if (rs && !rs.role && (!rs.puzzle || team.hasSolved(rs.puzzle) ||
            (!rs.mustSolve && team.isSolvable(rs.puzzle)))) {

            if (rs.linkUri) {
                redirect url: rs.linkUri
            } else if (rs.filename) {
                def f = new File("${bootstrapPath}/${rs.filename}")
                def extension = rs.filename.substring(rs.filename.lastIndexOf(".") + 1).toLowerCase()

                header 'Cache-Control', 'max-age=84600, public'
                render file: f, contentType: EXTENSION_TYPES[extension]
            }
        } else {
            render status: 404
        }
    }

    def getAlerts() {
        def player = Player.findById(session.playerId)
        def alerts = Alert.findAllByPlayer(player).findAll {
            it.targetTime - (1000 * it.leadTime) < System.currentTimeMillis()
        }.collect {
            [
                id: it.id,
                title: it.title,
                message: it.message,
                targetTime: it.targetTime,
                leadTime: it.leadTime,
                isAcknowledged: it.isAcknowledged
            ]
        }
        render alerts as JSON
    }

    def acknowledgeAlert() {
        def player = Player.findById session.playerId
        def alert = Alert.findByIdAndPlayer params.id, player
        if (alert == null) {
            render status: 500
            return
        }

        alert.isAcknowledged = true;
        if (!alert.save(flush: true)) {
            render status: 500
            return
        }

        def ret = [success: true]
        render ret as JSON
    }
}
