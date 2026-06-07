const branch = process.env.GITHUB_REF_NAME;

const isPreview =
    branch.startsWith("feat/") ||
    branch.startsWith("bug/");

module.exports = {
    tagFormat: "${version}",
    branches: [
        "main",
        {name: "beta", prerelease: true},
        {name: "alpha", prerelease: true},
        {name: "dev", prerelease: true},
        {name: "feat/**", channel: "preview", prerelease: "preview-${name.replace(/^feat\\//, '').replace(/\\//g, '-')}"},
        {name: "bug/**", channel: "preview", prerelease: "preview-${name.replace(/^bug\\//, '').replace(/\\//g, '-')}"},
    ],
    plugins: isPreview
        ? [
            "semantic-release-gitmoji",
            ["@semantic-release/changelog", {
                changelogFile: "CHANGELOG.md",
            }],
            ["@semantic-release/exec", {
                prepareCmd: "./gradlew setVersion -PnewVersion=${nextRelease.version}",
            }],
            ["@semantic-release/github", {
                releasedLabels: ["released on @preview"],
            }],
        ]
        : [
            "semantic-release-gitmoji",
            ["@semantic-release/changelog", {
                changelogFile: "CHANGELOG.md",
            }],
            ["@semantic-release/exec", {
                prepareCmd: "./gradlew setVersion -PnewVersion=${nextRelease.version}",
            }],
            "@semantic-release/github",
            ["@semantic-release/git", {
                assets: ["CHANGELOG.md", "gradle.properties"],
                message: "🔖 Update `gradle.properties` to `${nextRelease.version}` [skip release]\n\n${nextRelease.notes}",
            }],
        ],
};