.FORCE: ;

# Code.

# This will force a full rebuild and all tests will run.
full: .FORCE
	@./gradlew --warning-mode all --rerun-tasks build

test: .FORCE
	@./gradlew --warning-mode all test jsBrowserTest

check: .FORCE
	@./gradlew --warning-mode all check

format: .FORCE
	@./gradlew --warning-mode all ktfmtFormat

clean: .FORCE
	@./gradlew clean

build: .FORCE
	@./gradlew --warning-mode all build

dokka: .FORCE
	@./gradlew dokkaGenerate

docs: .FORCE
	@./gradlew dokkaHtmlCollector

# Util.

# Check for out of date libraries.
deps: .FORCE
	@./gradlew dependencyUpdates

# After changing deps in the browser project.
yarn: .FORCE
	@./gradlew kotlinUpgradeYarnLock

# Unfortunately, Mac allow multiple listeners on a port.
ports: .FORCE
	@lsof -nP -iTCP -sTCP:LISTEN
