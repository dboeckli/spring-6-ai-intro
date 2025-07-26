# Introduction to Spring AI

This repository contains source code examples for Spring AI.

Application runs on port 8080

* https://platform.openai.com/settings/organization/billing/overview
* https://platform.openai.com/settings/organization/api-keys
The Openai key (OPENAI_API_KEY) has been placed as environment variable in:
* Junit
* Maven
* Spring Boot Runner and Unit Tests: run PowerShell script set-openapi-key-as-env.ps1.run.xml via intellij run config.
  This script sets the environment variables by creating and using the file:
  File: [.run/.openapi-key-env](.run/.openapi-key-env)

For local development you need to set:
Den OPENAI_API_KEY in der Windows Anmeldeinformation als Generische Anmeldeinformation setzen:
- Internet oder Netzwerkadresse: OPENAI_API_KEY
- Benutzername: OPENAI_API_KEY
- Kennwort: key aus dem keypass


## Deployment with Helm

Be aware that we are using a different namespace here (not default).

To run maven filtering for destination target/helm
```bash
mvn clean install -DskipTests 
```

Go to the directory where the tgz file has been created after 'mvn install'
```powershell
cd target/helm/repo
```

unpack
```powershell
$file = Get-ChildItem -Filter *.tgz | Select-Object -First 1
tar -xvf $file.Name
```

install
```powershell
$APPLICATION_NAME = Get-ChildItem -Directory | Where-Object { $_.LastWriteTime -ge $file.LastWriteTime } | Select-Object -ExpandProperty Name
helm upgrade --install $APPLICATION_NAME ./$APPLICATION_NAME --namespace spring-6-ai-intro --create-namespace --wait --timeout 8m --debug --render-subchart-notes
```

show logs
```powershell
kubectl get pods -l app.kubernetes.io/name=$APPLICATION_NAME -n spring-6-ai-intro
```
replace $POD with pods from the command above
```powershell
kubectl logs $POD -n spring-6-ai-intro --all-containers
```

test
```powershell
helm test $APPLICATION_NAME --namespace spring-6-ai-intro --logs
```

uninstall
```powershell
helm uninstall $APPLICATION_NAME --namespace spring-6-ai-intro
```

delete all
```powershell
kubectl delete all --all -n spring-6-ai-intro
```

create busybox sidecar
```powershell
kubectl run busybox-test --rm -it --image=busybox:1.36 --namespace=spring-6-ai-intro --command -- sh
```

You can use the actuator rest call to verify via port 30080
