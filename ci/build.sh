#!/usr/bin/env bash
# MGTeam-JE 云端构建（GitHub Actions / Linux）
# 公开依赖用 Maven 拉取；服务器私有 GMZC 插件用 ci/stubs 的 ABI 桩对齐签名（不打包进 jar）。
set -euo pipefail

# javac/java classpath separator: Linux uses ':', Git Bash on Windows uses ';'
CP_SEP=':'
case "$(uname -s)" in
  MINGW*|MSYS*|CYGWIN*) CP_SEP=';' ;;
esac
cd "$(dirname "$0")/.."

MVN="${MVN:-mvn}"
rm -rf build/ci-lib build/stub-classes build/ci-stubs.jar build/plugin-classes build/MGTeam-1.0.0.jar
mkdir -p build/ci-lib build/stub-classes build/plugin-classes

echo "== 1/4 拉取公开依赖 =="
"$MVN" -B -q -f ci/deps-pom.xml dependency:copy-dependencies -DoutputDirectory="$PWD/build/ci-lib"

echo "== 2/4 编译 ABI 桩 =="
find ci/stubs -name '*.java' | sort > build/stub-sources.txt
javac -encoding UTF-8 -proc:none -cp "build/ci-lib/*" -d build/stub-classes @build/stub-sources.txt
jar --create --file build/ci-stubs.jar -C build/stub-classes .

echo "== 3/4 编译插件（与本地 build.ps1 一致：排除 FundConsumeManager.java）=="
find src -name '*.java' ! -name 'FundConsumeManager.java' | sort > build/sources.txt
javac -encoding UTF-8 -proc:none -cp "build/ci-lib/*${CP_SEP}build/ci-stubs.jar" -d build/plugin-classes @build/sources.txt

echo "== 4/4 打包 =="
cp plugin.yml config.yml build/plugin-classes/
jar --create --file build/MGTeam-1.0.0.jar -C build/plugin-classes .
echo "Built build/MGTeam-1.0.0.jar"
