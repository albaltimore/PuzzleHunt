package puzzlehunt

import grails.converters.JSON

class HintController {

    def index() {

    }
    
    def getHints() {
        def pl = Player.findById(session.playerId)
        def ownedList = Hint.findByOwnerAndClosedNotEqual(pl,true)
        def ohl = []
        if (ownedList) {
            // TODO - is there a way to do this together with findAll (without for loop)
            for (def h in ownedList) {
                def oh = [
                    id: h.id,
                    question: h.question,
                    puzzle: h.puzzle.name,
                    player: h.player.name,
                    owner: h.owner ? h.owner.name : "--",
                    lastOwner: h.lastOwner ? h.lastOwner.name : "--",
                    status: h.owner ? "unclaim" : "claim",
                    action : h.closed ? "closed" : "open",
                    createTime : h.createTime,
                    open : h.closed ? false : true,
                    orphan : h.owner ? false : true
                ]
                ohl.push(oh)
            }
        }
        def hintList = Hint.findAll("from Hint as h order by h.owner desc, h.createTime desc")
        def hdl = []
        // TODO - is there a way to do this together with findAll (without for loop)
        for (def h in hintList) {
            def hd = [
                id: h.id,
                question: h.question,
                puzzle: h.puzzle.name,
                player: h.player.name,
                owner: h.owner ? h.owner.name : "--",
                lastOwner: h.lastOwner ? h.lastOwner.name : "--",
                status: h.owner ? "unclaim" : "claim",
                action : h.closed ? "closed" : "open",
                createTime : h.createTime,
                open : h.closed ? false : true,
                orphan : h.owner ? false : true
            ]
            hdl.push(hd)
        }
        
        def ret = [hints : hdl, owned : ohl]
        render ret as JSON
    }

    def requestHint() {
        def pl = Player.findById(params.playerid)
        def pu = Puzzle.findById(params.puzzleid)
        
        def prevHints = Hint.findAll("from Hint as h where h.puzzle=:puzzle and\n\
                                      h.player=:player order by h.createTime desc",
            [puzzle: pu, player: pl], [max: 1])
        if (prevHints && prevHints.owner) {
            def lastHinter = prevHints.owner
            if (lastHinter) {
                lh = lastHinter
            }
        }
        def hn = new Hint(player:pl, 
            puzzle:pu, 
            owner: uu, 
            lastOwner: lh,
            question: params.question, 
            notes: "NO NOTES")
        hn.save()
    }

    def refreshlist() {
        redirect controller: "hint", action: "index"
    }
    
    def toggle() {
        def uh = Hint.findById(params.hintid)
        if (!uh.owner && !uh.closed) {
            redirect controller: "hint", action: "details", 
            params: [hintid: params.hintid, 
                notice: "cannot close unclaimed hint"]
        }
        else {
            if (uh.closed) {
                uh.closed = false
            }
            else {
                uh.closed = true
            }
            uh.save(flush : true)
            redirect controller: "hint", action: "details", 
            params: [hintid: params.hintid, 
                notice: "ticket state updated"]
        }
    }

    def claim() {
        def ret = [ owner : "--", action : "claim" ]
        def uh = Hint.findById(params.hintid)
        if (uh.owner) {
            println "Hint already claimed"
        }
        else {
            def ap = Player.findById(session.playerId)
            def ownedList = Hint.findAll("FROM Hint as h WHERE h.owner=:owner AND\n\
                                          (h.closed IS NULL or h.closed=FALSE)",
                [owner: ap])

            if (ownedList) {
                println "Hinter cannot claim additional hints"
                ret = [ error : "MAX" ]
            }
            else {
                if (ap) {
                    // claim hint
                    def count = Hint.executeUpdate("UPDATE Hint h SET h.owner = :owner \n\
                                                    WHERE h.id = int(:hintid) AND h.owner = null", 
                        [owner: ap, hintid: params.hintid])
                    if (count > 0) {
                        ret = [ owner : ap.name, action : "unclaim" ]
                    } else {
                        def nh = Hint.findById(params.hintid)
                        def name = (nh && nh.owner) ? nh.owner.name : "ERROR RELOAD"
                        ret = [owner : name, action : "unclaim"]
                    }
                }
            }
        }
        
        render ret as JSON;
    }
    
    def claimDetail() {
        def retMsg = ""
        def uh = Hint.findById(params.hintid)
        if (uh.owner) {
            retMsg = "Hint already claimed"
        }
        else {
            def ap = Player.findById(session.playerId)
            def ownedList = Hint.findAll("FROM Hint as h WHERE h.owner=:owner AND\n\
                                          (h.closed IS NULL or h.closed=FALSE)",
                [owner: ap])

            if (ownedList) {
                println "Hinter cannot claim additional hints"
                retMsg = "Hinter cannot claim additional hints"
            }
            else {
                if (ap) {
                    // claim hint
                    def count = Hint.executeUpdate("UPDATE Hint h SET h.owner = :owner \n\
                                                    WHERE h.id = int(:hintid) AND h.owner = null", 
                        [owner: ap, hintid: params.hintid])
                    if (count > 0) {
                        retMsg = "Hint request claimed"
                    } else {
                        def nh = Hint.findById(params.hintid)
                        def name = (nh && nh.owner) ? nh.owner.name : "ERROR RELOAD"
                        retMsg = "Hint already claimed"
                    }
                }
            }
        }
        redirect controller: "hint", action: "details", 
            params: [hintid: params.hintid, 
                     notice: retMsg]
    }
    
    def unclaim() {
        def uh = Hint.findById(params.hintid)
        def ap = Player.findById(session.playerId)
        def ret = [ owner : "--", action : "claim" ]
        // unclaim hint
        uh.owner = null
        uh.save(flush : true)
        
        render ret as JSON;
    }
    
    def details() {
        if (params.hintid) {
            println "showing details hintid:" + params.hintid
            def uh = Hint.findById(params.hintid)
            def hinterName = uh.owner ? uh.owner.name : "--"
            def actionBlob = uh.closed ? "re-open" : "done"
            render(view: "details", model: [hintid: params.hintid,
                    hinterName: hinterName,
                    playerName: uh.player.name,
                    phone: uh.phone,
                    nexi: uh.nexi,
                    puzzleName: uh.puzzle.name,
                    question: uh.question,
                    puzzleLink: uh.puzzle?.solvedResource?.accessor,
                    solution: uh.puzzle.solution,
                    notes: uh.notes,
                    notice: params.notice,
                    action : uh.closed ? "re-open" : "done"])
        }
        else {
            println "error, no hintid passed"
        }
    }
    
    def updateNote() {
        println "param entry notes"
        def uh = Hint.findById(params.hintid)
        uh.notes = params.entrynotes
        uh.save(flush : true)
        redirect controller: "hint", action: "details", 
        params: [hintid: params.hintid, 
            notice: "note updated"]
    }

}
