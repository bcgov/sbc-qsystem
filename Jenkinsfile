node('maven') {
stage ('OpenShift Build')
{
	openshiftBuild(buildConfig: 'qsystem', showBuildLogs: 'true')
}

stage('Copy Code'){
   git url: 'https://github.com/bcgov/sbc-qsystem.git'
}

stage('Sonar'){
	   dir('sonar-runner'){ 
			sh returnStdout: true, script: './gradlew sonarqube -Dsonar.host.url=http://sonarqube-servicebc-customer-flow-tools.pathfinder.gov.bc.ca -Dsonar.verbose=true --stacktrace'
	   }
   }
}