package puzzlehunt

import groovy.transform.ToString

@ToString(includeNames = true)
class Hunt {
    String id
    String description
    String winningText
    String gameoverText
    String linkKey = UUID.randomUUID().toString()
    Long startTime
    Long endTime
    Integer maxTeamSize
    boolean showLeaderboard = true //TODO hookup to something

    static constraints = {
        startTime nullable: true
        endTime nullable: true
        maxTeamSize nullable: true
        winningText nullable: true
        gameoverText nullable: true
    }

    static mapping = {
        id generator: 'uuid'
    }
}
