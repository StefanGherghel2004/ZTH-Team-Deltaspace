$ErrorActionPreference = "Stop"

$IMAGE_NAME = "deltaspace"
$IMAGE_VERSION = "latest"

Write-Host "### Pasul 1: Construirea imaginii Docker... ###" -ForegroundColor Blue
docker build -t "${IMAGE_NAME}:${IMAGE_VERSION}" .
if ($LASTEXITCODE -ne 0) { throw "Eroare la construirea imaginii Docker!" }

Write-Host "`n### Pasul 2: Salvarea imaginii Docker intr-o arhiva tar... ###" -ForegroundColor Blue
docker save -o "${IMAGE_NAME}.tar" "${IMAGE_NAME}:${IMAGE_VERSION}"
if ($LASTEXITCODE -ne 0) { throw "Eroare la salvarea imaginii Docker!" }

Write-Host "`n### Pasul 3: Crearea arhivei proiectului... ###" -ForegroundColor Blue
tar -czvf "${IMAGE_NAME}-with-code.tar.gz" "${IMAGE_NAME}.tar" Dockerfile build.ps1 pom.xml src
if ($LASTEXITCODE -ne 0) { throw "Eroare la arhivare!" }

Write-Host "`n### Proces finalizat cu succes! ###" -ForegroundColor Green