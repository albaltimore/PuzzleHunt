package puzzlehunt


class SSLInterceptor {

    public SSLInterceptor() {
        matchAll()
    }

    int order = 1
    final private String HTTP_PROTOCOL = 'http://'

    boolean before() {
        println "ssl ${request.isSecure} ${request.requestURL}"
        if (!request.isSecure && grailsApplication.config.getProperty("puzzlehunt.useSSL")) {
            def url = request.requestURL
            if (url.substring(0, HTTP_PROTOCOL.length()) == HTTP_PROTOCOL) {
                def newUrl = 'https://' + url.substring(HTTP_PROTOCOL.length())
                redirect url: newUrl
                return false
            }
        }
        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
