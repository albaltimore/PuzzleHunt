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
                login: it.name,
                priorityLine: it.getStatus()?.priorityLine,
                solved: it.getSolvedPuzzles()*.name,
                unlocked: it.getSolvablePuzzles()*.name,
            ]
        }
        def puzzles = Puzzle.list()


        def showAll = Player.findById(session.playerId).role != "PRINTER"

        if (!showAll) {
            def introHack = 'Intro'
            puzzles = puzzles.findAll { !it.disableHint || it.name.endsWith(introHack) }
        }

        def ret = [ players: players, puzzles: puzzles*.name, showAll: showAll ]
        render ret as JSON
    }
}

