package puzzlehunt

import grails.converters.JSON

class PlayerController {

    def index() {
    }

    def getPuzzles() {
        def solved = []
        Player.findById session.playerId solvedPuzzles.each {p -> solved << p}
        def ret = Puzzle.list().collect { p ->
            [id: p.id, xCor: p.xCor, yCor: p.yCor, name: p.name, requiredPuzzles: p.requiredPuzzles.collect {rp -> rp.id}, solved: p in solved]
        }
        render ret as JSON
    }

    def checkPuzzle() {
        def puzzle = Puzzle.findById(params.id)
        def player = Player.findById session.playerId
        def c = puzzle.solution == params.solution
        if (c) {
            player.lock()
            player.addToSolvedPuzzles(puzzle)
            player.save(flush: true)
        }
        def ret = [solved: c]
        render ret as JSON
    }
}
