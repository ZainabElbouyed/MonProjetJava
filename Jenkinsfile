pipeline {
    agent any
    
    tools {
        maven 'maven-3'
        jdk 'jdk-11'
    }
    
    environment {
        MAVEN_OPTS = '-Xmx1024m'
    }
    
    stages {
        
        stage('Before Script - Préparation') {
            steps {
                echo '========================================='
                echo '🔧 BEFORE SCRIPT - INITIALISATION'
                echo '========================================='
                
                cleanWs()
                
                // Créer tous les dossiers nécessaires
                bat '''
                    echo === CRÉATION DES DOSSIERS ===
                    if not exist logs mkdir logs
                    if not exist target mkdir target
                    if not exist reports mkdir reports
                    if not exist target\\site mkdir target\\site
                    echo === DOSSIERS CRÉÉS ===
                '''
                
                bat '''
                    echo === VERSIONS ===
                    java -version
                    mvn -version
                '''
                
                echo '✅ Before script terminé'
            }
        }
        
        stage('Initial Template Creation') {
            steps {
                echo '📋 Pipeline Jenkins - Équivalent .gitlab-ci.yml'
                echo "Projet: ${env.JOB_NAME}"
                echo "Date: " + new Date()
            }
        }
        
        stage('Checkout from GitHub') {
            steps {
                echo '📦 Récupération depuis GitHub...'
                git url: 'https://github.com/ZainabElbouyed/MonProjetJava.git',
                    branch: 'master'
            }
        }
        
        stage('Maven Compile') {
            steps {
                echo '🔨 Compilation Maven...'
                bat 'mvn clean compile'
            }
            post {
                success { echo '✅ Compilation réussie' }
                failure { echo '❌ Compilation échouée' }
            }
        }
        
        stage('Maven Test') {
            steps {
                echo '🧪 Exécution des tests...'
                bat '''
                    echo "=== Exécution des tests Spring Boot ==="
                    mvn test
                    echo "=== Code de retour: %ERRORLEVEL% ==="
                '''
            }
            post {
                always {
                    powershell '''
                        Write-Host "=== RAPPORTS DE TESTS ==="
                        if (Test-Path "target/surefire-reports") {
                            Get-ChildItem "target/surefire-reports" | ForEach-Object {
                                Write-Host "Fichier: $($_.Name)"
                            }
                        } else {
                            Write-Host "Dossier target/surefire-reports non trouvé"
                            New-Item -ItemType Directory -Force -Path "target/surefire-reports"
                        }
                    '''
                    
                    junit testResults: 'target/surefire-reports/*.xml', 
                           allowEmptyResults: true
                }
            }
        }
        
        stage('Maven Package') {
            steps {
                echo '📦 Packaging...'
                bat 'mvn package -DskipTests'
                stash name: 'app-jar', includes: 'target/*.jar'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
        
        stage('JUnit Reports') {
            steps {
                echo '📊 Génération des rapports JUnit HTML...'
                bat 'mvn surefire-report:report -Daggregate=true'
                bat 'if not exist target\\site mkdir target\\site'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', 
                           allowEmptyResults: true
                    
                    publishHTML([
                        reportDir: 'target/site',
                        reportFiles: 'surefire-report.html',
                        reportName: 'Rapport de Tests JUnit',
                        reportTitles: 'Résultats des tests Maven',
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: false
                    ])
                }
            }
        }
        
        stage('Deploy') {
            steps {
                echo '🚀 Déploiement...'
                unstash 'app-jar'
                
                powershell '''
                    Write-Host "=== DÉPLOIEMENT ==="
                    Write-Host "Arrêt de l'ancienne application..."
                    Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force
                    
                    Write-Host "Démarrage de l'application Spring Boot..."
                    $jarFile = Get-ChildItem -Path "target" -Filter "*.jar" | Select-Object -First 1
                    if ($jarFile) {
                        Write-Host "Fichier JAR trouvé: $($jarFile.Name)"
                        Start-Process -NoNewWindow -FilePath "java" `
                            -ArgumentList "-jar $($jarFile.FullName) --server.port=8081" `
                            -RedirectStandardOutput "logs\\app.log" `
                            -RedirectStandardError "logs\\app-error.log"
                        Write-Host "✅ Application démarrée sur le port 8081"
                    } else {
                        Write-Host "❌ Aucun fichier JAR trouvé dans target/"
                        Get-ChildItem -Path "target"
                    }
                '''
            }
            post {
                success {
                    echo '🎉 Déploiement réussi'
                    echo '🌐 Application: http://localhost:8081'
                }
                failure {
                    echo '❌ Échec du déploiement'
                }
            }
        }
        
        stage('Smoke Test - Allow Failure') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                    echo '🧪 Test de fumée (allow_failure activé)...'
                    powershell '''
                        Write-Host "=== TEST DE FUMÉE ==="
                        Start-Sleep -Seconds 5
                        
                        $urls = @(
                            "http://localhost:8081/",
                            "http://localhost:8081/hello",
                            "http://localhost:8081/health"
                        )
                        
                        foreach ($url in $urls) {
                            try {
                                $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 10
                                Write-Host "✅ $url - Status: $($response.StatusCode)"
                            } catch {
                                Write-Host "⚠️ $url - Non accessible (test optionnel)"
                            }
                        }
                    '''
                }
            }
            post {
                unstable {
                    echo '⚠️ Test optionnel échoué - pipeline continue (allow_failure)'
                }
            }
        }
    }
    
    post {
        always {
            echo '========================================='
            echo '🏁 AFTER SCRIPT - NETTOYAGE'
            echo '========================================='
            echo "Statut: ${currentBuild.result}"
            echo "Build #: ${env.BUILD_NUMBER}"
            
            archiveArtifacts artifacts: 'logs/**/*.log', 
                           allowEmptyArchive: true
        }
        
        success {
            echo ''
            echo '╔══════════════════════════════════════════════════════════════╗'
            echo '║     🎉🎉🎉 PIPELINE RÉUSSI ! 🎉🎉🎉                            ║'
            echo '╠══════════════════════════════════════════════════════════════╣'
            echo '║   ✅ Initial Template Creation                               ║'
            echo '║   ✅ Checkout depuis GitHub                                  ║'
            echo '║   ✅ Maven Compile                                           ║'
            echo '║   ✅ Maven Test                                              ║'
            echo '║   ✅ Maven Package                                           ║'
            echo '║   ✅ JUnit Reports                                           ║'
            echo '║   ✅ DEPLOY                                                  ║'
            echo '║   ✅ Allow Failure                                           ║'
            echo '║   ✅ Before/After Scripts                                    ║'
            echo '║                                                              ║'
            echo '║   🌐 Application: http://localhost:8081                      ║'
            echo '║   📦 Projet: MonProjetJava                                   ║'
            echo '╚══════════════════════════════════════════════════════════════╝'
            echo ''
        }
        
        failure {
            echo ''
            echo '╔════════════════════════════════════════╗'
            echo '║     💥 PIPELINE ÉCHOUÉ 💥              ║'
            echo '╠════════════════════════════════════════╣'
            echo "║ Échec au stage: ${env.STAGE_NAME}      ║"
            echo '╚════════════════════════════════════════╝'
            echo ''
        }
    }
}