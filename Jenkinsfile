pipeline {
   agent any

   tools {
      maven "M3"
   }

   stages {
      stage('BUILD') {
         steps {
            git 'https://github.com/kmasani81/WebGoatLocal.git'

            // Run Maven
            sh "mvn -Dmaven.test.failure.ignore=true clean package"
         }
      }
      stage('Docker Build') {
         steps {
            sh "echo 'Running Docker build ..' "
            script {
              SHORT_HASH = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
              DOCKER_RELEASE_TAG = "MYAPP-${SHORT_HASH}"
            }
            echo "DOCKER_RELEASE_TAG:  $DOCKER_RELEASE_TAG"
            sh "cd $WORKSPACE/webwolf && docker build -t kmasani/webwolf:${DOCKER_RELEASE_TAG} ."
         }
      }

      stage('Scans') {
         steps {
            parallel(
               SonarQube: {
                  withCredentials([ usernamePassword(credentialsId: 'SonarCred', \
                                    usernameVariable: 'SONARUSER', \
                                    passwordVariable: 'SONARKEY') ]){
                      sh "mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=${SONARKEY}"
                      echo "TODO: Parse analysis results .. "
                  }
               },
               GithubReport: {
                  echo "Getting Github dependabot alerts .."
                  withCredentials([ usernamePassword(credentialsId: 'Github', \
                                    usernameVariable: 'GITUSER', \
                                    passwordVariable: 'GITKEY') ]){
                      sh "python3 /opt/tools/github_cli/report_org_vuln.py --key ${GITKEY}"
                  }
               },
               SCAAnalysis: {
                  echo "TODO: Pending to be included."
                  sh "sleep 10"
               },
               ContainerScan: {
                  echo "Running Container scan .. "
                  // sh "cd $WORKSPACE && /opt/tools/anchore_cli/inline_scan-v0.6.0 -r -t 500  kmasani/webwolf:MYAPP-demo"
                  // sh "/usr/bin/python /opt/devops/scripts/parse_anchore_analysis.py --outfile $WORKSPACE/anchore-reports/webgoat-local_latest-vuln.json"
               }
            )
         }
      }

      stage('Scans: Container') {
         when {
             anyOf {
                 branch 'master';
                 branch 'develop' 
             }
         }
         steps {
             echo "Running Container scan .. "
             sh "cd $WORKSPACE && /opt/tools/anchore_cli/inline_scan-v0.6.0 scan -r kmasani/webwolf:${DOCKER_RELEASE_TAG}"
         }
      }

      stage('Checkpoint') {
          steps {
              echo "Analyze if all scan-results are within threshold"
              sh "python3 /opt/tools/custom/checkpoint.py"
          }
      }

      stage('Publish Artifacts') {
         steps {
            sh "echo 'Pushing to Nexus'"
            sh "sleep 30"
            //sh "echo 'Pushing Docker .. ' "
            //sh "docker push kmasani/myapp:${DOCKER_RELEASE_TAG}"
         }
      }

      stage('Deploy') {
         steps {
            sh "echo 'Deploying Docker ..' "
            // sh "/usr/bin/python /opt/devops/scripts/deploy_runner.py ${DOCKER_RELEASE_TAG}"
         }
      }      

   }
}
