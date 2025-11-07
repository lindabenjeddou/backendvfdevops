# Script de démarrage Docker Compose - Full Stack Application
# PowerShell script pour Windows

Write-Host "🐳 Démarrage de la stack Full-Stack (Backend + Frontend + MySQL)..." -ForegroundColor Cyan

# Vérifier si Docker est installé
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Docker n'est pas installé ou n'est pas dans le PATH" -ForegroundColor Red
    Write-Host "Téléchargez Docker Desktop: https://www.docker.com/products/docker-desktop/" -ForegroundColor Yellow
    exit 1
}

# Vérifier si Docker est démarré
docker info > $null 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Docker Desktop n'est pas démarré" -ForegroundColor Red
    Write-Host "Veuillez démarrer Docker Desktop et réessayer" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ Docker est prêt" -ForegroundColor Green

# Note: Avec le Dockerfile multi-stage, le build est fait dans Docker
Write-Host "ℹ️  Le build du backend sera effectué par Docker (multi-stage build)" -ForegroundColor Cyan

# Vérifier si .env existe
if (-not (Test-Path ".env")) {
    Write-Host "⚠️  Fichier .env non trouvé" -ForegroundColor Yellow
    if (Test-Path ".env.example") {
        Write-Host "📝 Copie de .env.example vers .env..." -ForegroundColor Cyan
        Copy-Item ".env.example" ".env"
        Write-Host "✅ .env créé" -ForegroundColor Green
    } else {
        Write-Host "⚠️  .env.example non trouvé, continuons quand même..." -ForegroundColor Yellow
    }
}

# Arrêter les anciens conteneurs
Write-Host "🛑 Arrêt des anciens conteneurs..." -ForegroundColor Cyan
docker-compose down > $null 2>&1

# Démarrer les services
Write-Host "🚀 Démarrage des services Docker..." -ForegroundColor Cyan
docker-compose up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================================" -ForegroundColor Green
    Write-Host "✅ Stack Full-Stack démarrée avec succès!" -ForegroundColor Green
    Write-Host "========================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "📍 Services disponibles:" -ForegroundColor Cyan
    Write-Host "   🌐 Frontend (React):  http://localhost" -ForegroundColor White
    Write-Host "   🔧 Backend API:       http://localhost:8089" -ForegroundColor White
    Write-Host "   📊 Health Check:      http://localhost:8089/actuator/health" -ForegroundColor White
    Write-Host "   🗃️  PhpMyAdmin:        http://localhost:8081" -ForegroundColor White
    Write-Host "   🗄️  MySQL:             localhost:3306 (pidev/pidev123)" -ForegroundColor White
    Write-Host ""
    Write-Host "⏳ Attendez ~1-2 minutes que tous les services démarrent..." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "📊 Vérifier les logs:" -ForegroundColor Cyan
    Write-Host "   docker-compose logs -f" -ForegroundColor Gray
    Write-Host ""
    Write-Host "🛑 Arrêter tous les services:" -ForegroundColor Cyan
    Write-Host "   docker-compose down" -ForegroundColor Gray
    Write-Host ""
    
    # Afficher les statuts
    Start-Sleep -Seconds 3
    Write-Host "📋 Statut des conteneurs:" -ForegroundColor Cyan
    docker-compose ps
    
} else {
    Write-Host "❌ Échec du démarrage Docker Compose" -ForegroundColor Red
    Write-Host "Vérifiez les logs avec: docker-compose logs" -ForegroundColor Yellow
    exit 1
}
