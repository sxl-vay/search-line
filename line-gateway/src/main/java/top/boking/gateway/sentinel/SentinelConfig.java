package top.boking.gateway.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SentinelConfig {

    @PostConstruct
    public void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("exampleResource");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(10); // 每秒最多通过10个请求
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

    @PostConstruct
    public void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        rule.setResource("exampleResource");
        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        rule.setCount(50); // 平均响应时间阈值为50ms
        rule.setTimeWindow(10); // 熔断时长为10秒
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }
}