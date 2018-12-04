package puzzlehunt

import grails.converters.JSON

class PlayerController {

    def gameService

    def index() {
        def player = Player.get(session.playerId)
        return [player: player]
    }

    def getAlerts() {
        def player = Player.findById(session.playerId)
        def alerts = Alert.findAllByPlayer(player).findAll {
            it.targetTime - (1000 * it.leadTime) < System.currentTimeMillis()
        }.collect {
            [
                    id            : it.id,
                    title         : it.title,
                    message       : it.message,
                    targetTime    : it.targetTime,
                    leadTime      : it.leadTime,
                    isAcknowledged: it.isAcknowledged
            ]
        }
        render alerts as JSON
    }

    def acknowledgeAlert() {
        def player = Player.findById session.playerId
        def alert = Alert.findByIdAndPlayer params.id, player
        if (alert == null) {
            render status: 500
            return
        }

        alert.isAcknowledged = true;
        if (!alert.save(flush: true)) {
            render status: 500
            return
        }

        def ret = [success: true]
        render ret as JSON
    }
}
