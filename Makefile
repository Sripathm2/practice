PKG := Data_structures
SRCS := $(wildcard $(PKG)/*.java)

.PHONY: build run clean list

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

# Remove all compiled .class files
clean:
	rm -f $(PKG)/*.class

# List available Main classes
list:
	@ls $(PKG)/*.java | sed 's|$(PKG)/||;s|\.java||' | awk '{print "  make run F="$$1}'