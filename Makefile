# Root Makefile — builds and runs the Data_structures and Algorithms packages.
#
#   make run F=Stack              -> runs Data_structures.Stack_Main
#   make run F=Unique_substrings  -> runs Algorithms.Unique_substrings_Main
#   make run-all                  -> runs every *_Main, logs to test-results.log,
#                                    prints only failing classes
#   make clean                    -> delete all .class files
#
# Source lives in package-named folders at the repo root:
#   Data_structures/*.java   (package Data_structures;)
#   Algorithms/*.java        (package Algorithms;)

DS_DIR  := Data_structures
ALG_DIR := Algorithms
SRCS    := $(wildcard $(DS_DIR)/*.java) $(wildcard $(ALG_DIR)/*.java)

.PHONY: run run-all clean

# Build everything, run <F>_Main from whichever package contains <F>.java, then clean.
run:
	@if [ -z "$(F)" ]; then echo "usage: make run F=ClassName"; exit 1; fi
	@javac -d . $(SRCS)
	@FQN=""; \
	if [ -f "$(DS_DIR)/$(F).java" ]; then FQN="$(DS_DIR).$(F)_Main"; \
	elif [ -f "$(ALG_DIR)/$(F).java" ]; then FQN="$(ALG_DIR).$(F)_Main"; \
	else echo "class '$(F)' not found in $(DS_DIR)/ or $(ALG_DIR)/"; $(MAKE) -s clean; exit 1; fi; \
	echo "running $$FQN"; echo; \
	java $$FQN; status=$$?; \
	$(MAKE) -s clean; \
	exit $$status

# Build everything, run every *_Main, log full output, print only failures.
run-all:
	@javac -d . $(SRCS)
	@: > test-results.log
	@fail=0; \
	for f in $(SRCS); do \
	  dir=$$(dirname $$f); pkg=$$(basename $$dir); base=$$(basename $$f .java); \
	  if [ -f "$$dir/$${base}_Main.class" ]; then \
	    main="$$pkg.$${base}_Main"; \
	    echo "=== $$main ===" >> test-results.log; \
	    if java $$main >> test-results.log 2>&1; then \
	      echo "" >> test-results.log; \
	    else \
	      echo "FAILED: $$main"; fail=1; \
	      echo "" >> test-results.log; \
	    fi; \
	  fi; \
	done; \
	$(MAKE) -s clean; \
	if [ $$fail -eq 0 ]; then echo "all classes passed (see test-results.log)"; \
	else echo "some classes failed (see test-results.log)"; fi

clean:
	@find . -name '*.class' -delete
	rm -f test-results.log

# List available Main classes
list:
	@ls $(DS_DIR)/*.java | sed 's|$(DS_DIR)/||;s|\.java||' | awk '{print "  make run F="$$1}'
	@ls $(ALG_DIR)/*.java | sed 's|$(ALG_DIR)/||;s|\.java||' | awk '{print "  make run F="$$1}'