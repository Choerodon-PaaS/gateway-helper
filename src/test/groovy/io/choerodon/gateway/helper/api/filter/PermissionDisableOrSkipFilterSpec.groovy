package io.choerodon.gateway.helper.api.filter

import io.choerodon.gateway.helper.domain.CheckRequest
import io.choerodon.gateway.helper.domain.CheckResponse
import io.choerodon.gateway.helper.domain.CheckState
import io.choerodon.gateway.helper.domain.RequestContext
import io.choerodon.gateway.helper.infra.properties.HelperProperties
import spock.lang.Specification

class PermissionDisableOrSkipFilterSpec extends Specification {

    def "test filter Order"() {
        when: ''
        def result = new PermissionDisableOrSkipFilter(null).filterOrder()
        then: ''
        result == 0
    }

    def "test should Filter"() {
        when: ''
        def result = new PermissionDisableOrSkipFilter(null).shouldFilter(null)
        then: ''
        result
    }

    def "test run"() {
        given: '创建requestContext'
        def context = new RequestContext(new CheckRequest(null, "/iam/test", "method"),
                new CheckResponse(message: "message", status: CheckState.SUCCESS_PASS_SITE))
        and: '创建HelperProperties'
        def helperProperties = new HelperProperties()
        def filter = new PermissionDisableOrSkipFilter(helperProperties)

        when: '权限不启用'
        helperProperties.getPermission().setEnabled(false)
        def result1 = filter.run(context)

        then: '验证结果'
        !result1
        context.response.status == CheckState.SUCCESS_PERMISSION_DISABLED

        when: '权限跳过的路径'
        helperProperties.getPermission().setEnabled(true)
        helperProperties.getPermission().setSkipPaths(Collections.singletonList('/iam/**'))
        def result2 = filter.run(context)

        then: '验证结果'
        !result2
        context.response.status == CheckState.SUCCESS_SKIP_PATH

        when: '非跳过的路径'
        helperProperties.getPermission().setSkipPaths(Collections.singletonList('/manager/**'))
        def result3 = filter.run(context)

        then: '验证结果'
        result3
    }

}
