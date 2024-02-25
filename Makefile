FORCE: ;

# Code.

spiff: FORCE
	@./gradlew ktlintFormat ktlintCheck detekt

test: FORCE
	@./gradlew test

rebuild: FORCE
	@./gradlew clean build --warning-mode all

# Apps.

# CONFIG_FILE=../demo/config.yml make run-server
run-server: FORCE
	@./gradlew :server:run

run-browser: FORCE
	@./gradlew :browser:browserDevelopmentRun --continuous

# Util.

yarn: FORCE
	@./gradlew kotlinUpgradeYarnLock
