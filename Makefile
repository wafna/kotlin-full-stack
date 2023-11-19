FORCE: ;

test: FORCE
	@./gradlew test

build: FORCE
	@./gradlew formatKotlin clean build --warning-mode all

spiff: FORCE
	@./gradlew formatKotlin lintKotlin detekt

yarn: FORCE
	@./gradlew kotlinUpgradeYarnLock

# Apps.

server: FORCE
	@./gradlew :server:run

browser: FORCE
	@./gradlew :browser:browserDevelopmentRun --continuous
