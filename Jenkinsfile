node {
//stage ('build')
//openshiftBuild(buildConfig: 'qsystem', showBuildLogs: 'true')

stage('checkout source'){
   git url: 'https://github.com/bcgov/sbc-qsystem.git'
}
stage('sonar'){
	   dir('sonar-runner'){ 
            sh 'ls -l -srt'	   
			stage('execute sonar-runner'){
				sh './gradlew sonarqube -Dsonar.host.url=https://jenkins-pipeline-svc-servicebc-customer-flow-tools.pathfinder.gov.bc.ca -Dsonar.verbose=true --stacktrace'
			}
	   }
   }
   
}