-include ./settings.env
export

FORCE: ;

psql: FORCE
	@docker exec -it ${DB_CONTAINER_NAME} psql -U "${POSTGRES_USER}" "${POSTGRES_DB}"

stop: FORCE
	@docker rm -f "${DB_CONTAINER_NAME}"
	@docker rm -f "${FLYWAY_CONTAINER_NAME}"
	@docker rm -f "${SERVER_CONTAINER_NAME}"

run: stop
	@docker-compose -f "./demo.yml" up -d

server-image: FORCE
	@echo Unzipping demo to build. && \
		unzip -q ../app/server/build/distributions/kotlin-full-stack-demo.zip -d ./build/ && \
		docker image build -t server-demo:latest .
	@rm -rf ./build

server-run: FORCE
	@docker rm -f server-demo
	@docker run -d --name server-demo server-demo:latest