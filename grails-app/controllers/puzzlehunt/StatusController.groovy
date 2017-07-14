package puzzlehunt

import grails.converters.JSON
import java.text.SimpleDateFormat

class StatusController {

    def index() {
    }

    def getStatus() {
        def players = Player.findAllByRole(null).collect {
            [
                name: it.description,
                priorityLine: it.getStatus()?.priorityLine,
                solved: it.getSolvedPuzzles()*.name,
                unlocked: it.getSolvablePuzzles()*.name,
            ]
        }
        def puzzles = Puzzle.list()

        if (Player.findById(session.playerId).role == "PRINTER") {
            puzzles = puzzles.findAll { ! it.disableHint }
        }

        def ret = [ players: players, puzzles: puzzles*.name ]
        //String timestamp = new SimpleDateFormat("yyyyMMdd-HH:mm:ss").format(new Date());
        //println timestamp+" Game status fetched"
        render ret as JSON
    }
}

