# Jenkins Shared Library Test Harness Example
This repository contains examples of a Jenkins Shared Library
and JenkinsPipelineUnit test harness (see slides https://slides.com/stasovchar/deck-2)

## Repository Structure
```
.
├── jobs                              # Template pipeline scripts to load the lib
│   │                                 # are used by unit tests
│   └── template
|       |
│       └── template.groovy
├── src
│   └── org
│       └── hcm
│           └── libjenkins
│               └── *.groovy         # Examples of Library class
├── test
│   ├── integration
│   │   └── groovy
│   │       └── *.groovy             # Integration tests
│   └── unit
│       └── groovy
│           └── *.groovy             # JenkinsPipelineUnit tests
└── vars
    └── *.groovy                     # Jenkins pipeline shared library vars objects
```

## Contribution

###  Testing
```
./gradlew check

# Runing gradle behind a proxy
# ./gradlew -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=3128 check
```

## Slides
* `EN_US`: https://slides.com/stasovchar/deck-2

## Video
* `RU_RU`: TBD
* `EN_US`: TBD


## Additional Links
* https://jenkins.io/doc/book/pipeline/shared-libraries/
* https://www.cloudbees.com/blog/top-10-best-practices-jenkins-pipeline-plugin
* https://github.com/lesfurets/JenkinsPipelineUnit
* https://github.com/mkobit/jenkins-pipeline-shared-libraries-gradle-plugin
* https://github.com/jenkinsci/jenkins-test-harnes
