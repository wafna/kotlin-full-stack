FORCE: ;

# Code.

test: FORCE
	@./gradlew test

clean: FORCE
	@./gradlew clean

build: FORCE
	@./gradlew --warning-mode all build

rebuild: clean build

# Apps.

# CONFIG_FILE=../demo/config.yml make run-server
run-server: FORCE
	@./gradlew :app:server:run

run-browser: FORCE
	@./gradlew :app:browser:browserDevelopmentRun --continuous

# Util.

docs: FORCE
	@./gradlew dokkaHtmlCollector

deps: FORCE
	@./gradlew dependencyUpdates

# After changing deps in the browser project.
yarn: FORCE
	@./gradlew kotlinUpgradeYarnLock
