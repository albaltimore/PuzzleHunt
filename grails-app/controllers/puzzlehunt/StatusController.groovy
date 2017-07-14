package puzzlehunt

import grails.converters.JSON

class StatusController {

    def index() {
    }

    def getStatus() {
        def players = Player.list().findResults {
          it.role ? null :
          [
            name: it.description,
            solved: it.getSolvedPuzzles()*.name,
            unlocked: it.getSolvablePuzzles()*.name
          ]
        }
        def puzzles = Puzzle.list()

        // If we're the printer, we only need to see printable puzzles
        //  (which, conveniently, is exactly the puzzles that don't
        //   have hints enabled). Hooray for less screen crowding.
        if (Player.findById(session.playerId).role == "PRINTER") {
          puzzles = puzzles.findAll { ! it.disableHint }
        }

        def ret = [ players: players, puzzles: puzzles*.name ]
        println "Game status fetched"
        render ret as JSON
    }
}

