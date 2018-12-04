package puzzlehunt

class TeamInvite {
    static final def declinedState = "declined"
    static final def acceptedState = "accepted"
    static final def pendingState = "pending"

    Team team
    Player invitee
    String state = pendingState

    static constraints = {
        invitee unique: ["team"]
    }

    static belongsTo = [team: "team", invitee: "player"]

    static getPendingInvites(Player player) {
        withCriteria {
            invitee {
                idEq(player.id)
            }
            eq('state', pendingState)
        }
    }

    // TODO: Some sort of Finite State Machine plugin?

    def isDeclined() {
        state == declinedState
    }

    def isAccepted() {
        state == acceptedState
    }

    def isPending() {
        state == pendingState
    }

    def accept() {
        if (isPending()) {
            team.addToMembers(invitee)
            setState(acceptedState)
            save(flush: true)
        }
    }

    def decline() {
        if (isPending()) {
            setState(declinedState)
            save(flush: true)
        }
    }
}