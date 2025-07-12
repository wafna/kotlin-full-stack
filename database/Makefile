-include ./settings.env
export

.FORCE: ;

run: .FORCE
	@echo ">>> Running database container."
	@docker compose up -d --remove-orphans

stop: .FORCE
	@echo ">>> Stopping database container."
	@-docker stop "${DB_CONTAINER_NAME}" 2> /dev/null

clean: .FORCE
	@echo ">>> Cleaning demo containers."
	@-docker rm -f "${DB_CONTAINER_NAME}" 2> /dev/null
	@-docker rm -f "${FLYWAY_CONTAINER_NAME}" 2> /dev/null

# Targets the local `fullstack_db` container.
psql.local: .FORCE
	@docker exec -it ${DB_CONTAINER_NAME} psql -U "${POSTGRES_USER}" "${POSTGRES_DB}"

# Targets the local `fullstack_db` container.
log.local: .FORCE
	@docker logs fullstack_db

# Targets the test container.
psql.test: .FORCE
	@docker exec -it "$(shell docker ps | grep 'postgres:' | cut -d' ' -f1)" psql -U username test
