package puzzlehunt

class Hunt {
    String id
    String description
    String linkKey = UUID.randomUUID().toString()

    Long startTime
    Long endTime
    Integer maxTeamSize
    boolean showLeaderboard = true //TODO hookup to something

    static constraints = {
        startTime nullable: true
        endTime nullable: true
        maxTeamSize nullable: true
    }

    static mapping = {
        id generator: 'uuid'
    }
}
