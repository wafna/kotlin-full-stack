-include ./demo.env
export

FORCE: ;

psql: FORCE
	@docker exec -it ${DB_CONTAINER_NAME} psql -U "${POSTGRES_USER}" "${POSTGRES_DB}"

run: FORCE
	@./bin/destroy-container.sh "${DB_CONTAINER_NAME}"
	@./bin/destroy-container.sh "${FLYWAY_CONTAINER_NAME}"
	@docker-compose -f "./demo.yml" up -d
