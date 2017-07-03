package puzzlehunt

import grails.converters.JSON

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
        def timedStarted = PuzzleStart.findAllByPlayer player collect {println it.puzzle.name; it.puzzle.id}

        def rounds = [:]
        def puzzles = player.solvablePuzzles.collect { p ->
            def started = p.timeLimit ? (p.id in timedStarted) : true
            def startTime = p.id in timedStarted ? PuzzleStart.findByPlayerAndPuzzle(player, p).startTime : null
            if (!(p.round.id in rounds)) {
                rounds[p.round.id] = [
                    id: p.round.id,
                    name: p.round.name,
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
                requiredPuzzles: p.requiredPuzzles.collect {rp -> [id: rp.puzzle.id, color: rp.color, points: rp.coordinates.collect {c -> [xCor: c.xCor, yCor: c.yCor]} ]},
                solved: p.id in solved,
                timeLimit: p.timeLimit,
                started: started,
                startTime: startTime,
                roundId: p.round.id,
                introAccessor: started ? p?.introResource?.accessor : null,
                introFilename: started ? p?.introResource?.filename : null,
                solvedAccessor: p.id in solved ? p?.solvedResource?.accessor : null,
                solvedFilename: p.id in solved ? p?.solvedResource?.filename : null,
            ]
        }
        def ret = [puzzles: puzzles, rounds: rounds.values()]
        render ret as JSON
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

    def nextHintTime() {
        def player = Player.findById(session.playerId)
        def val = player.lastHint + player.hintRegen
        render val
    }

    def requestHint() {
        //TODO CHECK PUZZLE IS ACCESSIBLE
        def player = Player.findById(session.playerId)



        if (System.currentTimeMillis() < player.lastHint + player.hintRegen) {
            def ret = [error: "Hint requested too soon. Try later."]
            render ret as JSON
            return
        }

        def puzzle = Puzzle.findById(params.id)

        if (player.hasSolved(puzzle) || !player.isSolvable(puzzle)) {
            def ret = [error: "You can't request a hint for this puzzle."]
            render ret as JSON
            return
        }

        def hn = new Hint(player:player, puzzle:puzzle, question: params.question)
        hn.save()
        player.lastHint = System.currentTimeMillis()
        player.save(flush: true)

        def ret = [success:true]
        render ret as JSON
    }

    def checkPuzzle() {
        def player = Player.findById session.playerId
        if (System.currentTimeMillis() <= player.lastSubmission  + (grailsApplication.config.getProperty("puzzlehunt.puzzleTimeout") as Long) ) {
            def ret = [solved: false, message: "Too many submissions at once"]
            render ret as JSON
            return
        }

        def puzzle = Puzzle.findById(params.id)

        if (puzzle.timeLimit) {
            def pstart = PuzzleStart.findByPuzzleAndPlayer(puzzle, player)
            if (!pstart) {
                def ret = [solved: false, message: "Puzzle not started"]
                render ret as JSON
                return
            }
            if (pstart.startTime + puzzle.timeLimit * 1000 < System.currentTimeMillis()) {
                def ret = [solved: false, message: "You're out of time :("]
                render ret as JSON
                return
            }
        }


        def attempt = new Attempt(player: player, puzzle: puzzle, answer: params.solution, timestamp: System.currentTimeMillis())
        attempt.save(flush: true)
        println "guess ${player.name} ${puzzle.name} ${puzzle.solution} ${params.solution}"

        def c = attempt.isCorrect

        def ret = [solved: c]
        if (!c) ret.message = puzzle.getPartialSolution(params.solution) ?: "Incorrect"
        render ret as JSON
    }

    def getResource() {
        def bootstrapPath = grailsApplication.config.getProperty("puzzlehunt.resourcePath")
        def rs = Resource.findByAccessor(params.accessor)
        def player = Player.findById session.playerId

        if (rs && (!rs.puzzle || player.hasSolved(rs.puzzle) ||
                (!rs.mustSolve && player.isSolvable(rs.puzzle)))) {
            def f = new File("${bootstrapPath}/${rs.filename}")
            def extension = rs.filename.substring(rs.filename.lastIndexOf(".") + 1).toLowerCase()

            render file:f, contentType: EXTENSION_TYPES[extension]
        } else {
            render status: 404
        }
    }
}
