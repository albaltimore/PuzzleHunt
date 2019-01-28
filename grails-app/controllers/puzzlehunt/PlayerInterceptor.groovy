package puzzlehunt


class PlayerInterceptor {

    int order = 200

    public PlayerInterceptor() {
        match controller: "player"
    }

    boolean before() {
        def player = Player.get(session.playerId)
        def team = player.team
        def hunt = Hunt.findById(session.huntId)
        if (!hunt) {
            redirect controller: 'login', action: 'nohunt'
            return false
        }

        def hasEventStarted = ((hunt.startTime ?: 0) as Long) <= System.currentTimeMillis()

        if (!team || !team.isFinalized || !hasEventStarted) {
            redirect controller: 'team'
            return false
        }

        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
