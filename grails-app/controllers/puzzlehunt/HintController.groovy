package puzzlehunt

import grails.converters.JSON

class HintController {

    def index() {
        def list = Hint.list().collect { h ->
            [id: h.id,
             question: h.question,
             puzzle: h.puzzle.name,
             player: h.player.name,
             owner: h.owner ? h.owner.name : "--",
             status: h.owner ? "unlock" : "claim",
             action : h.closed ? "re-open" : "close"]
        }

        [ list : list ]

    }

    def requestHint() {
        def pl = Player.findByNameAndId(params.playername, params.playerid)
        def pu = Puzzle.findByNameAndId(params.puzzlename, params.puzzleid)
        def hn = new Hint(player:pl, 
                          puzzle:pu, 
                          owner: uu, 
                          question: params.question, 
                          notes: "NO NOTES")
        hn.save()
    }

    def refreshlist() {
        redirect controller: "hint", action: "index"
    }
    
    def toggle() {
        def uh = Hint.findById(params.hintid)
        if (uh.closed) {
            uh.closed = false
        }
        else {
            uh.closed = true
        }
        uh.save(flush : true)
        redirect controller: "hint", action: "index"
    }

    def claim() {
        def uh = Hint.findById(params.hintid)
        def ap = Player.findById(session.playerId)
        if (uh.owner) {
            println "unlocking hint request"
            uh.owner = null
            uh.save(flush : true)
        }
        else if (ap && uh) {
            println "claiming hint request"
            uh.owner = ap
            uh.save(flush : true)
        }
        redirect controller: "hint", action: "index"
    }
    
    def details() {
        println "showing details hintid:"
        def uh = Hint.findById(params.hintid)
        render(view: "details", model: [hintid: params.hintid,
                                        playerName: uh.player.name,
                                        phone: uh.phone,
                                        nexi: uh.nexi,
                                        puzzleName: uh.puzzle.name,
                                        question: uh.question,
                                        puzzleLink: "<link to puzzle>",
                                        solution: "<link to solution>",
                                        notes: uh.notes])
    }
    
    def updateNote() {
        println "param entry notes"
        def uh = Hint.findById(params.hintid)
        uh.notes = params.entrynotes
        uh.save(flush : true)
        redirect controller: "hint", action: "index"
    }
    
}
