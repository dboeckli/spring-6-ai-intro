# Renovate Strategy

## Go here: https://developer.mend.io/github/dboeckli

1. Use separate rules for domboeckli images and other Docker images.
2. For domboeckli images, use Maven versioning strategy to align with Java project versioning.
3. Pin versions for stability, especially for images with 'latest' or single number versions.
4. Create PRs for all updates, no auto-merging.
5. Schedule updates to run before 5am daily.
6. Separate minor and patch updates for better granularity in version control.
7. Use feature branches for all Renovate updates.
