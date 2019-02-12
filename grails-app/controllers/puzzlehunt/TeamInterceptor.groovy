package puzzlehunt


class TeamInterceptor {

    int order = 201

    public TeamInterceptor() {
        match controller: 'team'
    }


    boolean before() {
        def player = Player.get(session.playerId)
        Hunt hunt = Hunt.findById(session.huntId)
        Team team = TeamInvite.where { completed == true && player == player && team.hunt == hunt }.find()?.team

        session.teamId = team?.id

        def hasEventStarted = ((hunt?.startTime ?: 0) as Long) <= System.currentTimeMillis()

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
