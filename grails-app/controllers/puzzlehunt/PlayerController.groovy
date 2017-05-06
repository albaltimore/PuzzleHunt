package puzzlehunt

import grails.converters.JSON

class PlayerController {

    def gameService

    def index() {
    }

    def getPuzzles() {
        def solved = Player.get(session.playerId).solvedPuzzles.collect {it.id}
        println solved
        def ret = Puzzle.list().findAll { p ->
            p.id in solved || !p.requiredPuzzles || p.requiredPuzzles.findAll {rp -> rp.id in solved}.size()
        }.collect { p ->
            [id: p.id, xCor: p.xCor, yCor: p.yCor, name: p.name, requiredPuzzles: p.requiredPuzzles.collect {rp -> rp.id}, solved: p.id in solved, accessor:p?.introResource?.accessor]
        }
        render ret as JSON
    }

    def checkPuzzle() {
        def puzzle = Puzzle.findById(params.id)
        def player = Player.findById session.playerId

        def attempt = new Attempt(player: player, puzzle: puzzle, answer: params.solution, timestamp: System.currentTimeMillis())
        attempt.save(flush: true)

        def c = attempt.isCorrect

        def ret = [solved: c]
        render ret as JSON
    }

    def getResource() {
        def bootstrapPath = grailsApplication.config.getProperty("puzzlehunt.bootstrapPath")
        def rs = Resource.findByAccessor(params.accessor)
        def player = Player.findById session.playerId

        if (rs && (player.hasSolved(rs.puzzle) || !rs.puzzle.requiredPuzzles || rs.puzzle.requiredPuzzles.collect {player.hasSolved it}.contains(true))) {
            def f = new File("${bootstrapPath}/${rs.filename}")
            render file:f, contentType: "image/${rs.filename.substring(rs.filename.lastIndexOf(".") + 1)}"
        } else {
            render status: 404
        }
    }
}
