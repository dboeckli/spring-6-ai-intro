cd target/helm/repo

$file = Get-ChildItem -Filter spring-6-ai-intro-v*.tgz | Select-Object -First 1
tar -xvf $file.Name

$APPLICATION_NAME = Get-ChildItem -Directory | Where-Object { $_.LastWriteTime -ge $file.LastWriteTime } | Select-Object -ExpandProperty Name
helm upgrade --install $APPLICATION_NAME ./$APPLICATION_NAME --namespace spring-6-ai-intro --create-namespace --wait --timeout 8m --debug --render-subchart-notes
