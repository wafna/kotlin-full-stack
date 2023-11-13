FORCE: ;

test: FORCE
	@./gradlew test

server: FORCE
	@./gradlew :server:run

browser: FORCE
	@./gradlew :browser:browserDevelopmentRun --continuous

build: FORCE
	@./gradlew formatKotlin clean build --warning-mode all

spiff: FORCE
	@./gradlew formatKotlin lintKotlin detekt

yarn: FORCE
	@./gradlew kotlinUpgradeYarnLock

# API.

BASE_URL := http://0.0.0.0:8686/api

schemas: FORCE
	@curl "${BASE_URL}/schemas" | jq

tables: FORCE
	@curl "${BASE_URL}/tables/${SCHEMA}" | jq
