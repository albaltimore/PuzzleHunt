package puzzlehunt


class LoginInterceptor {

    public LoginInterceptor() {
        matchAll().excludes controller: "login" excludes uri: "/error" excludes uri: "/notFound" excludes controller: "healthcheck"
    }

    boolean before() {
        if (!session.playerId) {
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
