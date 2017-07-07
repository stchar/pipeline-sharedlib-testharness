# Jenkins Shared Library Test Harness Example
This repository contains examples of a Jenkins Shared Library
and JenkinsPipelineUnit test harness

## Repository Structure
```
.
├── jobs
│   │
│   └── template                      # Template pipeline scripts to load the lib
|       |                             # are used by unit tests
│       └── pipeline
│           └── template.groovy
├── src
│   ├── org
│   │   └── hcm
│   │       └── libjenkins
│   │           └── Gitlab.groovy    # An example of a class
│   │
│   └── test
│       └── groovy
│           └── *.groovy             # JenkinsPipelineUnit tests
└── vars
    └── gitlab_stage.groovy          # Wrapper for gitalbCommitStatus


## Contribution

###  Testing
```
./gradlew test

# Runing gradle behind a proxy
./gradlew -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=3128 test
```

#### Additional Links
* https://jenkins.io/doc/book/pipeline/shared-libraries/
* https://www.cloudbees.com/blog/top-10-best-practices-jenkins-pipeline-plugin
* https://github.com/lesfurets/JenkinsPipelineUnit
