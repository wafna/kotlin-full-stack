-include test.env
export

FORCE: ;

CONTAINER_ID := $(shell docker ps -a | grep "${CONTAINER_NAME}" | cut -d' ' -f1)

env: FORCE
	@echo ${APP_NAME}
	@echo ${IMAGE_NAME}
	@echo ${CONTAINER_NAME}
	@echo ${NETWORK_NAME}
	@echo ${POSTGRES_DB}
	@echo ${POSTGRES_USER}
	@echo ${POSTGRES_PASSWORD}


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
	@([ -z "${CONTAINER_ID}" ] && echo "Container not found.") || \
		docker kill "${CONTAINER_ID}"

ps: FORCE
	@docker ps -a

rm: FORCE
	@([ -z "${CONTAINER_ID}" ] && echo "Container not found.") || \
		docker rm "${CONTAINER_ID}"

exec: FORCE
	@([ -z "${CONTAINER_ID}" ] && echo "Container not found.") || \
		docker run -it "${IMAGE_NAME}" psql -h localhost -U "${POSTGRES_USER}" "${POSTGRES_DB}"

psql: FORCE
	@([ -z "${CONTAINER_ID}" ] && echo "Container not found.") || \
		psql -h localhost -U "${POSTGRES_USER}" "${POSTGRES_DB}"

log: FORCE
	@([ -z "${CONTAINER_ID}" ] && echo "Container not found.") || \
		docker logs "${CONTAINER_ID}"