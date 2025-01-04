# Contribution Guidelines

First off, thanks for showing interest in contributing to the **webscout** project! Your contributions help make this protocol a more robust one, and I'm excited to see what improvements and ideas you bring to the table.

This repository contains the official implementation of scraper using the ProtoScout protocol and therefore, I welcome your contributions, big or small!

Before continuing though, please refer to our [Code of Conduct](./CODE_OF_CONDUCT.md) to learn more about our values and expectations for contributors.

## Table of Contents

- [Contribution Guidelines](#contribution-guidelines)
  - [Table of Contents](#table-of-contents)
  - [How Can I Contribute?](#how-can-i-contribute)
    - [Reporting Bugs](#reporting-bugs)
    - [Suggesting Features or Enhancements](#suggesting-features-or-enhancements)
    - [Other suggestions](#other-suggestions)
    - [Pull Requests](#pull-requests)
  - [Code Style](#code-style)
  - [License](#license)

## How Can I Contribute?

You can contribute to the **webscout** in a few ways. The easiest method is to [start a discussion](https://github.com/lengors/webscout/discussions) or [open an issue](https://github.com/lengors/webscout/issues) on the [repository](https://github.com/lengors/webscout). If you’re feeling ambitious, feel free to jump right into coding and submit a [pull request](https://github.com/lengors/webscout/pulls).

The preferred method of contribution is to start a discussion so that both the maintainers and the community are made aware of your suggestion and/or improvement and so that both can assess if it makes sense to promote the discussion into an issue worth tracking. It should be noted that even if the discussion is promoted to an issue, it may never end up being worked on, as the need for the proposed changes may vary over time.

The details for the various ways of contributing are as described below:

### Reporting Bugs

If you discover a bug or unexpected behavior, please [post a question on our community](https://github.com/lengors/webscout/discussions/new?category=q-a), [start a general discussion](https://github.com/lengors/webscout/discussions/new?category=general) or [report the bug as an isuse](https://github.com/lengors/webscout/issues/new?template=bug_report.yml) and follow the instructions there.
Please be as descriptive as possible, including steps to reproduce the issue and any relevant details.

### Suggesting Features or Enhancements

We welcome your ideas for new features or improvements to existing ones. To suggest a feature, [share your idea with the community](https://github.com/lengors/webscout/discussions/new?category=ideas) or [request a feature as an issue](https://github.com/lengors/webscout/issues/new?template=feature_request.yml) and follow the instructions there. Please be as descriptive as possible. You may even include implementation proposals!

### Other suggestions

If you instead want to suggest improvements to documentation, deployment pipelines and build processes, or report other issues or suggestions, please [start a general discussion](https://github.com/lengors/webscout/discussions/new?category=general) or [create a task for us](https://github.com/lengors/webscout/issues/new?template=create_task.yml) and follow the instructions there. Please be as descriptive as possible and select the appropriate labels.

### Pull Requests

We also encourage direct contributions that improve the project through pull requests. To submit your changes, follow these steps:

1. Fork the project.
2. Create a branch for your feature, bug fix or other changes.
3. Implement your changes and ensure they adhere to our code style and conventions.
4. Write tests to validate your changes (if applicable).
5. Submit a pull request (PR) to our [GitHub repository](https://github.com/lengors/webscout).

Your PR will be reviewed by project maintainers. If necessary, we may request further changes before merging.

## Code Style

To maintain a consistent codebase, we use [Ktlint](https://github.com/pinterest/ktlint) and the [respective gradle plugin](https://github.com/JLLeitschuh/ktlint-gradle) with its default settings. Make sure to run ktlint locally before pushing your changes to catch any issues early.

For commits, we’re keeping it fun and expressive with [Gitmoji](https://gitmoji.dev/). Use Gitmoji to prefix your commit messages, making them both informative and visually appealing. It's also important for our build system as it will automatically generate a new version following the [Semantic Versioning 2.0.0](https://semver.org/) specification.

If you're new to Gitmoji, here’s a quick example:

```
✨ Add new feature to handle user authentication
```

## License

By contributing to **webscout**, you agree that your contributions will be licensed under [Apache-2.0 license](./LICENSE). This means your contributions will be in the public domain, making them available for anyone to use for any purpose.

Thanks again for helping make **webscout** a better protocol. Your contributions are truly valued, and I look forward to working together!
