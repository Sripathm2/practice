# Root Makefile — builds and runs the Data_structures, Algorithms, and Problems packages.
#
#   make run F=Stack              -> runs Data_structures.Stack_Main
#   make run F=Merge_sort         -> runs Algorithms.Merge_sort_Main
#   make run F=Magical_cows       -> runs Problems.Magical_cows_Main
#   make run-latest               -> runs the most recently modified file
#   make run-all                  -> runs every *_Main, logs to test-results.log,
#                                    prints only failing classes
#   make list                     -> list all runnable classes, newest first
#   make clean                    -> delete all .class files
#
# Source lives in package-named folders at the repo root:
#   Data_structures/*.java   (package Data_structures;)
#   Algorithms/*.java        (package Algorithms;)
#   Problems/*.java          (package Problems;)

DS_DIR   := Data_structures
ALG_DIR  := Algorithms
PROB_DIR := Problems
DIRS     := $(DS_DIR) $(ALG_DIR) $(PROB_DIR)
SRCS     := $(foreach d,$(DIRS),$(wildcard $(d)/*.java))

.PHONY: run run-latest run-all list clean

# Build everything, run <F>_Main from whichever package contains <F>.java, then clean.
run:
	@if [ -z "$(F)" ]; then echo "usage: make run F=ClassName"; exit 1; fi
	@javac -d . $(SRCS)
	@FQN=""; \
	for d in $(DIRS); do \
	  if [ -f "$$d/$(F).java" ]; then FQN="$$d.$(F)_Main"; break; fi; \
	done; \
	if [ -z "$$FQN" ]; then echo "class '$(F)' not found in: $(DIRS)"; $(MAKE) -s clean; exit 1; fi; \
	echo "running $$FQN"; echo; \
	java $$FQN; status=$$?; \
	$(MAKE) -s clean; \
	exit $$status

# Run the most recently modified source file's *_Main.
run-latest:
	@latest=$$(ls -t $(foreach d,$(DIRS),$(d)/*.java) | head -1); \
	F=$$(basename $$latest .java); \
	echo "latest: $$F"; \
	$(MAKE) run F=$$F

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

# List runnable classes, newest first, across all packages.
list:
	@ls -t $(foreach d,$(DIRS),$(d)/*.java) | sed 's|.*/||;s|\.java||' | awk '{print "  make run F="$$1}'

clean:
	@find . -name '*.class' -delete