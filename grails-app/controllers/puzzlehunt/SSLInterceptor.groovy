package puzzlehunt


class SSLInterceptor {

    public SSLInterceptor() {
        matchAll()
    }

    int order = 1

    boolean before() {
        println "ssl ${request.isSecure}"


        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
