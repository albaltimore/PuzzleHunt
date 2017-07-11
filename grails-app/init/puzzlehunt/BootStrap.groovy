package puzzlehunt

import grails.core.GrailsApplication
import groovy.json.JsonSlurper

class BootStrap {

    GrailsApplication grailsApplication
    def propertiesService

    def init = { servletContext ->
        loadFromPath()
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
        def statuses = []

        config.rounds.each {
            rounds[it.id] = new Round(name: it.name, width: it.width, height: it.height)
        }

        config.puzzles.each {
            puzzles[it.id] = new Puzzle(xCor: it.xcor, yCor: it.ycor, name: it.name, solution: it.solution, round: rounds[it.round], timeLimit: it.timeLimit ?: null, disableHint: it.disableHint ?: false, statusBoost: it.statusBoost ?: false)
            puzzles[it.id].partialSolutions = it.hints.collect { k, v ->
                def ptl = new PartialSolution(partialSolution: k, hint: v ?: "You're on the right track, keep going!")
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
            resources[it.id] = new Resource(puzzle: puzzles[it.puzzle], filename: it.file, accessor: UUID.randomUUID().toString(), mustSolve: it.mustSolve ?: false, role: it.role ?: null)
            resources[it.id].save()
        }

        config.rounds.each {
            rounds[it.id].requiredPuzzles = it.requiredPuzzles.collect {i -> puzzles[i]}
            rounds[it.id].background = resources[it.background]
        }

        config.puzzles.each {
            puzzles[it.id].introResource = resources[it.introResource]
            if (it.solvedResource) puzzles[it.id].solvedResource = resources[it.solvedResource]
            if (it.solutionResource) puzzles[it.id].solutionResource = resources[it.solutionResource]

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

        if (config.favicon != null) {
            propertiesService.favicon = resources[config.favicon]
        }

        config.statuses.each {
            println "add status ${it}"
            new PlayerStatus(statusLevel: it.level, resource: resources[it.resource], name: it.name, hintCount: it.hintCount ?: 0, hintTime: it.hintTime ?: 0, puzzleTime: it.puzzleTime ?: 0).save()
        }

        players.each {k,v->v.save(flush:true)}
        puzzles.each{k,v->v.save(flush:true)}
        resources.each{k,v->v.save(flush:true)}
        rounds.each{k,v->v.save(flush:true)}

        println "favicon ${config.favicon}  ${propertiesService.favicon}"

        //        println players
        //        println puzzles
        //        println resources
    }
}
