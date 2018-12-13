package puzzlehunt

class TeamInviteController {
    def index() {
        def player = Player.get(session.playerId)
        [teamInvites: player.pendingTeamInvites]
    }

    def newInvite() {
        def team = Team.get(params.teamId)
        [team: team]
    }

    def createInvite() {
        // TODO: Player search
        def invitee = Player.findByName(params.playerName)
        if (invitee) {
            def team = Team.get(params.teamId)
            new TeamInvite(team: team, invitee: invitee).save(flush: true, failOnError: true)
            flash.message = "Invite sent."
        } else {
            flash.message = "That player does not exist."
        }
        forward action: "newInvite", params: params
    }

    def acceptInvite() {
        def invite = TeamInvite.get(params.id)
        invite.accept()

        forward controller: "team", action: "show"
    }

    def declineInvite() {
        def invite = TeamInvite.get(params.id)
        invite.decline()

        forward action: "index"
    }
}