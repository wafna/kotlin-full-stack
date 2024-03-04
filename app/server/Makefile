FORCE: ;

BASE_URL := http://localhost:8686/api

schemas: FORCE
	@curl -v "${BASE_URL}/schemas" | jq

tables: FORCE
	@curl -v "${BASE_URL}/tables/${SCHEMA}" | jq

table-detail: FORCE
	@curl -v "${BASE_URL}/tables/${SCHEMA}/${TABLE}" | jq
