package puzzlehunt

import grails.converters.JSON

class PlayerController {

    def gameService

    def index() {
    }

    def getPuzzles() {
        def solved = Player.get(session.playerId).solvedPuzzles.collect {it.id}
        println solved
        def ret = Puzzle.list().collect { p ->
            [id: p.id, xCor: p.xCor, yCor: p.yCor, name: p.name, requiredPuzzles: p.requiredPuzzles.collect {rp -> rp.id}, solved: p.id in solved]
        }
        render ret as JSON
    }

    def checkPuzzle() {
        def puzzle = Puzzle.findById(params.id)
        def player = Player.findById session.playerId

        def attempt = new Attempt(player: player, puzzle: puzzle, answer: params.solution)
        attempt.save(flush: true)

        def c = attempt.isCorrect

        def ret = [solved: c]
        render ret as JSON
    }
}
