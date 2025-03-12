# CONTRIBUTING to athena-portal
> Rules for checking out and creating pull request with proper pattern and messages.

### Checkout Rule

> Create a new BRANCH with JIRA ticket Number in it from `develop` branch to raise any PR.

### Commit Rules

**Before procced, Execute `ln -sf ../../hooks/commit-msg .git/hooks/commit-msg` command in GIT Bash on root path of your repository**

1. Commit message must have JIRA ticket number with pattern --> JIRA-123 
2. Commit message must be at least 40 character long.
3. The branch from which you are creating a PR must have JIRA number in its name.

### Pull Request

> Each Pull request must have proper title and description with **WHAT** and **WHY** it changed.