# Kotlin Projects

This repository is a central place for multiple independent Kotlin projects. Each project is kept in its own branch so that project histories, build setups, and documentation remain isolated while the repository serves as a single index.

## Repository layout

- `main` — Top-level README and repository index. No project source is kept here.
- `<project-branch>` — Each project has its own branch (branch name should reflect the project, e.g., `calculator-app`, `ktor-server`, `my-library-v1`). Project branches contain the project source, build files, and a project-level README with usage instructions.

## Quick start

1. Clone the repository:
   ```
   git clone https://github.com/OtakuSanskar/Kotlin-Projects.git
   cd Kotlin-Projects
   ```
2. Fetch all branches and list them:
   ```
   git fetch --all --prune
   git branch -a
   ```
3. Switch to a project branch:
   ```
   git checkout <branch-name>
   ```
4. Read the README inside that branch for project-specific build and run instructions.

## Adding a new project

- Create a new branch named after the project:
  ```
  git checkout -b <new-project-name>
  ```
- Add project files and a project README describing build and usage steps.
- Commit and push the branch:
  ```
  git add .
  git commit -m "Add <new-project-name> project"
  git push -u origin <new-project-name>
  ```

## Contributions and issues

- For contributions, open a pull request from your project branch to the corresponding branch in this repository (or to a new branch if appropriate).
- Use issues to report bugs or request features; mention the relevant project branch in the issue title or body.

## Licenses

- Each project branch may include its own LICENSE file. If you want a repository-wide license, add a LICENSE file to `main`.

## Contact

If you want me to add or update this README in the repository, tell me how you'd like it added (commit message, branch name), and I'll prepare the exact commands or a commit patch you can apply.
