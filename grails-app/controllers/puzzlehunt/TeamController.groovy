package puzzlehunt

import grails.converters.JSON
import grails.gorm.transactions.Transactional

class TeamController {
    def index() {}

    def namingService

    def getState() {
        Player player = Player.findById(session.playerId)
        Hunt hunt = Hunt.findById(session.huntId)

        render([
            hunt: hunt.description,
            name: player.name,
            description: player.description,
            team: player.team ? [
                id: player.team.id, name: player.team.name, isPublic: player.team.isPublic, isFinalized: player.team.isFinalized, hasStarted: player.team.hasStarted, key: player.team.teamKey,
                players: Player.findAllByTeam(player.team).collect {
                    [name: it.name, description: it.description, id: it.id]
                },
                invites: TeamInvite.findAllByTeam(player.team).collect { [id: it.id, player: it.player.name] }
            ] : null,
            teams: Team.findAllByIsPublicAndHasStarted(true, false).collect {
                [players: Player.countByTeam(it), name: it.name, id: it.id, key: it.teamKey, hasInvite: TeamInvite.countByTeamAndPlayer(it, player).asBoolean()]
            }
        ] as JSON)
    }

    def start() {
        render([
            startsIn: ((Hunt.findById(session.huntId).startTime ?: 0) as Long) - System.currentTimeMillis(),
            hasStarted: Player.findById(session.playerId).team?.hasStarted
        ] as JSON)
    }

    def createTeam() {
        Player player = Player.findById(session.playerId)
        if (player.team) {
            render status: 500
            return
        }

        Hunt hunt = Hunt.findById(session.huntId)
        Team team = new Team(name: namingService.getRandomName(), hunt: hunt)

        player.team = team

        if (!team.save(flush: true) || !player.save(flush: true)) {
            render status: 500
        }

        render [:] as JSON
    }

    def publicize() {
        Player player = Player.findById(session.playerId)
        Team team = player.team
        if (!team) {
            render status: 500
            return
        }

        team.isPublic = Boolean.parseBoolean(params.isPublic)

        if (!team.save(flush: true)) {
            render status: 500
            return
        }
        render [:] as JSON
    }

    def makeFinal() {
        Player player = Player.findById(session.playerId)
        Team team = player.team
        if (!team) {
            render status: 500
            return
        }

        boolean isFinalize = Boolean.parseBoolean(params.isFinalized)

        if (!isFinalize && team.hasStarted) {
            render status: 500
            return
        }

        team.isFinalized = isFinalize

        if (!team.save(flush: true)) {
            render status: 500
            return
        }
        render [:] as JSON
    }

    def leaveTeam() {
        Player player = Player.findById(session.playerId)
        Team team = player.team
        if (!team || team.isFinalized || team.hasStarted) {
            render status: 500
            return
        }

        player.team = null;
        if (!player.save(flush: true)) {
            render status: 500
            return
        }

        if (!Player.countByTeam(team)) {
            team.delete(flush: true)
        }

        render [:] as JSON
    }

    @Transactional
    def playerInvite() {
        Team team = Team.findByTeamKey(params.key)
        if (!team) {
            render status: 500
            return
        }

        Player player = Player.findById(session.playerId)
        TeamInvite invite = TeamInvite.findByTeamAndPlayer(team, player)
        if (invite) {
            render status: 500
            return
        }

        invite = new TeamInvite(team: team, player: player, playerRequest: true)

        if (!invite.save(flush: true)) {
            render status: 500
            return
        }
        render [:] as JSON
    }

    @Transactional
    def teamInvite() {
        Player player = Player.findById(session.playerId)
        if (!player.team) {
            render status: 500
            return
        }

        Player otherPlayer = Player.findById(params.player)
        if (!otherPlayer || TeamInvite.findByPlayerAndTeam(otherPlayer, team)) {
            render status: 500
            return
        }

        TeamInvite invite = new TeamInvite(team: team, player: player, playerRequest: false)

        if (!invite.save(flush: true)) {
            render status: 500
            return
        }

        render [:] as JSON
    }

    @Transactional
    def playerDecline() {
        TeamInvite invite = TeamInvite.findById(params.invite)
        if (!invite) {
            render status: 500
            return
        }

        Player player = Player.findById(session.playerId)
        if (player != invite.player) {
            render status: 500
            return
        }

        invite.delete(flush: true)

        render [:] as JSON
    }


    @Transactional
    def teamDecline() {
        TeamInvite invite = TeamInvite.findById(params.invite)
        if (!invite) {
            render status: 500
            return
        }

        Player player = Player.findById(session.playerId)
        if (!player.team || player.team != invite.team) {
            render status: 500
            return
        }

        invite.delete(flush: true)

        render [:] as JSON
    }

    @Transactional
    def playerAccept() {
        TeamInvite invite = TeamInvite.findById(params.invite)
        if (!invite) {
            render status: 500
            return
        }

        Player player = Player.findById(session.playerId)
        if (player.team || player != invite.player) {
            render status: 500
            return
        }

        player.team = invite.team
        if (!player.save(flush: true)) {
            render status: 500
            return
        }

        TeamInvite.where { player == player && playerRequest == true }.deleteAll()

        render [:] as JSON
    }


    @Transactional
    def teamAccept() {
        TeamInvite invite = TeamInvite.findById(params.invite)
        if (!invite) {
            render status: 500
            return
        }

        Player player = Player.findById(session.playerId)
        Player otherPlayer = invite.player
        if (!player.team || player.team != invite.team || otherPlayer.team) {
            render status: 500
            return
        }

        otherPlayer.team = invite.team

        if (!otherPlayer.save(flush: true)) {
            render status: 500
            return
        }

        invite.delete(flush: true)

        render [:] as JSON
    }

}