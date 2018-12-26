package puzzlehunt

import groovy.transform.ToString

@ToString(includeNames = true)
class Player {
    String name
    String password
    String email
    String room
    String description
    String source

    String role

    Long firstLoginTime
    Team team

    static constraints = {
        name unique: ['email', 'source']
        password nullable: true
        role nullable: true
        email nullable: true
        room nullable: true
        description nullable: true
        source nullable: true

        firstLoginTime nullable: true
        team nullable: true
    }

    static hasMany = [:]
}