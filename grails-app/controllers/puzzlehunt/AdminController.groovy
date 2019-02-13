package puzzlehunt

import grails.converters.JSON

class AdminController {

    def index() {}

    def getData() {
        def rounds = Round.list().collect { [id: it.id, name: it.name, unlocked: it.unlocked] }
        def players = Player.findAllByRoleIsNull().collect { [id: it.id, name: it.name, description: it.description] }
        def teams = Team.list().collect { [id: it.id, name: it.name] }
        def activities = Activity.list().collect { [id: it.id, name: it.name] }

        def alerts = Alert.list().inject [:], { acc, it -> acc[it.batchId] = [title: it.title, targetTime: it.targetTime]; acc }

        def hunts = Hunt.list()
        def ret = [rounds: rounds, players: players, activities: activities, start: Property.findByName('START')?.value, alerts: alerts, teams: teams, hunts: hunts]
        render ret as JSON
    }

    def setRoundUnlocked() {
        def round = Round.findById(params.round)
        round.unlocked = Boolean.parseBoolean(params.unlocked)
        if (!round.save(flush: true)) {
            render status: 500
            return
        }

        def ret = [success: "true"]
        render ret as JSON
    }

    def getTeamActivityPoints() {
        def team = Team.findById(params.team)
        def activity = Activity.findById(params.activity)
        def attempt = ActivityAttempt.findByTeamAndActivity(team, activity)

        if (attempt) {
            def ret = [points: attempt.statusPoints]
            render ret as JSON
            return
        }
        def ret = [points: 0]
        render ret as JSON
    }

    def setTeamActivityPoints() {
        println params
        def team = Team.findById(params.team)
        def activity = Activity.findById(params.activity)
        def attempt = ActivityAttempt.findByTeamAndActivity(team, activity)

        if (!attempt) {
            attempt = new ActivityAttempt(team: team, activity: activity, statusPoints: Integer.parseInt(params.points))
        } else {
            attempt.statusPoints = Integer.parseInt(params.points)
        }
        if (!attempt.save(flush: true)) {
            println "Failed To Save ${attempt.team} ${attempt.activity} ${attempt.statusPoints}"
            render status: 500
        }

        def ret = [success: "true"]
        render ret as JSON
    }

    def setProperty() {
        Property p = Property.findByName(params.name) ?: new Property(name: params.name)
        p.value = params.value

        if (!p.save(flush: true)) {
            println "Failed To Save propery $params"
            render status: 500

        }
        def ret = [success: "true"]
        render ret as JSON
    }

    def createAlert() {
        def batchId = UUID.randomUUID().toString()

        def createAlertPlayer = { p ->
            if (!new Alert(player: p, title: params.title, message: params.message, targetTime: params.targetTime, leadTime: params.leadTime, batchId: batchId).save(flush: true)) {
                render status: 500
                return
            }
        }

        if (params.player == 'ALL') {
            Player.findAllByRole(null).each createAlertPlayer
        } else {
            createAlertPlayer(Player.findById(params.player))
        }

        def ret = [success: "true"]
        render ret as JSON
    }

    def deleteAlertsByBatchId() {
        Alert.findAllByBatchId(params.batchId).each {
            it.delete(flush: true)
        }

        def ret = [success: "true"]
        render ret as JSON
    }

    def saveHunt() {
        Hunt hunt = params.id ? Hunt.findById(params.id) : new Hunt()
        hunt.description = params.description ?: null
        hunt.maxTeamSize = params.maxTeamSize ? params.maxTeamSize as Integer : null
        hunt.startTime = params.startTime ? params.startTime as Long : null
        hunt.endTime = params.endTime ? params.endTime as Long : null
        hunt.winningText = params.winningText ?: null
        hunt.gameoverText = params.gameoverText ?: null

        if (!hunt.save(flush: true)) {
            println hunt.errors
            render status: 500
            return
        }

        def ret = [success: "true"]
        render ret as JSON
    }
}
