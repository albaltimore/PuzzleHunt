package puzzlehunt

class Hunt {
    String description
    String linkKey = UUID.randomUUID().toString()

    Long startTime
    Long endTime
    Integer maxTeamSize
    boolean showLeaderboard = true

    static constraints = {
        startTime nullable: true
        endTime nullable: true
        maxTeamSize nullable: true
    }
}
