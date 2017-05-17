package puzzlehunt


class HintInterceptor {

    HintInterceptor() {
        match(controller: "hint")
    }

    int order = 100

    boolean before() {
        def player = Player.findById(session.playerId)
        if (player?.role != "HINTER") {
            redirect controller: "player"
            return false
        }

        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
