-include ./settings.env
export

FORCE: ;

psql: FORCE
	@docker exec -it ${DB_CONTAINER_NAME} psql -U "${POSTGRES_USER}" "${POSTGRES_DB}"

stop: FORCE
	@docker rm -f "${DB_CONTAINER_NAME}"
	@docker rm -f "${FLYWAY_CONTAINER_NAME}"

run: stop
	@docker-compose -f "./demo.yml" up -d
