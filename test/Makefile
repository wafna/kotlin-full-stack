-include test.env
export

FORCE: ;

images: FORCE
	@docker images

prune: FORCE
	@docker system prune -a

pull: FORCE
	@docker pull "${IMAGE_NAME}"

start: FORCE
	@docker run --name "${CONTAINER_NAME}" \
		-e POSTGRES_DB="${POSTGRES_DB}" \
		-e POSTGRES_USER="${POSTGRES_USER}" \
		-e POSTGRES_PASSWORD="${POSTGRES_PASSWORD}" \
		-d "${IMAGE_NAME}"

stop: FORCE
	@docker kill "${CONTAINER_NAME}"

rm: FORCE
	@docker rm "${CONTAINER_NAME}"

# Remove all exited containers.
clean: FORCE
	@for DUD in $$(docker ps -a | grep Exited | cut -d" " -f1); do \
		docker rm $${DUD}; \
	done

psql: FORCE
	@docker exec -it ${CONTAINER_NAME} psql -U "${POSTGRES_USER}" "${POSTGRES_DB}"

log: FORCE
	@docker logs "${CONTAINER_NAME}"

demo: stop rm clean
	@docker-compose -f "./demo.yml" up -d