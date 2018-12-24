package puzzlehunt


class TeamInterceptor {

    int order = 201

    public TeamInterceptor() {
        match controller: 'team'
    }


    boolean before() {
        def player = Player.get(session.playerId)
        def team = player.team
        def hasEventStarted = ((Property.findByName('START')?.value ?: 0) as Long) <= System.currentTimeMillis()

        if (team && team.isFinalized && hasEventStarted) {
            redirect controller: 'player'
            false
        }
        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
