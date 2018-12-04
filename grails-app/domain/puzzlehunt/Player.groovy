package puzzlehunt

class Player {
    String name
    String password
    String role
    String email
    String room
    String description
    Long firstLoginTime

    static constraints = {
        name unique: true
        role nullable: true
        lastHint nullable: true
        email nullable: true
        room nullable: true
        description nullable: true
        firstLoginTime nullable: true
    }

    def getTeam() {
        Team.withCriteria(uniqueResult: true) {
            members {
                idEq(id)
            }
        } as Team
    }

    def getPendingTeamInvites() {
        TeamInvite.getPendingInvites(this)
    }

    static hasMany = [:]
}
