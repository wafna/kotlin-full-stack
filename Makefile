FORCE: ;

test: FORCE
	@./gradlew test

build: spiff
	@./gradlew clean build --warning-mode all

spiff: FORCE
	@./gradlew ktlintFormat ktlintCheck detekt

yarn: FORCE
	@./gradlew kotlinUpgradeYarnLock

# Apps.

server: FORCE
	@./gradlew :server:run

browser: FORCE
	@./gradlew :browser:browserDevelopmentRun --continuous
