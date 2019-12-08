
import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static com.lesfurets.jenkins.unit.global.lib.ProjectSource.projectSource

import org.junit.Before
import org.junit.Test

import com.lesfurets.jenkins.unit.BasePipelineTest

import com.lesfurets.jenkins.unit.MethodCall
import static com.lesfurets.jenkins.unit.MethodCall.callArgsToString
import static org.assertj.core.api.Assertions.assertThat
import static org.junit.Assert.assertEquals

class TestJenkinsCommonLib extends BasePipelineTest {

  String sharedLibs = ''

  @Override
  @Before
  void setUp() throws Exception {
      scriptRoots += 'jobs'
      super.setUp()
      def library = library().name('jenkins-commons')
                        .defaultVersion('<notNeeded>')
                        .allowOverride(true)
                        .implicit(false)
                        .targetPath('<notNeeded>')
                        .retriever(projectSource())
                        .build()
      helper.registerSharedLibrary(library)
  }

  @Test
  void should_execute_without_errors() throws Exception {
    def script = runScript("template/gitlab.groovy")
    //printCallStack()
  }

  @Test
  void verify_is_upstream() throws Exception {
    def script = runScript("template/gitlab.groovy")
    assertEquals('Verify is_upstream ', false, script.gitlab_lib.is_upstream('feature'))
    assertEquals('Verify is_upstream ', true, script.gitlab_lib.is_upstream('master'))
    assertEquals('Verify is_upstream ', true, script.gitlab_lib.is_upstream('rel-1.2.3'))
    //printCallStack()
  }

  @Test
  void verify_increment_version() throws Exception {
    def script = runScript("template/gitlab.groovy")
    assertEquals('Verify increment_version ', "1.2.4", script.gitlab_lib.increment_version('1.2.3'))
    //printCallStack()
  }

}
