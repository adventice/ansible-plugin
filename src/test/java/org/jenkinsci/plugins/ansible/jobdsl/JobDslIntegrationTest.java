package org.jenkinsci.plugins.ansible.jobdsl;

import org.hamcrest.Matcher;
import org.jenkinsci.plugins.ansible.AnsibleAdHocCommandBuilder;
import org.jenkinsci.plugins.ansible.AnsiblePlaybookBuilder;
import org.jenkinsci.plugins.ansible.InventoryContent;
import org.jenkinsci.plugins.ansible.InventoryPath;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.jvnet.hudson.test.JenkinsRule;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author lanwen (Merkushev Kirill)
 */
public class JobDslIntegrationTest {
    public static final String ANSIBLE_DSL_GROOVY_PLAYBOOK = "jobdsl/playbook.groovy";
    public static final String ANSIBLE_DSL_GROOVY_ADHOC = "jobdsl/adhoc.groovy";

    public JenkinsRule jenkins = new JenkinsRule();
    public DslJobRule dsl = new DslJobRule(jenkins);

    @Rule
    public RuleChain chain = RuleChain.outerRule(jenkins).around(dsl);

    @Test
    @DslJobRule.WithJobDsl(ANSIBLE_DSL_GROOVY_PLAYBOOK)
    public void shouldCreateJobWithPlaybookDsl() throws Exception {
        AnsiblePlaybookBuilder step = dsl.getGeneratedJob().getBuildersList().get(AnsiblePlaybookBuilder.class);
        assertThat("Should add playbook builder", step, notNullValue());

        assertThat("playbook", step.playbook, is("path/playbook.yml"));
        assertThat("inventory", step.inventory, (Matcher) isA(InventoryPath.class));
        assertThat("ansibleName", step.ansibleName, is("1.9.4"));
        assertThat("limit", step.limit, is("retry.limit"));
        assertThat("tags", step.tags, is("one,two"));
        assertThat("skippedTags", step.skippedTags, is("three"));
        assertThat("startAtTask", step.startAtTask, is("task"));
        assertThat("credentialsId", step.credentialsId, is("credsid"));
        assertThat("sudo", step.sudo, is(true));
        assertThat("sudoUser", step.sudoUser, is("user"));
        assertThat("forks", step.forks, is(6));
        assertThat("unbufferedOutput", step.unbufferedOutput, is(false));
        assertThat("colorizedOutput", step.colorizedOutput, is(true));
        assertThat("hostKeyChecking", step.hostKeyChecking, is(false));
        assertThat("additionalParameters", step.additionalParameters, is("params"));
    }

    @Test
    @DslJobRule.WithJobDsl(ANSIBLE_DSL_GROOVY_ADHOC)
    public void shouldCreateJobAdhocDsl() throws Exception {
        AnsibleAdHocCommandBuilder step = dsl.getGeneratedJob().getBuildersList().get(AnsibleAdHocCommandBuilder.class);
        assertThat("Should add adhoc builder", step, notNullValue());

        assertThat("module", step.module, is("module"));
        assertThat("inventory", step.inventory, (Matcher) isA(InventoryContent.class));
        assertThat("ansibleName", step.ansibleName, is("1.9.1"));

        assertThat("credentialsId", step.credentialsId, is("credsid"));
        assertThat("hostPattern", step.hostPattern, is("pattern"));
        assertThat("sudo", step.sudo, is(false));
        assertThat("sudoUser", step.sudoUser, is("root"));
        assertThat("forks", step.forks, is(5));
        assertThat("unbufferedOutput", step.unbufferedOutput, is(true));
        assertThat("colorizedOutput", step.colorizedOutput, is(false));
        assertThat("hostKeyChecking", step.hostKeyChecking, is(false));
    }
}
