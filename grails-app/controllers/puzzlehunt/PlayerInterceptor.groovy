package puzzlehunt


class PlayerInterceptor {

    PlayerInterceptor() {
        match controller: "player"
    }

    int order = 101

    boolean before() {
        def player = Player.findById(session.playerId)
        
        if (player?.role == "HINTER") {
            redirect controller: "hint"
            return false
        }
        if (!player?.role) {
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
