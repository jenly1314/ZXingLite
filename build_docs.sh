#!/bin/bash

set -ex

# Generate the API docs
./gradlew dokkaHtml

mkdir -p docs/api
mv zxing-lite/build/dokka/html/* docs/api

# Copy in special files that GitHub wants in the project root.
sed '/<!-- end -->/q' README.md > docs/index.md
cat CHANGELOG.md | grep -v '## 版本日志' > docs/changelog.md
cp GIF.gif docs/

# Build the site locally
mkdocs build
