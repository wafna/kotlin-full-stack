.FORCE: ;

build: .FORCE
	@$(MAKE) -C app build

test: .FORCE
	@$(MAKE) -C app test

full: .FORCE
	@$(MAKE) -C app full

check: .FORCE
	@$(MAKE) -C app check

format: .FORCE
	@$(MAKE) -C app format

deps: .FORCE
	@$(MAKE) -C app deps

db: .FORCE
	@$(MAKE) -C database clean run

dokka: .FORCE
	@$(MAKE) -C app dokka
