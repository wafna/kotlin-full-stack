-include ./demo.env
export

FORCE: ;

pull: FORCE
	@docker pull "${IMAGE_NAME}"

start: FORCE
	@docker run --name "${CONTAINER_NAME}" \
		-e POSTGRES_DB="${POSTGRES_DB}" \
		-e POSTGRES_USER="${POSTGRES_USER}" \
		-e POSTGRES_PASSWORD="${POSTGRES_PASSWORD}" \
		-d "${IMAGE_NAME}"

psql: FORCE
	@docker exec -it ${CONTAINER_NAME} psql -U "${POSTGRES_USER}" "${POSTGRES_DB}"

demo: FORCE
	@echo -n "Stopping ${CONTAINER_NAME}"
	@docker kill "${CONTAINER_NAME}"
	@echo -n "Removing ${CONTAINER_NAME}"
	@docker rm "${CONTAINER_NAME}"
	@docker-compose -f "./demo.yml" up -d