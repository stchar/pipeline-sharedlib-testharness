/**
 * Process groovy template
 * @argument template string groovy template
 * @argument bindings any vars to be used in the template
 * @return string
 *
 * @note template shouldn't contain any calls to pipeline methods
 *       only static data, if you need to generate something please perform it before to
 *       call renderTemplate
 * */
def call(String template, Map bindings){
  def engine = new groovy.text.StreamingTemplateEngine()
  return engine.createTemplate(template).make(bindings).toString()
}