pipeline {
   agent any

   tools {
      maven "M3"
   }

   stages {
      stage('Build') {
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
                      sh "python3 /opt/tools/custom/generate_sonar_csv.py --key ${SONARKEY} --outfile sonar_vuln.csv"
                      sh "ls -trl $WORKSPACE"
                      sh "cp $WORKSPACE/sonar_vuln.csv /Users/kiran/Downloads/files_to_process/"
                  }
               },
               GithubReport: {
                  echo "Getting Github dependabot alerts .."
                  withCredentials([ usernamePassword(credentialsId: 'Github', \
                                    usernameVariable: 'GITUSER', \
                                    passwordVariable: 'GITKEY') ]){
                      sh "python3 /opt/tools/github_cli/report_org_vuln.py --key ${GITKEY} --type csv --output github_vuln_data.csv"
                      sh "ls -trl $WORKSPACE"
                      sh "cp $WORKSPACE/github_vuln_data.csv /Users/kiran/Downloads/files_to_process/"
                  }
               },
               SCAAnalysis: {
                  echo "TODO: Pending to be included."
                  sh "sleep 10"
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
             sh "python3 /opt/tools/custom/generate_anchore_csv.py  --file $WORKSPACE/anchore-reports/webwolf_${DOCKER_RELEASE_TAG}-vuln.json  --outfile anchore-vuln-data.csv"
             sh "ls -trl"
             sh "cp $WORKSPACE/anchore-vuln-data.csv /Users/kiran/Downloads/files_to_process/"
         }
      }

      stage('RiskSense Upload') {
          steps {
              echo "Uploading scan-analysis results to RiskSense Platform"
              sh "ls -trl /Users/kiran/Downloads/files_to_process/"
              sh "python3 /opt/tools/upload_to_platform_v1.0/upload_to_platform.py"
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
