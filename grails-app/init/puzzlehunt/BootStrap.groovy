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
        println "bootstrapping"
        if (Puzzle.list().size()) return

        if (!bootstrapPath) return
        println "started $bootstrapPath"

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
            rounds[it.id] = new Round(name: it.name, width: it.width, height: it.height, floorId: it.floorId)

            if(!rounds[it.id].save()) {
                println "Failed to save F ${it}"
            }
        }

        println "All round ${rounds}"

        config.puzzles.each {
            puzzles[it.id] = new Puzzle(xCor: it.xcor, yCor: it.ycor, name: it.name, solution: it.solution, round: rounds[it.round], timeLimit: it.timeLimit ?: null, disableHint: it.disableHint ?: false, statusBoost: it.statusBoost ?: false)
            puzzles[it.id].partialSolutions = it.hints.collect { k, v ->
                def ptl = new PartialSolution(partialSolution: k, hint: v ?: "You're on the right track, keep going!")
                if(!ptl.save()) {
                    println "Failed to save AA ${k} ${v} ${puzzles[it.id]}"
                }
                ptl
            }
            if(!puzzles[it.id].save()) {
                def a = puzzles[it.id]
                println "Failed to save A ${it} ${a.name} ${a.solution} ${a.round}"
            }
        }

        config.players.each {
            players[it.name] = new Player(name: it.name, password: it.password, role: it.role, description: it.description, email: it.email ?: null, room: it.room ?: null)
            if(!players[it.name].save()) {
                println "Failed to save B ${it}"
            }
        }

        config.resources.each {
            resources[it.id] = new Resource(puzzle: puzzles[it.puzzle], filename: it.file, accessor: UUID.randomUUID().toString(), linkUri: it.link , mustSolve: it.mustSolve ?: false, role: it.role ?: null)
            if(!resources[it.id].save()){
                println "Failed to save C ${it}"
            }
        }

        config.rounds.each {
            rounds[it.id].requiredPuzzles = it.requiredPuzzles.collect {i -> puzzles[i]}
            rounds[it.id].background = resources[it.background]
        }

        config.puzzles.each {
            puzzles[it.id].introResource = resources[it.introResource]
            if (it.solvedResource) puzzles[it.id].solvedResource = resources[it.solvedResource]
            if (it.solutionResource) puzzles[it.id].solutionResource = resources[it.solutionResource]

            if (it.iconReadyResource) puzzles[it.id].iconReadyResource = resources[it.iconReadyResource]
            if (it.iconSolvedResource) puzzles[it.id].iconSolvedResource = resources[it.iconSolvedResource]
            if (it.iconFailedResource) puzzles[it.id].iconFailedResource = resources[it.iconFailedResource]

            puzzles[it.id].requiredPuzzles = it.requires.collect {pid ->
                def rp = new RequiredPuzzle(puzzle: puzzles[pid.puzzle], color: pid.color)
                rp.coordinates = pid?.path.collect { point ->
                    def co = new Coordinate(xCor: point.xcor, yCor: point.ycor)
                    co.save()
                    co
                }

                if (pid?.pathResource) {
                    println it.pathResource
					rp.pathResource = pid?.pathResource.collect{ point ->
						def co = new Coordinate(xCor: point.xcor, yCor: point.ycor)
						def rs = new PathResource(resource: resources[point.resource], coordinate: co)
						rs.save()
						rs
					}
                }

                if(!rp.save()) {
                    println "Failed to save D $it - $rp"
                }
                rp
            }

            println "puzzles ${puzzles[it.id].name} ${puzzles[it.id].requiredPuzzles*.puzzle*.name}"
            if (!puzzles[it.id].save()) {
                println "Failed to save E ${it}"
            }
        }

        if (config.favicon != null) {
            new Favicon(resource: resources[config.favicon]).save(flush: true)
        }

        config.statuses.each {
            println "add status ${it}"
            new PlayerStatus(statusLevel: it.level, resource: resources[it.resource], name: it.name, hintCount: it.hintCount ?: 0, hintTime: it.hintTime ?: 0, puzzleTime: it.puzzleTime ?: 0, priorityLine: it.priorityLine ?: false).save()
        }

        config.activities.each {
            new Activity(name: it.name).save(flush:true)
        }

        players.each {k,v-> println "player ${v.name} ${v.password} ${v.role}" ; v.save(flush:true)}
        puzzles.each{k,v->v.save(flush:true)}
        resources.each{k,v->println "resource ${v.filename} ${v.accessor} ${v.role}"; v.save(flush:true)}
        rounds.each{k,v->v.save(flush:true)}

        println "favicon ${config.favicon}  ${propertiesService.favicon}"

        //        println players
        //        println puzzles
        //        println resources
    }
}
