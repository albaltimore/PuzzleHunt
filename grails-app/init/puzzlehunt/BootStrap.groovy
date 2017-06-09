package puzzlehunt

import grails.core.GrailsApplication
import groovy.json.JsonSlurper

class BootStrap {

    GrailsApplication grailsApplication

    def init = { servletContext ->
        loadFromPath()
        
        def roundzero = new Round(name: "ZERO", width: 50, height: 50)

        def a = new Puzzle(xCor:100, yCor:100, name:'Puzzle A', solution:"A", round:roundzero)
        def b = new Puzzle(xCor:300, yCor:100, name:'Puzzle B', solution:"B", round:roundzero)
        def c = new Puzzle(xCor:500, yCor:100, name:'Puzzle C', solution:"C", round:roundzero)
        def d = new Puzzle(xCor:300, yCor:200, name:'Puzzle D', solution:"D", requiredPuzzles: [b], round:roundzero)
        def e = new Puzzle(xCor:200, yCor:400, name:'Puzzle E', solution:"E", requiredPuzzles: [a, d], round:roundzero)
        def f = new Puzzle(xCor:400, yCor:400, name:'Puzzle F', solution:"F", requiredPuzzles: [c, d], round:roundzero)
        def g = new Puzzle(xCor:400, yCor:500, name:'Puzzle G', solution:"G", requiredPuzzles: [f], round:roundzero)
        def h = new Puzzle(xCor:200, yCor:600, name:'Puzzle H', solution:"H", requiredPuzzles: [e, g], round:roundzero)
        def i = new Puzzle(xCor:300, yCor:800, name:'Puzzle I', solution:"I", requiredPuzzles: [g, h], round:roundzero)

        a.save()
        b.save()
        c.save()
        d.save()
        e.save()
        f.save()
        g.save()
        h.save()
        i.save()

        println "adding players"
        def pl = new Player(name:"Aleks", password:"1", role: "HINTER")
        def ss = new Player(name: "Stephanie", password:"2", role: "PLAYER")
        
        pl.save()
        ss.save()
        println "adding hints"
        def h_a = new Hint(player:pl, puzzle:a, owner: ss, question:"I need help with this", notes: "NO NOTES")
        def h_b = new Hint(player:pl, puzzle:a, question:"I need help again", notes:"NO NOTES")
        def h_c = new Hint(player:pl, puzzle:a, question:"I need more help", notes:"NO NOTES")
        def h_d = new Hint(player:pl, puzzle:a, question:"I have another question", notes:"NO NOTES")
        
        h_a.save()
        h_b.save()
        h_c.save()
        h_d.save()
        println "done adding hints"
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
