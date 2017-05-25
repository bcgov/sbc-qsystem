node {
//stage ('build')
//openshiftBuild(buildConfig: 'qsystem', showBuildLogs: 'true')

stage('checkout source'){
   git url: 'https://github.com/GeorgeWalker/sbc-qsystem.git', branch: 'sonar'
}
stage('sonar'){
	   dir('sonar-runner'){ 
            sh 'ls -l -srt'	   
			stage('execute sonar-runner'){
				sh returnStdout: true, script: 'gradlew sonarqube -Dsonar.host.url=https://jenkins-pipeline-svc-servicebc-customer-flow-tools.pathfinder.gov.bc.ca -Dsonar.verbose=true --stacktrace'
			}
	   }
   }
   
}