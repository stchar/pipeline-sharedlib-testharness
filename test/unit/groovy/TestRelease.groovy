//package ut


import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static com.lesfurets.jenkins.unit.global.lib.ProjectSource.projectSource
import static org.junit.Assert.assertEquals

class TestRelease extends BasePipelineTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none()

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


    binding.setVariable('env', [
        BUILD_URL:"fake_jurl/",
    ])

  }

  @Test
  void "Verify release mail with default fields"() {

    helper.registerAllowedMethod("emailext", [Map.class], {m->return m})

    def script = runScript("jobs/template/release.groovy")

    def c = [
      name:'fake_component',
      version: "1.2.3",
      gitlab_url: "fake_gitlab_url/",
      project: "fake/project",
      mailto: 'jhon.doe@example.com',
    ]

    def expected_mail_body = '''
<p>Hola, fake_component-1.2.3 ha sido lanzado</p>

<h1>Release Artifacts</h1>
<p>Build Artifacts: <a href="fake_jurl/artifact">fake_jurl/artifact</a></p>
'''
    Map mail
    def random_mock = new groovy.mock.interceptor.MockFor(java.util.Random)
    random_mock.demand.nextInt(){
      Integer size -> println "Mocked random"
      return 0
    }
    random_mock.use {
      mail = script.release_lib.release_mail(c)
    }
    assertEquals('Verify to field', 'jhon.doe@example.com', mail.to)
    assertEquals('Verify subject field', (String) "${c.name}-${c.version} is out", mail.subject)
    assertEquals('Verify mimeType field',  "text/html", mail.mimeType)
    assertEquals('Verify body field',  expected_mail_body, mail.body)
    printCallStack()
  }
} // class
