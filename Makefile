PKG := Data_structures
SRCS := $(wildcard $(PKG)/*.java)
LOG := test-results.log

.PHONY: build run run-all clean list

# Compile everything
build:
	javac $(SRCS)

# Run a specific Main class, then clean up: `make run F=Stack`
run: build
	@if [ -z "$(F)" ]; then \
		echo "usage: make run F=<filename without .java>"; \
		echo "       e.g. make run F=Stack  (runs $(PKG).Stack_Main)"; \
		exit 1; \
	fi
	@java $(PKG).$(F)_Main; \
	status=$$?; \
	$(MAKE) --no-print-directory clean; \
	exit $$status

# Run all Main classes, log everything to $(LOG), summarize failures.
run-all: build
	@rm -f $(LOG)
	@failed_classes=""; \
	for src in $(SRCS); do \
		base=$$(basename $$src .java); \
		echo "=================================================" >> $(LOG); \
		echo "RUN: $$base" >> $(LOG); \
		echo "=================================================" >> $(LOG); \
		if java $(PKG).$${base}_Main >> $(LOG) 2>&1; then \
			:; \
		else \
			failed_classes="$$failed_classes $$base"; \
		fi; \
		echo "" >> $(LOG); \
	done; \
	$(MAKE) --no-print-directory clean; \
	echo ""; \
	echo "Full output written to $(LOG)"; \
	if [ -z "$$failed_classes" ]; then \
		echo "All classes passed."; \
	else \
		echo "FAILED classes:"; \
		for c in $$failed_classes; do echo "  - $$c"; done; \
		exit 1; \
	fi

# Remove all compiled .class files
clean:
	rm -f $(PKG)/*.class

# List available Main classes
list:
	@ls $(PKG)/*.java | sed 's|$(PKG)/||;s|\.java||' | awk '{print "  make run F="$$1}'