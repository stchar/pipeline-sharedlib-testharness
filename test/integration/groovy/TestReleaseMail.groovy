package it

import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.mkobit.jenkins.pipelines.codegen.LocalLibraryRetriever
import hudson.model.queue.QueueTaskFuture
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.cps.CpsFlowExecution
import org.jenkinsci.plugins.workflow.libs.GlobalLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.LibraryRetriever
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.job.WorkflowRun
import org.jenkinsci.plugins.workflow.support.steps.input.InputAction
import org.jenkinsci.plugins.workflow.support.steps.input.InputStepExecution
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.jvnet.hudson.test.JenkinsRule
import org.jvnet.hudson.test.MockAuthorizationStrategy
import org.jvnet.hudson.test.recipes.WithTimeout
import org.jvnet.mock_javamail.Mailbox

import javax.mail.Message
import javax.mail.internet.MimeMultipart

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class TestReleaseMail {

  @Rule
  public JenkinsRule rule = new JenkinsRule() {
    @Override
    public void before() throws Throwable {
      super.before()
      Mailbox.clearAll()
    }

    @Override
    public void after() throws Exception {
      super.after()
      Mailbox.clearAll()
    }
  }

  @Before
  void configureGlobalLibraries() {
    rule.timeout = 30
    final LibraryRetriever retriever = new LocalLibraryRetriever()
    final LibraryConfiguration localLibrary =
        new LibraryConfiguration('jenkins-commons', retriever)
    localLibrary.implicit = true
    localLibrary.defaultVersion = 'master'
    localLibrary.allowVersionOverride = true
    GlobalLibraries.get().setLibraries(Collections.singletonList(localLibrary))
    Mailbox.clearAll();
  }


  @Test
  void "verify release mail"() {

    WorkflowJob workflowJob = rule.createProject(WorkflowJob, 'test-release-mail')
    def script = new File('jobs/release/mail/basic.groovy')
    workflowJob.definition = new CpsFlowDefinition(script.text, true)

    WorkflowRun result = rule.buildAndAssertSuccess(workflowJob)
    rule.assertLogContains('Sending email to: jhon.doe@example.com', result)

    assertEquals(1, Mailbox.get("jhon.doe@example.com").getNewMessageCount());
    Message message = Mailbox.get("jhon.doe@example.com").get(0);
    MimeMultipart part = (MimeMultipart) message.getContent();
    assertTrue("Verify message body", part.getBodyPart(0).getInputStream().text.contains("Release Artifacts"))


  }


  @Test
  @WithTimeout(0)
  void "verify release input"() {

    JenkinsRule.WebClient webClient = rule.createWebClient()
    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false)

    rule.jenkins.setSecurityRealm(rule.createDummySecurityRealm())
    rule.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().
        grant(Jenkins.ADMINISTER).everywhere().to("admin")
    );

    WorkflowJob workflowJob = rule.createProject(WorkflowJob, 'test-release-input')
    def script = new File('jobs/release/release_input.groovy')
    workflowJob.definition = new CpsFlowDefinition(script.text, true)

    runAndContinue(webClient, workflowJob, "admin", "123",  true)
  }


  private void runAndContinue(JenkinsRule.WebClient webClient, WorkflowJob job, String loginAs, inputId, boolean expectContinueOk) throws Exception {
    // get the build going, and wait until workflow pauses
    QueueTaskFuture<WorkflowRun> queueTaskFuture = job.scheduleBuild2(0);
    WorkflowRun run = queueTaskFuture.getStartCondition().get();
    CpsFlowExecution execution = (CpsFlowExecution) run.getExecutionPromise().get();
    println("DEBUG: started build ${execution}")
    while (run.getAction(InputAction.class) == null) {
      execution.waitForSuspension()
    }
    InputAction inputAction = run.getAction(InputAction.class);
    InputStepExecution is = inputAction.getExecution(inputId);
    println("DEBUG: InputStepExecution: ${is}")

    webClient.login(loginAs);
    HtmlPage p = webClient.getPage(run, inputAction.getUrlName());

    rule.submit(p.getFormByName(is.getId()), "proceed");
    assertEquals(0, inputAction.getExecutions().size());
    queueTaskFuture.get();

    assertTrue(expectContinueOk);
    rule.assertBuildStatusSuccess(rule.waitForCompletion(run)); // Should be successful.
  }

}
