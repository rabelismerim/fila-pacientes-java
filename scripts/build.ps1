param([switch]$Run, [switch]$Test, [switch]$Screenshots)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$project = Join-Path $projectRoot 'FilaPacientes'
$compiler = Get-Command javac -ErrorAction SilentlyContinue
if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME 'bin/javac.exe'))) {
    $javaBin = Join-Path $env:JAVA_HOME 'bin'
} elseif ($compiler) {
    $javaBin = Split-Path $compiler.Source
} else {
    throw 'JDK nao encontrado. Instale um JDK 8 ou superior e configure JAVA_HOME ou o PATH.'
}
$classes = Join-Path $project 'build/classes'
$dist = Join-Path $project 'dist'
New-Item -ItemType Directory -Force $classes, $dist | Out-Null
$sources = @(Get-ChildItem (Join-Path $project 'src') -Filter '*.java' -Recurse | ForEach-Object { $_.FullName })
& (Join-Path $javaBin 'javac.exe') -encoding UTF-8 -source 8 -target 8 -d $classes @sources
if ($LASTEXITCODE -ne 0) { throw 'Falha na compilacao.' }
$jar = Join-Path $dist 'FilaPacientes.jar'
& (Join-Path $javaBin 'jar.exe') cfe $jar filapacientes.Menu -C $classes .
if ($LASTEXITCODE -ne 0) { throw 'Falha ao gerar o JAR.' }
Write-Host "JAR gerado: $jar"
if ($Test -or $Screenshots) {
    $tests = @(Get-ChildItem (Join-Path $project 'test') -Filter '*.java' -Recurse | ForEach-Object { $_.FullName })
    $testClasses = Join-Path $project 'build/test/classes'
    New-Item -ItemType Directory -Force $testClasses | Out-Null
    & (Join-Path $javaBin 'javac.exe') -encoding UTF-8 -source 8 -target 8 -d $testClasses @sources @tests
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao compilar os testes.' }
    $classpath = "$classes;$testClasses"
    if ($Test) {
        & (Join-Path $javaBin 'java.exe') '-Djava.awt.headless=true' -cp $classpath filapacientes.Testes
        if ($LASTEXITCODE -ne 0) { throw 'Falha nos testes.' }
    }
    if ($Screenshots) {
        & (Join-Path $javaBin 'java.exe') '-Djava.awt.headless=true' -cp $classpath filapacientes.Capturas (Join-Path $projectRoot 'docs/screenshots')
        if ($LASTEXITCODE -ne 0) { throw 'Falha ao gerar capturas.' }
    }
}
if ($Run) { & (Join-Path $javaBin 'java.exe') -jar $jar }
