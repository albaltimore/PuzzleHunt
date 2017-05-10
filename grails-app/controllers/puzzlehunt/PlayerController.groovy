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
    ]



    def getPuzzles() {
        def solved = Player.get(session.playerId).solvedPuzzles.collect {it.id}
        println solved
        def ret = Puzzle.list().findAll { p ->
            p.id in solved || !p.requiredPuzzles || p.requiredPuzzles.findAll {rp -> rp.id in solved}.size()
        }.collect { p ->
            [
                id: p.id,
                xCor: p.xCor,
                yCor: p.yCor,
                name: p.name,
                requiredPuzzles: p.requiredPuzzles.collect {rp -> rp.id},
                solved: p.id in solved,
                introAccessor:p?.introResource?.accessor,
                introFilename: p?.introResource?.filename,
                solvedAccessor: p.id in solved ? p?.solvedResource?.accessor : null,
                solvedFilename: p.id in solved ? p?.solvedResource?.filename : null,
            ]
        }
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

        def attempt = new Attempt(player: player, puzzle: puzzle, answer: params.solution, timestamp: System.currentTimeMillis())
        attempt.save(flush: true)

        def c = attempt.isCorrect

        def ret = [solved: c]
        if (!c) ret.message = puzzle.getPartialSolution(params.solution) ?: "Incorrect"
        render ret as JSON
    }

    def getResource() {
        def bootstrapPath = grailsApplication.config.getProperty("puzzlehunt.resourcePath")
        def rs = Resource.findByAccessor(params.accessor)
        def player = Player.findById session.playerId

        println "${rs} ${rs.puzzle.name} ${player.hasSolved(rs.puzzle)}"
        if (rs && (player.hasSolved(rs.puzzle) ||
                (!rs.mustSolve && (!rs.puzzle.requiredPuzzles || rs.puzzle.requiredPuzzles.collect {player.hasSolved it}.contains(true))))) {
            def f = new File("${bootstrapPath}/${rs.filename}")
            def extension = rs.filename.substring(rs.filename.lastIndexOf(".") + 1).toLowerCase()

            render file:f, contentType: EXTENSION_TYPES[extension]
        } else {
            render status: 404
        }
    }
}
