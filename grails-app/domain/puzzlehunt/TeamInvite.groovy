package puzzlehunt

class TeamInvite {
    Team team
    Player player
    boolean playerRequest

    static constraints = {

    }

    static belongsTo = [team: "team", player: "player"]


}