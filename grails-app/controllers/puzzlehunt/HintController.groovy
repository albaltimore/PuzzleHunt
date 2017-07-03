package puzzlehunt

import grails.converters.JSON

class HintController {

    def index() {
        def list = Hint.list().collect { h ->
            [id: h.id,
                question: h.question,
                puzzle: h.puzzle.name,
                player: h.player.name,
                owner: h.owner ? h.owner.name : "--"]
        }

        [ list : list ]
    }

    def requestHint() {
        def pl = Player.findByNameAndId(params.playername, params.playerid)
        def pu = Puzzle.findByNameAndId(params.puzzlename, params.puzzleid)
        def hn = new Hint(player:pl, puzzle:pu, owner: uu, question: params.question, notes: "NO NOTES")
        hn.save()
    }

    def refreshlist() {
        redirect controller: "hint", action: "index"
    }

    def claim() {
        def uh = Hint.findById(params.hintid)
        def ap = Player.findById(session.playerId)
        if (ap && uh)
        {
            println "claiming hint request"
            uh.owner = ap;
            uh.save(flush : true);
        }
        redirect controller: "hint", action: "index"
    }

}
