# Welcome to Webscout &middot; [![GitHub license](https://img.shields.io/github/license/lengors/webscout?color=blue)](https://github.com/lengors/webscout/blob/main/LICENSE) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=lengors_webscout&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=lengors_webscout)

Welcome to **webscout**, a high-performance scraper service designed for seamless configuration and execution of scraping tasks. Built with **Spring Boot**, **WebFlux**, **coroutines**, and **virtual threads**, WebScout empowers you to efficiently manage and execute parallel scraping workflows.

## Features

- **Scraper Specifications**: Define and store reusable configurations for scraping tasks, including optional input parameters.
- **Protocol Compliance**: Uses the [ProtoScout](https://github.com/lengors/protoscout-pojos) protocol for consistent and structured communication across endpoints.
- **Reactive Programming**: Fully asynchronous architecture leveraging WebFlux and coroutines for high-parallelism scraping.
- **API-Driven**: RESTful endpoints documented with **OpenAPI** for easy integration.
- **Optimized Execution**: Designed to handle large-scale scraping operations efficiently.

## Getting Started

#### Clone the repository

```bash
git clone https://github.com/lengors/webscout.git
cd webscout
```

#### Build the project

Ensure you have Gradle and JDK installed. Run:

```bash
./gradlew clean build
```

#### Run tests

```bash
./gradlew clean test
```

## Usage

WebScout provides flexible configuration and execution options to meet your scraping needs.

### API Overview

1. **Specification Management**:
    - Create, update, and delete scraper specifications.
    - Store configurations with optional parameters such as authentication details, PEM certificates, and search filters.

2. **Scraping Requests**:
    - Submit requests with selected specifications, a search term, and additional input.
    - Retrieve scraping results through the REST API.

### Configuration

- **Reactive Framework**: Built on **Spring WebFlux** to handle concurrent requests efficiently.
- **Protocol Definitions**: Implements [ProtoScout](https://github.com/lengors/protoscout-pojos) for shared schema definitions across request and response payloads.
- **Documentation**: Generated with [Dokka](https://kotlinlang.org/docs/dokka-introduction.html). Ensure repository references are updated to reflect your service.
- **OpenAPI Integration**: The API is documented using **Springdoc**. Once the application is running, you can access the OpenAPI UI at: `<host>/swagger-ui.html`

### Build & Deployment

- **Docker Support**: Includes a `Dockerfile` to deploy the service as a container. Modify it as needed for your deployment scenario.
- **CI/CD Pipelines**: Fully automated pipelines for code quality checks, build, testing, publishing, and deployment.

## Documentation and Resources

For detailed guides and additional information, please refer to
our [GitHub Wiki](https://github.com/lengors/webscout/wiki).

If you wish to check an example of the generated API documentation, visit
the [Dokka generated reference](https://lengors.github.io/webscout) page.

## Contributing

Contributions are welcome! Please refer to our [Contribution Guidelines](./CONTRIBUTING.md) for more information on how
to get involved.

## License

This project is licensed under [Apache License Version 2.0](./LICENSE), which places it in the public domain.