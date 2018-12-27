package puzzlehunt


class LoginInterceptor {

    public LoginInterceptor() {
        matchAll().excludes controller: "login" excludes uri: "/error" excludes uri: "/notFound" excludes controller: "healthcheck"
    }

    int order = 10

    boolean before() {
        if (!session.huntId) {
            render status: 404
            return false
        }

        if (!session.playerId || !session.playerName || ! Player.findByNameAndId(session.playerName, session.playerId)) {
            redirect controller: "login"
            return false
        }
        return true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
