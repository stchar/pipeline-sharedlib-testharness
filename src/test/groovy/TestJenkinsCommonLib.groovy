
import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static com.lesfurets.jenkins.unit.global.lib.LocalSource.localSource

import org.junit.Before
import org.junit.Test

import com.lesfurets.jenkins.unit.BasePipelineTest

import static org.junit.Assert.assertEquals

//import static com.lesfurets.jenkins.unit.MethodCall.callArgsToString
//import static org.junit.Assert.assertTrue

class TestJenkinsCommonLib extends BasePipelineTest {

  String sharedLibs = ''

  @Override
  @Before
  void setUp() throws Exception {
      scriptRoots += 'jobs'
      super.setUp()
      def library = library().name('jenkins-commons')
                       .defaultVersion("master")
                       .allowOverride(true)
                       .implicit(true)
                       .targetPath('build/libs')
                       .retriever(localSource('build/libs'))
                       .build()
      helper.registerSharedLibrary(library)
  }

  @Test
  void should_execute_without_errors() throws Exception {
    def script = runScript("template/pipeline/template.groovy")
    //printCallStack()
  }

  @Test
  void verify_is_upstream() throws Exception {
    def script = runScript("template/pipeline/template.groovy")
    assertEquals('Verify is_upstream ', false, script.gitlab_lib.is_upstream('feature'))
    assertEquals('Verify is_upstream ', true, script.gitlab_lib.is_upstream('master'))
    assertEquals('Verify is_upstream ', true, script.gitlab_lib.is_upstream('rel-1.2.3'))
    //printCallStack()
  }

  @Test
  void verify_increment_version() throws Exception {
    def script = runScript("template/pipeline/template.groovy")
    assertEquals('Verify increment_version ', "1.2.4", script.gitlab_lib.increment_version('1.2.3'))
    //printCallStack()
  }

}
