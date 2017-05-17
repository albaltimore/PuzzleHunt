package puzzlehunt


import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(HintInterceptor)
class HintInterceptorSpec extends Specification {

    def setup() {
    }

    def cleanup() {

    }

    void "Test hint interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"hint")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
