package puzzlehunt

import grails.converters.JSON
import grails.gorm.transactions.Transactional

class PlayerController {

    def gameService

    def index() {
    }

    private static final EXTENSION_TYPES = [
            "png": "image/png",
            "jpg": "image/jpg",
            "gif": "image/gif",
            "pdf": "application/pdf",
            "mp4": "video/mp4"
    ]

    def getPuzzles() {
        def player = Player.get(session.playerId)
        def solved = player.solvedPuzzles*.id
        def timedStarted = PuzzleStart.findAllByPlayer player collect { it.puzzle.id }

        def rounds = [:]
        def stat = player.status
        def statPoints = player.statusPoints

        def puzzles = player.solvablePuzzles.collect { p ->
            def started = p.timeLimit ? (p.id in timedStarted) : true
            def startTime = p.id in timedStarted ? PuzzleStart.findByPlayerAndPuzzle(player, p).startTime : null
            def timeLimit = p.timeLimit ? p.timeLimit + (player.status?.puzzleTime ?: 0) : null
            def failed = p.timeLimit && started && (startTime + (timeLimit * 1000) < System.currentTimeMillis())

            if (!(p.round.id in rounds)) {
                rounds[p.round.id] = [
                        id        : p.round.id,
                        name      : p.round.name,
                        floorId   : p.round.floorId,
                        background: p.round.background.accessor,
                        width     : p.round.width,
                        height    : p.round.height,
                ]
            }
            [
                    id              : p.id,
                    xCor            : p.xCor,
                    yCor            : p.yCor,
                    name            : p.name,
                    requiredPuzzles : p.requiredPuzzles.collect { rp ->
                        [
                                id          : rp.puzzle.id,
                                color       : rp.color,
                                points      : rp.coordinates.collect { c -> [xCor: c.xCor, yCor: c.yCor] },
                                pathResource: rp.pathResource?.collect { pr -> [resource: pr.resource.accessor, xCor: pr.xCor, yCor: pr.yCor] },
                        ]
                    },
                    pathResource    : p.pathResource?.collect { pr -> [resource: pr.resource.accessor, xCor: pr.xCor, yCor: pr.yCor] },
                    hintDisabled    : p.disableHint,
                    solved          : p.id in solved,
                    timeLimit       : timeLimit,
                    started         : started,
                    startTime       : startTime,
                    roundId         : p.round.id,
                    introAccessor   : started ? p?.introResource?.accessor : null,
                    introFilename   : started ? p?.introResource?.filename : null,
                    solvedAccessor  : p.id in solved ? p?.solvedResource?.accessor : null,
                    solvedFilename  : p.id in solved ? p?.solvedResource?.filename : null,
                    iconAccessor    : p.id in solved ? p?.iconSolvedResource?.accessor : (failed ? p.iconFailedResource?.accessor : p?.iconReadyResource?.accessor),
                    hasHintResources: p.hintResources.asBoolean()
            ]
        }

        def status = stat ? [
                resource    : stat.resource.accessor,
                name        : stat.name,
                hintTime    : stat.hintTime,
                puzzleTime  : stat.puzzleTime,
                hintCount   : stat.hintCount,
                priorityLine: stat.priorityLine,
                level       : stat.statusLevel,
                points      : statPoints
        ] : null



        def ret = [puzzles: puzzles, rounds: rounds.values(), status: status, contactInfo: player.contactInfo]
        render ret as JSON
    }

    def getInstructions() {
        def instructions = Instruction.list(sort: 'orderNumber').collect {
            [name: it.name, resource: it.resource.accessor]
        }
        render instructions as JSON
    }

    def startTimedPuzzle() {
        def player = Player.findById(session.playerId)
        def puzzle = Puzzle.findById(params.id)
        if (player.hasSolved(puzzle) || !player.isSolvable(puzzle)) {
            render status: 404
            return
        }

        new PuzzleStart(puzzle: puzzle, player: player, startTime: System.currentTimeMillis()).save(flush: true)
    }

    def getAlerts() {
        def player = Player.findById(session.playerId)
        def alerts = Alert.findAllByPlayer(player).findAll {
            it.targetTime - (1000 * it.leadTime) < System.currentTimeMillis()
        }.collect {
            [
                    id            : it.id,
                    title         : it.title,
                    message       : it.message,
                    targetTime    : it.targetTime,
                    leadTime      : it.leadTime,
                    isAcknowledged: it.isAcknowledged
            ]
        }
        render alerts as JSON
    }

    def nextHintTime() {
        def player = Player.findById(session.playerId)
        def maxHints = player.hintMaxCount + (player.status?.hintCount ?: 0)
        def totalTime = player.hintRegen - (player.status?.hintTime ?: 0) * 1000

        def start = (Property.findByName('START')?.value ?: 0) as Long
        def left = (1..maxHints).findAll {
            def targetStart = System.currentTimeMillis() - totalTime * it
            (it - Hint.countByPlayerAndCreateTimeGreaterThan(player, targetStart) - ((start > targetStart) ? 1 : 0)) > 0
        }.size()

        def recentHints = Hint.findAllByPlayerAndCreateTimeGreaterThan(player, System.currentTimeMillis() - totalTime) ?: []
        if (start > System.currentTimeMillis() - totalTime) recentHints.add(new Hint(createTime: start))

        def ret = [
                max : maxHints,
                left: left,
                time: !recentHints.size() ? 0 : (recentHints*.createTime.max() ?: 0) + totalTime
        ]

        render ret as JSON
    }

    @Transactional
    def requestHint() {
        def player = Player.findById(session.playerId)

        def totalTime = player.hintRegen - (player.status?.hintTime ?: 0) * 1000
        def maxHints = player.hintMaxCount + (player.status?.hintCount ?: 0)

        def start = (Property.findByName('START')?.value ?: 0) as Long
        def left = (1..maxHints).findAll {
            def targetStart = System.currentTimeMillis() - totalTime * it
            (it - Hint.countByPlayerAndCreateTimeGreaterThan(player, targetStart) - ((start > targetStart) ? 1 : 0)) > 0
        }.size()

        if (params.contactInfo) {
            player.contactInfo = params.contactInfo
            player.save(flush: true)
        }

        if (!left) {
            def ret = [error: "Hint requested too soon. Try later."]
            render ret as JSON
            return
        }

        def puzzle = Puzzle.findById(params.id)

        if (player.hasSolved(puzzle) || !player.isSolvable(puzzle) || puzzle.disableHint) {
            def ret = [error: "You can't request a hint for this puzzle."]
            render ret as JSON
            return
        }

        def hn = new Hint(player: player, puzzle: puzzle, question: params.question, contactInfo: params.contactInfo)
        hn.save()

        player.save(flush: true)

        def ret = [success: true]
        render ret as JSON
    }

    def checkPuzzle() {
        def player = Player.findById session.playerId
        if (System.currentTimeMillis() <= player.lastSubmission + (grailsApplication.config.puzzlehunt.puzzleTimeout as Long)) {
            def ret = [solved: false, message: "Too many submissions at once"]
            render ret as JSON
            return
        }

        Puzzle puzzle = Puzzle.findById(params.id)

        if (puzzle.timeLimit) {
            def pstart = PuzzleStart.findByPuzzleAndPlayer(puzzle, player)
            if (!pstart) {
                def ret = [solved: false, message: "Puzzle not started"]
                render ret as JSON
                return
            }
            if (pstart.startTime + (puzzle.timeLimit + (player.status?.puzzleTime ?: 0)) * 1000 < System.currentTimeMillis()) {
                def ret = [solved: false, message: "You're out of time :("]
                render ret as JSON
                return
            }
        }

        def c = Attempt.checkIfCorrect(puzzle, params.solution)

        def attempt = new Attempt(player: player, puzzle: puzzle, answer: params.solution, isCorrect: c, timestamp: System.currentTimeMillis())
        attempt.save(flush: true)
        println "guess ${player.name} ${puzzle.name} ${puzzle.solution} ${params.solution}"

        def ret = [solved: c]
        if (!c) ret.message = puzzle.getPartialSolution(params.solution) ?: "Incorrect"
        render ret as JSON
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

    def getHintResources() {
        Player player = Player.findById session.playerId
        Puzzle puzzle = Puzzle.findById params.id

        if (!player.isSolvable(puzzle)) {
            render status: 500
            return
        }

        def unlockTime = player.getUnlockTime(puzzle)

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
            if (unlockTime + it.seconds * 1000 > System.currentTimeMillis()) [unlockTime: unlockTime + it.seconds]
            else [description: it.description, accessor: it.resource?.accessor, filename: it?.resource?.filename]
        }

        render ret as JSON
    }

    def getResource() {
        def bootstrapPath = grailsApplication.config.puzzlehunt.resourcePath
        def rs = Resource.findByAccessor(params.accessor)
        def player = Player.findById session.playerId

        if (rs && !rs.role && (!rs.puzzle || player.hasSolved(rs.puzzle) ||
                (!rs.mustSolve && player.isSolvable(rs.puzzle)))) {

            if (rs.linkUri) {
                redirect url: rs.linkUri
            } else if (rs.filename) {
                def f = new File("${bootstrapPath}/${rs.filename}")
                def extension = rs.filename.substring(rs.filename.lastIndexOf(".") + 1).toLowerCase()

                response.addHeader 'Cache-Control', 'max-age=84600, public'
                render file: f, contentType: EXTENSION_TYPES[extension]
            }
        } else {
            render status: 404
        }
    }
}
