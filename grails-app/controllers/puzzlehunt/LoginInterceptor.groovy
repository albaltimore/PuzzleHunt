package puzzlehunt


class LoginInterceptor {

    public LoginInterceptor() {
        matchAll().excludes controller: "login" excludes uri: "/error" excludes uri: "/notFound" excludes controller: "healthcheck"
    }

    int order = 10

    boolean before() {
        if (!session.playerId || !session.playerName || ! Player.findByNameAndId(session.playerName, session.playerId)) {
            redirect controller: "login"
            return false
        }

        if (!session.huntId && controllerName != 'admin') {
            redirect controller: 'login', action: 'nohunt'
            return false
        }

        return true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
