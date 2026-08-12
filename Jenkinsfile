#!/usr/bin/env groovy

/*
 * CI pipeline for superdive-backend — Spring Boot 3.4.2, Java 21, Maven.
 *
 * Prerequisite on the Jenkins controller:
 *   Manage Jenkins -> Tools -> JDK installations -> add an installation named 'jdk-21'.
 *   Maven itself is not needed: the build runs through the Maven wrapper (./mvnw),
 *   which downloads the pinned Maven 3.9.9 from .mvn/wrapper/maven-wrapper.properties.
 *
 * Runs on both Linux and Windows agents — every Maven call goes through mvn() below.
 */

def mvn(String goals) {
	if (isUnix()) {
		sh "./mvnw ${env.MAVEN_ARGS} ${goals}"
	} else {
		bat ".\\mvnw.cmd ${env.MAVEN_ARGS} ${goals}"
	}
}

pipeline {

	agent any

	tools {
		jdk 'jdk-21'
	}

	options {
		timestamps()
		buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '10'))
		timeout(time: 30, unit: 'MINUTES')
		disableConcurrentBuilds()
	}

	parameters {
		booleanParam(
			name: 'RUN_DB_TESTS',
			defaultValue: false,
			description: 'Also run BackendApplicationTests. It is a @SpringBootTest that boots the ' +
					'whole context and needs MySQL reachable on the URL in application.properties. ' +
					'Leave off unless this agent has that database.')
		booleanParam(
			name: 'SKIP_TESTS',
			defaultValue: false,
			description: 'Package without running any test. For emergency builds only.')
		booleanParam(
			name: 'DEPLOY',
			defaultValue: false,
			description: 'Run the Deploy stage (main branch only). See the stage body — it is not wired up yet.')
	}

	environment {
		MAVEN_ARGS = '-B -ntp'          // batch mode, no download progress spam
		MAVEN_OPTS = '-Xmx1024m'
		JAR_PATTERN = 'target/backend-*.jar'
	}

	stages {

		stage('Prepare') {
			steps {
				script {
					// mvnw is committed without the executable bit, so a Linux agent
					// cannot run ./mvnw straight from a fresh checkout.
					if (isUnix()) {
						sh 'chmod +x mvnw'
					}
				}
				mvn '-version'
			}
		}

		stage('Build') {
			steps {
				mvn 'clean compile'
			}
		}

		stage('Unit Tests') {
			when {
				expression { !params.SKIP_TESTS }
			}
			steps {
				script {
					// '!Class' is Surefire's exclusion syntax. Unquoted on purpose:
					// it is literal in both cmd.exe and a non-interactive shell.
					String filter = params.RUN_DB_TESTS ? '' : '-Dtest=!BackendApplicationTests -DfailIfNoTests=false'
					mvn "test ${filter}"
				}
			}
			post {
				always {
					junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
				}
			}
		}

		stage('Package') {
			steps {
				// Tests already ran in their own stage; do not run them twice.
				mvn 'package -DskipTests'
			}
			post {
				success {
					archiveArtifacts artifacts: env.JAR_PATTERN, fingerprint: true, onlyIfSuccessful: true
				}
			}
		}

		stage('Deploy') {
			when {
				allOf {
					branch 'main'
					expression { params.DEPLOY }
				}
			}
			steps {
				// TODO: replace with the real deployment for this environment, e.g.
				//   - scp the jar to the app server and restart a systemd unit, or
				//   - docker build / docker push, or
				//   - ansible-playbook deploy.yml
				// Whatever it becomes, keep the DB password out of the Jenkinsfile:
				// use withCredentials([...]) and pass it as -Dspring.datasource.password=...
				echo "Deploy stage is a placeholder — nothing was deployed for build ${env.BUILD_NUMBER}."
			}
		}
	}

	post {
		success {
			echo "SUCCESS — build ${env.BUILD_NUMBER} on ${env.BRANCH_NAME ?: 'local branch'}"
		}
		unstable {
			echo "UNSTABLE — tests reported failures. See the Test Result page."
		}
		failure {
			echo "FAILED — build ${env.BUILD_NUMBER}. See the console log."
		}
	}
}
