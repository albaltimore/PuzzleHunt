package puzzlehunt


class EventInterceptor {

    public EventInterceptor() {
        match controller: "team", action: "index"
    }

    boolean before() {
        def player = Player.get(session.playerId)
        def team = player.team
        def hasEventStarted = ((Property.findByName('START')?.value ?: 0) as Long) <= System.currentTimeMillis()

        if (team && team.isFinalized && hasEventStarted) {
            return true
        } else {
            redirect controller: "team", action: "show"
        }
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
