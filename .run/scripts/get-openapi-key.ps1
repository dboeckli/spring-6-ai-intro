# Einmalig installieren
# Install-Module -Name CredentialManager -Scope CurrentUser

# Den OPENAI_API_KEY in der Windows Anmeldeinformation als Generische Anmeldeinformation setzen:
# - Internet oder Netzwerkadresse: OPENAI_API_KEY
# - Benutzername: OPENAI_API_KEY
# - Kennwort: key aus dem keypass

$cred = Get-StoredCredential -Target 'OPENAI_API_KEY'

if ($cred -and $cred.Password) {
    $token = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto(
        [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($cred.Password)
    )
    $env:OPENAI_API_KEY = $token
    Write-Host "Token erfolgreich in Env-Var gesetzt."

    Set-Content -Path "..\.openapi-key-env" -Value "OPENAI_API_KEY=$token"
    Write-Host "Env .openapi-key-env file written."
} else {
    Write-Warning "Credential 'OPENAI_API_KEY' nicht gefunden oder leer."
}