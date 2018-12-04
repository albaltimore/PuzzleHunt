package puzzlehunt 

class TeamController {
    def newTeam() {}

    def show() {
        def team = Team.get(params.id)
        return [team: team, id: params.id]
    }

    def createTeam() {
        def player = Player.get(session.playerId)
        def team = new Team(name: params.name).save()
        team.addToMembers(player).save(flush: true)
        redirect action: "show", id: team.id
    }

    def leaveTeam() {
        def player = Player.get(session.playerId)
        def team = Team.get(params.id)

        team.removeFromMembers(player).save(flush: true)
        redirect controller: "player", action: "index"
    }

    def finalizeTeam()  {
        def team = Team.get(params.id)
        team.setIsFinalized(true)
        team.save(flush: true)
        redirect action: "show", id: params.id
    }

    def unfinalizeTeam() {
        // TODO: Conditional on the event having not started yet
        def team = Team.get(params.id)
        team.setIsFinalized(false)
        team.save(flush: true)
        redirect action: "show", id: params.id
    }
}