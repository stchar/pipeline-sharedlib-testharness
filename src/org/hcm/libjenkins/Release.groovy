package org.hcm.libjenkins
import groovy.transform.InheritConstructors


// Class declaration
@InheritConstructors
class Release extends AbstractLibClass {

  /**
   * Sends release mail. It takes email body template from the appropriate
   * component field and processes it using groovy.text.StreamingTemplateEngine
   *
   * @argument Object component
   *
   * @note Mail template in component.body shouldn't contain any calls to pipeline methods
   *       only static data, if you need to generate something please perform it before to
   *       call release_mail
   * */
  def release_mail(Object component) {
    String mailto = component.mailto ? component.mailto : ""

    String subject = component.subject ? component.subject :
        "${component.name}-${component.version} is out"

    String mimeType = component.mimeType ? component.mimeType : 'text/html'

    Map greet = component.greet ? component.greet : [
        es_ES: '''Hola, ${component.name}-${component.version} ha sido lanzado''',
        en_EN: '''Hi, ${component.name}-${component.version} has been released''',
        ru_RU: '''Привет, ${component.name}-${component.version} построен''',
        de_DE: '''Hallo, ${component.name}-${component.version} wurde freigegeben''',
        uk_UK: '''Здрастуй, ${component.name}-${component.version} був випущений'''
    ]
    def random = new java.util.Random()
    def randomKey = (greet.keySet() as List)[random.nextInt(greet.size())]

    String body = component.body ? component.body : '''
<p>${GREET}</p>

<h1>Release Artifacts</h1>
<p>Build Artifacts: <a href="${env.BUILD_URL}artifact">${env.BUILD_URL}artifact</a></p>
'''
    script.emailext(to: mailto,
        subject: subject,
        mimeType: mimeType,
        body: script.renderTemplate(body,[
            "component":component,
            "env":script.env,
            "GREET":script.renderTemplate(greet[randomKey],[
                "component":component,
            ]),
        ])
    )
  }

  /**
  * Wait user input to approve the load
  * @return Map<T,String> [ newtag_ref:  // Commit sha1 or branch or tag
                                        // where to place the new tag
                 ,newtag:  // New tag name
                 ,prevtag  // Previous tag name ]
  */
  def release_input(options=[:]) {
    String newtag_ref = options.newtag_ref ? options.newtag_ref : "HEAD"
    String prevtag    = options.prevtag    ? options.prevtag    : null
    String newtag     = options.newtag     ? options.newtag     : null
    String id         = options.id         ? options.id         : null

    return script.input(id: id, message:'Would you like to release the load?',
      parameters: [
        script.string(name: 'newtag_ref',
          defaultValue: newtag_ref,
          description: 'Commit sha1 or branch or tag where to place the new tag'),
        script.string(name: 'newtag',
          defaultValue: '',
          description: '''New tag name will be calculated automaticaly if empty:
if prevtag = 1.0.0 => newtag = 1.0.1'''),
        script.string(name: 'prevtag',
         defaultValue: '',
         description: 'Previous tag name will be retrived from the repository if empty  ')
      ] // input parameters
    ) // input
  }
//"


} // Class

