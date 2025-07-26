#!/bin/bash

# Safer Bash scripting options
# https://web.archive.org/web/20190329060125/https://vaneyckt.io/posts/safer_bash_scripts_with_set_euxo_pipefail/
set -o pipefail # Don't hide errors within pipes
set -o nounset  # Abort on unbound variable
set -o errexit  # Abort on nonzero exit status

# Function to log errors and exit
function error_exit {
  echo "Error: $1" >&2
  exit 1
}

# Check prerequisites
command -v git >/dev/null 2>&1 || error_exit "Git is not installed or not in PATH."
command -v mvn >/dev/null 2>&1 || error_exit "Maven is not installed or not in PATH."

echo 'Will change the version in pom.xml files...'

if [ "$EVENT_NAME" == "pull_request" ]; then
    BRANCH_NAME="PR_$HEAD_REF"
fi    
echo "### Branch is $BRANCH_NAME"

# Replace `/` and `-` with `_` and remove any other unwanted characters
branch=$(echo "$BRANCH_NAME" | sed 's/[^a-zA-Z0-9]/_/g')
echo "### Cleaned branch is  $branch"
echo "BRANCH_NAME=$branch" >>"$GITHUB_OUTPUT"

# Get current Maven project version
MVN_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -e -DforceStdout 2>&1) || { error_output=$MVN_VERSION; error_exit "Failed to evaluate Maven project version. Error: $error_output"; }
ARTIFACT_ID=$(mvn help:evaluate -Dexpression=project.artifactId -q -e -DforceStdout 2>&1) || { error_output=$ARTIFACT_ID; error_exit "Failed to evaluate Maven artifact ID. Error: $error_output"; }
GROUP_ID=$(mvn help:evaluate -Dexpression=project.groupId -q -e -DforceStdout 2>&1) || { error_output=$GROUP_ID; error_exit "Failed to evaluate Maven group ID. Error: $error_output"; }
ORGANIZATION=$(mvn help:evaluate -Dexpression=project.organization.name -q -e -DforceStdout 2>&1) || { error_output=$ORGANIZATION; error_exit "Failed to evaluate ORGANIZATION. Error: $error_output"; }

# Log to GitHub Actions output
echo "MVN_VERSION=$MVN_VERSION" >>"$GITHUB_OUTPUT"
echo "ARTIFACT_ID=$ARTIFACT_ID" >>"$GITHUB_OUTPUT"
echo "GROUP_ID=$GROUP_ID" >>"$GITHUB_OUTPUT"
echo "ORGANIZATION=$ORGANIZATION" >>"$GITHUB_OUTPUT"
echo "### Current version is: $MVN_VERSION"

# Extract version parts
suffix=$(echo "$MVN_VERSION" | cut -d '-' -f 2 || true)
prefix=$(echo "$MVN_VERSION" | cut -d '-' -f 1 || true)
echo "### Suffix is: $suffix"
echo "### Prefix is: $prefix"

# Build new version
if [[ "$branch" != "master" ]] && [[ "$branch" != "main" ]]; then
  # Calculate available length for branch name
  available_length=$((63 - ${#prefix} - ${#suffix} - 2))  # 2 for the separators

  # Truncate branch name if necessary
  truncated_branch=$(echo "$branch" | cut -c1-$available_length)

  NEW_MAVEN_VERSION="${prefix}_${truncated_branch}-${suffix}"
else
  NEW_MAVEN_VERSION=$MVN_VERSION
fi

# Log new version
echo "BRANCH_MVN_VERSION=$NEW_MAVEN_VERSION" >>"$GITHUB_OUTPUT"
echo "### Changed version in pom.xml files to $NEW_MAVEN_VERSION"
