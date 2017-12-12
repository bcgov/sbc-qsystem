node('maven') {
    parameters {
        booleanParam(defaultValue: true, description: '', name: 'quickPush')
    }
    stage('checkout') {
        echo "checking out source"
        checkout scm
        echo "Build: ${BUILD_ID}"
    }

    stage('code quality check') {
        if (!params.quickPush) {
            SONARQUBE_PWD = sh (
                        script: 'oc env dc/sonarqube --list | awk  -F  "=" \'/SONARQUBE_ADMINPW/{print $2}\'',
                        returnStdout: true
                        ).trim()
        
            echo "SONARQUBE_PWD: ${SONARQUBE_PWD}"

            SONARQUBE_URL = sh (
                        script: 'oc get routes -o wide --no-headers | awk \'/sonarqube/{ print match($0,/edge/) ?  "https://"$2 : "http://"$2 }\'',
                        returnStdout: true
                        ).trim()
        
            echo "SONARQUBE_URL: ${SONARQUBE_URL}"

            dir('sonar-runner') {
                sh "./gradlew sonarqube -Dsonar.host.url=\"${SONARQUBE_URL}\" -Dsonar.verbose=true --stacktrace -Dsonar.java.binaries=.. -Dsonar.sources=.. -Dsun.jnu.encoding=UTF-8"
            }
        }
    }
    
    stage('build') {
        echo "Building..."
        openshiftBuild bldCfg: 'qsystem', showBuildLogs: 'true'
        openshiftTag destStream: 'qsystem', verbose: 'true', destTag: '$BUILD_ID', srcStream: 'qsystem', srcTag: 'latest'
        openshiftTag destStream: 'qsystem', verbose: 'true', destTag: 'dev', srcStream: 'qsystem', srcTag: 'latest'
   }

   stage('verify') {
        if (!params.quickPush) {
            openshiftVerifyDeployment depCfg: 'qsystem', namespace: 'servicebc-customer-flow-dev', verbose: 'true'
        }
   }

    stage('validation') {
        if (!params.quickPush) {
            dir('functional-tests'){
                try {
                    TEST_USERNAME = sh (
                                script: 'oc env bc/qsystem --list | awk  -F  "=" \'/TEST_USERNAME/{print $2}\'',
                                returnStdout: true
                                ).trim()
                          
                    TEST_PASSWORD = sh (
                                script: 'oc env bc/qsystem --list | awk  -F  "=" \'/TEST_PASSWORD/{print $2}\'',
                                returnStdout: true
                                ).trim()
                          
                    echo "TEST_USERNAME: ${TEST_USERNAME}"
                    echo "TEST_PASSWORD: ${TEST_PASSWORD}"
      
                    sh "export TEST_USERNAME=${TEST_USERNAME}\nexport TEST_PASSWORD=${TEST_PASSWORD}\n./gradlew --debug --stacktrace chromeHeadlessTest"
                } finally {
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'build/reports/**/*'
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'build/test-results/**/*'
                    junit 'build/test-results/**/*.xml'
                }
            }
        }
    }
}

stage('deploy-test') {
    if (!params.quickPush) {
        input "Deploy to test?"
        node('master'){
            openshiftTag destStream: 'qsystem', verbose: 'true', destTag: 'test', srcStream: 'qsystem', srcTag: '$BUILD_ID'
        }
    }
}

stage('deploy-prod') {
    if (!params.quickPush) {
        input "Deploy to prod?"
        node('master'){
            openshiftTag destStream: 'qsystem', verbose: 'true', destTag: 'prod', srcStream: 'qsystem', srcTag: '$BUILD_ID'
        }
    }
}
