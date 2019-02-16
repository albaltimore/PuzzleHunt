package puzzlehunt

import grails.converters.JSON
import grails.gorm.transactions.Transactional

class TeamController {
    def index() {}

    def namingService

    def getState() {
        Player player = Player.findById(session.playerId)
        Hunt hunt = Hunt.findById(session.huntId)
        Team team = session.teamId ? Team.findById(session.teamId) : null

        render([
            hunt: hunt.description,
            name: player.name,
            description: player.description,
            team: team ? [
                id: team.id, name: team.name, isPublic: team.isPublic, isFinalized: team.isFinalized, hasStarted: team.hasStarted, key: team.teamKey,
                maxTeamSize: hunt.maxTeamSize ?: Integer.MAX_VALUE,
                players: TeamInvite.findAllByTeamAndCompleted(team, true)*.player.collect {
                    [name: it.name, description: it.description, id: it.id]
                },
                invites: TeamInvite.findAllByTeamAndCompleted(team, false).collect { [id: it.id, player: it.player.name] }
            ] : null,
            teams: Team.findAllByIsPublicAndHasStartedAndHunt(true, false, hunt).collect {
                [
                    players: TeamInvite.findAllByTeamAndCompleted(it, true)*.player*.id.unique().size(),
                    name: it.name,
                    id: it.id,
                    key: it.teamKey,
                    hasInvite: TeamInvite.countByTeamAndPlayerAndCompleted(it, player, false).asBoolean()
                ]
            }
        ] as JSON)
    }

    def start() {
        Team team = session.teamId ? Team.findById(session.teamId) : null
        render([
            startsIn: ((Hunt.findById(session.huntId).startTime ?: 0) as Long) - System.currentTimeMillis(),
            hasStarted: team?.hasStarted
        ] as JSON)
    }

    def createTeam() {
        Player player = Player.findById(session.playerId)
        Team team = session.teamId ? Team.findById(session.teamId) : null
        if (team) {
            render status: 500
            return
        }

        Hunt hunt = Hunt.findById(session.huntId)
        team = new Team(name: namingService.getRandomName(), hunt: hunt)


        if (!team.save(flush: true)) {
            render status: 500
        }

        TeamInvite teamInvite = new TeamInvite(team: team, player: player, playerRequest: true, completed: true)

        if (!teamInvite.save(flush: true)) {
            render status: 500
        }

        render [:] as JSON
    }

    def publicize() {
        Team team = session.teamId ? Team.findById(session.teamId) : null

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
        Team team = session.teamId ? Team.findById(session.teamId) : null
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
        Team team = session.teamId ? Team.findById(session.teamId) : null
        if (!team || team.isFinalized || team.hasStarted) {
            render status: 500
            return
        }
        TeamInvite.findAllByTeamAndPlayerAndCompleted(team, player, true).each {
            it.delete(flush: true)
        }
        if (!(TeamInvite.where { completed == true && team == team }.count())) {
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
        Team team = session.teamId ? Team.findById(session.teamId) : null
        if (!team) {
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
        Team team = session.teamId ? Team.findById(session.teamId) : null
        if (!invite) {
            render status: 500
            return
        }

        Player player = Player.findById(session.playerId)
        if (!team || team != invite.team) {
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
        Team team = session.teamId ? Team.findById(session.teamId) : null
        if (team || player != invite.player) {
            render status: 500
            return
        }

        if (hunt.maxTeamSize <= TeamInvite.findAllByTeamAndCompleted(invite.team, true)*.player*.id.unique().size()) {
            render status: 500
            return
        }

        invite.completed = true
        if (!invite.save(flush: true)) {
            render status: 500
            return
        }
        session.teamId = invite.team.id
        TeamInvite.where { player == player && playerRequest == true && id != invite.id }.deleteAll()
        render [:] as JSON
    }


    @Transactional
    def teamAccept() {
        TeamInvite invite = TeamInvite.findById(params.invite)
        if (!invite) {
            render status: 500
            return
        }

        Hunt hunt = Hunt.findById(session.huntId)
        Player player = Player.findById(session.playerId)
        Team playerTeam = session.teamId ? Team.findById(session.teamId) : null
        Player otherPlayer = invite.player
        Team otherTeam = TeamInvite.where {
            player == otherPlayer && team.hunt == hunt && completed == true
        }.get()?.team

        if (!playerTeam || playerTeam != invite.team || otherTeam) {
            render status: 500
            return
        }

        if (hunt.maxTeamSize <= TeamInvite.findAllByTeamAndCompleted(playerTeam, true)*.player*.id.unique().size()) {
            render status: 500
            return
        }

        invite.completed = true
        if (!invite.save(flush: true)) {
            render status: 500
            return
        }
        render [:] as JSON
    }

}