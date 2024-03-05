FORCE: ;

BASE_URL := http://localhost:8686/api

overview: FORCE
	@curl -v "${BASE_URL}/overview" | jq

schema: FORCE
	@curl -v "${BASE_URL}/schema/${SCHEMA}" | jq

table-detail: FORCE
	@curl -v "${BASE_URL}/table/${SCHEMA}/${TABLE}" | jq
