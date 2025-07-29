# Introduction to Spring AI

This repository contains source code examples for Spring AI.
This project uses private Docker repositories and GitHub, requiring specific deployment configurations for secure access and management.

Application runs on port 8080/30080

## AI Test Prompts
A collection of AI test prompts can be found in the [AI-Test.md](AI-Test.md) file. These prompts were developed by Mathew Berman to test and evaluate the capabilities of different AI models.

## 1. OpenAI Platform and Monitoring

### 1.1 Important URLs
- Main Platform: https://platform.openai.com/
- API Usage and Consumption: https://platform.openai.com/usage
- Billing Details: https://platform.openai.com/billing/overview
- API Keys: https://platform.openai.com/api-keys

### 1.2 Monitoring Information
#### Usage Dashboard
- Current consumption
- Historical usage
- Model-specific breakdown
- Cost estimates

#### Billing Dashboard
- Current balance
- Payment history
- Subscription details
- Usage limits

### 1.3 API Key Configuration
The Openai key (OPENAI_API_KEY) has been placed as environment variable in:
* Junit
* Maven
* Spring Boot Runner and Unit Tests: run PowerShell script set-openapi-key-as-env.ps1.run.xml via intellij run config.
  This script sets the environment variables by creating and using the file:
  File: [.run/.openapi-key-env](.run/.openapi-key-env)

For local development you need to set:
Set the OPENAI_API_KEY in Windows Credentials as Generic Credential:
- Internet or network address: OPENAI_API_KEY
- Username: OPENAI_API_KEY
- Password: key from keypass

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
