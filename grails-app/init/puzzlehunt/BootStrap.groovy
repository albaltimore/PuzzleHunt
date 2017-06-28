package puzzlehunt

import grails.core.GrailsApplication
import groovy.json.JsonSlurper

class BootStrap {

    GrailsApplication grailsApplication

    def init = { servletContext ->
        loadFromPath()
        
        def firstRound = new Round(name: 'ROUND ONE', width: 10, height: 10)
        
        def a = new Puzzle(xCor:100, yCor:100, name:'Puzzle A', solution:"A", round: firstRound)
        def b = new Puzzle(xCor:300, yCor:100, name:'Puzzle B', solution:"B", round: firstRound)
        def c = new Puzzle(xCor:500, yCor:100, name:'Puzzle C', solution:"C", round: firstRound)
        def d = new Puzzle(xCor:300, yCor:200, name:'Puzzle D', solution:"D", requiredPuzzles: [b], round: firstRound)
        def e = new Puzzle(xCor:200, yCor:400, name:'Puzzle E', solution:"E", requiredPuzzles: [a, d], round: firstRound)
        def f = new Puzzle(xCor:400, yCor:400, name:'Puzzle F', solution:"F", requiredPuzzles: [c, d], round: firstRound)
        def g = new Puzzle(xCor:400, yCor:500, name:'Puzzle G', solution:"G", requiredPuzzles: [f], round: firstRound)
        def h = new Puzzle(xCor:200, yCor:600, name:'Puzzle H', solution:"H", requiredPuzzles: [e, g], round: firstRound)
        def i = new Puzzle(xCor:300, yCor:800, name:'Puzzle I', solution:"I", requiredPuzzles: [g, h], round: firstRound)

        a.save()
        b.save()
        c.save()
        d.save()
        e.save()
        f.save()
        g.save()
        h.save()
        i.save()

        def pl = new Player(name:"Aleks", password:"1", role: "HINTER")
        def ss = new Player(name: "Stephanie", password:"2", role: "HINTER")
        def uu = new Player(name: "Ania", password:"0", role: "PLAYER")
        def dd = new Player(name: "Josh", password:"2", role: "HINTER")
        def vv = new Player(name: "Ben", password:"2", role: "HINTER")

        pl.save()
        ss.save()
        uu.save()
        dd.save()
        vv.save()
        
        def today = new Date().parse('yyyy/MM/dd', '2017/01/30')
        def yesterday = new Date().parse('yyyy/MM/dd', '2017/01/29')
        def oldest = new Date().parse('yyyy/MM/dd', '2001/01/29')
        
        def h_a = new Hint(player:pl, puzzle:a, owner: ss, lastOwner: uu, question:"I need help with this", notes: "NOTES", createTime: today)
        def h_b = new Hint(player:pl, puzzle:b, owner: uu, question:"I need help again", notes:"NOTES", createTime: today)
        def h_c = new Hint(player:pl, puzzle:c, owner:pl, question:"I need more help", notes:"NOTES", createTime: today)
        def h_d = new Hint(player:pl, puzzle:d, owner:uu, question:"I have another question", notes:"NOTES", createTime: today)
        def h_e = new Hint(player:pl, puzzle:e, owner:uu, question:"I have another question", notes:"NOTES", createTime: today)
        def h_f = new Hint(player:pl, puzzle:a, owner:dd, question:"I have another question", notes:"NOTES", createTime: yesterday)
        def h_g = new Hint(player:pl, puzzle:a, owner:vv, lastOwner: vv, question:"I have another question", notes:"NOTES", createTime: oldest)
        def h_h = new Hint(player:pl, puzzle:b, owner:ss, question:"I have another question", notes:"NOTES", createTime: yesterday)
        def h_i = new Hint(player:pl, puzzle:b, owner:dd, lastOwner: ss, question:"I have another question", notes:"NOTES", createTime: oldest)
        h_a.save()
        h_b.save()
        h_c.save()
        h_d.save()
        h_e.save()
        h_f.save()
        h_g.save()
        h_h.save()
        h_i.save()

        def ts = new Player(name: "Tom", password:"3")
        ts.save()
    }
    def destroy = {
    }

    def loadFromPath() {

        def bootstrapPath = grailsApplication.config.getProperty("puzzlehunt.bootstrapPath")

        if (Puzzle.list().size()) return

        if (!bootstrapPath) return

        File f = new File(bootstrapPath + "/bootstrap.json")

        if(!f.exists()) {
            System.err.println "Could not find bootstrap manifest"
            return
        }

        def config = new JsonSlurper().parse(f)

        def players = [:]
        def puzzles = [:]
        def resources = [:]
        def rounds = [:]

        config.rounds.each {
            rounds[it.id] = new Round(name: it.name, width: it.width, height: it.height)
        }

        config.puzzles.each {
            puzzles[it.id] = new Puzzle(xCor: it.xcor, yCor: it.ycor, name: it.name, solution: it.solution, round: rounds[it.round], timeLimit: it.timeLimit ?: null)
            puzzles[it.id].partialSolutions = it.hints.collect { k, v ->
                def ptl = new PartialSolution(trigger: k, hint: v ?: "You're on the right track, keep going!")
                ptl.save()
                ptl
            }
            puzzles[it.id].save()
        }

        config.players.each {
            players[it.name] = new Player(name: it.name, password: it.password, round: rounds[it.round], role: it.role)
            players[it.name].save()
        }

        config.resources.each {
            resources[it.id] = new Resource(puzzle: puzzles[it.puzzle], filename: it.file, accessor: UUID.randomUUID().toString(), mustSolve: it.mustSolve ?: false)
            resources[it.id].save()
        }

        config.rounds.each {
            rounds[it.id].background = resources[it.background]
        }

        config.puzzles.each {
            puzzles[it.id].introResource = resources[it.introResource]
            if (it.solvedResource) puzzles[it.id].solvedResource = resources[it.solvedResource]

            puzzles[it.id].requiredPuzzles = it.requires.collect {pid ->
                def rp = new RequiredPuzzle(puzzle: puzzles[pid.puzzle], color: pid.color)
                rp.coordinates = pid?.path.collect { point ->
                    def co = new Coordinate(xCor: point.xcor, yCor: point.ycor)
                    co.save()
                    co
                }
                rp.save()
                rp
            }

            println "puzzles ${puzzles[it.id].name} ${puzzles[it.id].requiredPuzzles*.puzzle*.name}"
            puzzles[it.id].save()
        }

        players.each {k,v->v.save(flush:true)}
        puzzles.each{k,v->v.save(flush:true)}
        resources.each{k,v->v.save(flush:true)}
        rounds.each{k,v->v.save(flush:true)}

        println players
        println puzzles
        println resources
    }
}
