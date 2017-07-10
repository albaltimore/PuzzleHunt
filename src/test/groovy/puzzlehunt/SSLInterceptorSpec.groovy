package puzzlehunt


import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(SSLInterceptor)
class SSLInterceptorSpec extends Specification {

    def setup() {
    }

    def cleanup() {

    }

    void "Test SSL interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"SSL")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
