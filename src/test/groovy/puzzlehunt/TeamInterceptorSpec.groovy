package puzzlehunt

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class TeamInterceptorSpec extends Specification implements InterceptorUnitTest<TeamInterceptor> {

    def setup() {
    }

    def cleanup() {

    }

    void "Test team interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"team")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
