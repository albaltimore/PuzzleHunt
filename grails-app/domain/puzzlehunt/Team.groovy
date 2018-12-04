package puzzlehunt

class Team {
    String name
    Set<Player> members
    boolean isFinalized = false

    static constraints = {
        name unique: true
    }

    static hasMany = [members: Player]
}
