package puzzlehunt

import grails.core.GrailsApplication
import groovy.json.JsonSlurper

class BootStrap {

    GrailsApplication grailsApplication

    def init = { servletContext ->
        loadFromPath()

    }
    def destroy = {
    }

    def loadFromPath() {
        def bootstrapPath = grailsApplication.config.getProperty("puzzlehunt.bootstrapPath")

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

        config.puzzles.each {
            puzzles[it.id] = new Puzzle(xCor: it.xcor, yCor: it.ycor, name: it.name, solution: it.solution)
            puzzles[it.id].partialSolutions = it.hints.collect { k, v ->
                def ptl = new PartialSolution(trigger: k, hint: v)
                ptl.save()
                ptl
            }

            puzzles[it.id].save()
        }

        config.players.each {
            players[it.name] = new Player(name: it.name, password: it.password)
            players[it.name].save()
        }

        config.resources.each {
            resources[it.id] = new Resource(puzzle: puzzles[it.id], filename: it.file, accessor: UUID.randomUUID().toString())
            resources[it.id].save()
        }

        config.puzzles.each {
            puzzles[it.id].introResource = resources[it.introResource]
            if (puzzles[it.id].solvedResource) puzzles[it.id].solvedResource = resources[it.solvedResource]
            puzzles[it.id].requiredPuzzles = it.requires.collect {pid -> puzzles[pid]}
        }

        println resources

        players.each {k,v->v.save(flush:true)}
        puzzles.each{k,v->v.save(flush:true)}
        resources.each{k,v->v.save(flush:true)}
    }
}
