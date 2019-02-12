package puzzlehunt

class TeamInvite {
    Team team
    Player player
    boolean playerRequest
    boolean completed = false

    static constraints = {

    }

    static belongsTo = [team: "team", player: "player"]


}