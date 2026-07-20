# Contributing

Thank you for considering contributing to this project! Here are some guidelines to help you get started.

## Reporting Issues

If you find a bug, have an idea for an enhancement, or want to start a discussion, please file an issue in the
repository. Make sure to provide as much detail as possible to help us understand and address the issue. A reproduction
of the issue, including steps to reproduce, is always helpful.

## Branch Naming

When working on an issue, create a new branch starting with `issue-xx` where `xx` is the number of the issue. For
example, if you are working on issue #42, your branch should be named `issue-42-the-issue-to-be-fixed`.

## Submitting a Pull Request

1. Run `mvn verify` to ensure your changes pass all tests and styling.
2. Commit your changes to your branch on your
   fork. [How to create a fork](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/fork-a-repo)
3. Push your branch to your forked repository.
4. Submit a pull request (PR) from your forked repository to the main repository.
5. Reference the issue number in your PR description.

## Code Style

Please ensure your code adheres to the project's coding standards and passes all tests. This helps maintain code quality
and consistency.

## Releasing

### Required GitHub secrets

The following secrets must be configured once under **Settings → Secrets and variables → Actions**:

| Secret                       | Description                                          |
|------------------------------|------------------------------------------------------|
| `GPG_PRIVATE_KEY`            | ASCII-armored GPG private key used to sign artifacts |
| `GPG_PUBLIC_KEY`             | ASCII-armored GPG public key                         |
| `GPG_PRIVATE_KEY_PASSPHRASE` | Passphrase for the GPG key                           |
| `MAVEN_CENTRAL_USERNAME`     | Maven Central (Sonatype) username                    |
| `MAVEN_CENTRAL_PASSWORD`     | Maven Central (Sonatype) token                       |

### How to release

1. Go to **Actions → Publish artifact → Run workflow**.
2. Enter the release version (e.g. `1.5.0`). It must be ≥ the version currently in `pom.xml` (minus `-SNAPSHOT`).
3. Click **Run workflow**.

The workflow will:

- Set the `pom.xml` version to the release version, commit, and tag it.
- Sign, build, and publish the artifacts to Maven Central.
- Bump the `pom.xml` to the next patch SNAPSHOT (e.g. `1.5.1-SNAPSHOT`) and commit.
- Push all commits and the tag to `main`.

## Thank You

We appreciate your contributions and efforts to improve this project. Thank you for your support!
