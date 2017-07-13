package puzzlehunt


class PlayerInterceptor {

    PlayerInterceptor() {
        match controller: "player"
    }

    int order = 100

    boolean before() {
        def player = Player.findById(session.playerId)
        // Hinters should be kindly redirected back to hint board
        if (player?.role == "HINTER") {
            redirect controller: "hint"
            return false
        }
        // If somehow we're an unrecognized role that isn't null (i.e. not-player), go back to login.
        if (player?.role != null) {
            redirect controller: "login"
            return false
        }

        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
