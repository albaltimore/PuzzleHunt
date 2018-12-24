package puzzlehunt

class Player {
    String name
    String password
    String role
    String email
    String room
    String description
    Long firstLoginTime
    Team team

    static constraints = {
        name unique: true
        role nullable: true
        email nullable: true
        room nullable: true
        description nullable: true
        firstLoginTime nullable: true
        team nullable: true
    }

    static hasMany = [:]
}
