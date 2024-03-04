FORCE: ;

# Code.

spiff: FORCE
	@./gradlew ktlintFormat ktlintCheck detekt

test: FORCE
	@./gradlew test

clean: FORCE
	@./gradlew clean

rebuild: clean
	@./gradlew --warning-mode all build

# Apps.

# CONFIG_FILE=../demo/config.yml make run-server
run-server: FORCE
	@./gradlew :app:server:run

run-browser: FORCE
	@./gradlew :app:browser:browserDevelopmentRun --continuous

# Util.

deps: FORCE
	@./gradlew dependencyUpdates

# After changing deps in the browser project.
yarn: FORCE
	@./gradlew kotlinUpgradeYarnLock
