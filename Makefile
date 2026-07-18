# Study Lab — root Makefile.
# Builds/runs the Java packages (Data_structures, Algorithms, Problems) and the
# Jupyter notebooks under ml/.
#
#   make run F=Stack              -> runs Data_structures.Stack_Main
#   make run F=Merge_sort         -> runs Algorithms.Merge_sort_Main
#   make run F=Magical_cows      -> runs Problems.Magical_cows_Main
#   make run-nb N=<name|path>     -> executes a notebook top-to-bottom, in place
#                                    (N=islp_lab_ch03 finds ml/**/islp_lab_ch03.ipynb)
#   make run-latest               -> runs the most recently modified source file:
#                                    .java -> its *_Main, .ipynb -> execute the
#                                    notebook, ml .py -> run it in the ml env
#   make run-all                  -> runs every *_Main, logs to test-results.log,
#                                    prints only failing classes
#   make list                     -> list runnable classes + notebooks, newest first
#   make clean                    -> delete .class files, .ipynb_checkpoints, and
#                                    GENERATED artifacts under ml/ (anything not a
#                                    protected type, outside ml/data/ — see below)
#   make clean-all                -> clean + strip all output cells from every
#                                    notebook under ml/ (commit-ready notebooks)
#
# clean's protected types under ml/ (never deleted): .ipynb .py .yml .yaml .md
# and everything inside ml/data/ and hidden files. Everything else under ml/
# (pngs, pdfs, html exports, csv dumps outside data/, ...) is treated as
# generated output and removed. Add extensions to ML_KEEP to protect more.
#
# Notebook execution/stripping uses the `ml` conda env when conda is available
# (conda run -n ml), otherwise whatever `jupyter` is on PATH.

DS_DIR   := Data_structures
ALG_DIR  := Algorithms
PROB_DIR := Problems
DIRS     := $(DS_DIR) $(ALG_DIR) $(PROB_DIR)
SRCS     := $(foreach d,$(DIRS),$(wildcard $(d)/*.java))

ML_DIR   := ml
ML_KEEP  := ipynb py yml yaml md
NBS       = $(shell find $(ML_DIR) -name '*.ipynb' -not -path '*/.ipynb_checkpoints/*' 2>/dev/null)

# Run jupyter inside the ml conda env when conda exists; fall back to PATH.
JUPYTER  := $(shell command -v conda >/dev/null 2>&1 && echo "conda run -n ml jupyter" || echo "jupyter")
MLPY     := $(shell command -v conda >/dev/null 2>&1 && echo "conda run -n ml python" || echo "python3")

.PHONY: run run-nb run-latest run-all list clean clean-all

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

# Execute a notebook top-to-bottom, saving outputs in place.
# N can be a path (ml/trees/islp_lab_ch08.ipynb) or a bare name (islp_lab_ch08).
run-nb:
	@if [ -z "$(N)" ]; then echo "usage: make run-nb N=<notebook name or path>"; exit 1; fi
	@nb="$(N)"; \
	if [ ! -f "$$nb" ]; then \
	  nb=$$(find $(ML_DIR) -name "$(N).ipynb" -not -path '*/.ipynb_checkpoints/*' | head -1); \
	fi; \
	if [ -z "$$nb" ] || [ ! -f "$$nb" ]; then echo "notebook '$(N)' not found under $(ML_DIR)/"; exit 1; fi; \
	echo "executing $$nb"; \
	$(JUPYTER) nbconvert --to notebook --execute --inplace "$$nb"

# Run the most recently modified source file across the whole lab:
#   .java under the packages -> its *_Main; .ipynb under ml/ -> execute it;
#   .py under ml/ -> run it in the ml env.
run-latest:
	@latest=$$(ls -t $(foreach d,$(DIRS),$(d)/*.java) $$(find $(ML_DIR) \( -name '*.ipynb' -o -name '*.py' \) -not -path '*/.ipynb_checkpoints/*' 2>/dev/null) 2>/dev/null | head -1); \
	if [ -z "$$latest" ]; then echo "no runnable sources found"; exit 1; fi; \
	echo "latest: $$latest"; \
	case "$$latest" in \
	  *.ipynb) $(MAKE) run-nb N="$$latest" ;; \
	  *.py)    echo; $(MLPY) "$$latest" ;; \
	  *.java)  $(MAKE) run F=$$(basename $$latest .java) ;; \
	esac

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

# List runnable classes and notebooks, newest first.
list:
	@ls -t $(foreach d,$(DIRS),$(d)/*.java) | sed 's|.*/||;s|\.java||' | awk '{print "  make run F="$$1}'
	@find $(ML_DIR) -name '*.ipynb' -not -path '*/.ipynb_checkpoints/*' 2>/dev/null | \
	  xargs -I{} echo "  make run-nb N={}"

# clean: .class files everywhere; under ml/: checkpoint dirs and any GENERATED
# artifact — a non-hidden file whose extension is not in ML_KEEP, outside ml/data/.
clean:
	@find . -name '*.class' -delete
	@if [ -d $(ML_DIR) ]; then \
	  find $(ML_DIR) -type d -name '.ipynb_checkpoints' -exec rm -rf {} + 2>/dev/null; \
	  find $(ML_DIR) -type f \
	    -not -path '$(ML_DIR)/data/*' \
	    -not -name '.*' \
	    $(foreach e,$(ML_KEEP),-not -name '*.$(e)') \
	    -print -delete | sed 's/^/  cleaned: /'; \
	fi

# clean-all: clean + strip every notebook's output cells (small, diff-friendly commits).
clean-all: clean
	@if [ -n "$(NBS)" ]; then \
	  echo "stripping notebook outputs:"; \
	  for nb in $(NBS); do echo "  $$nb"; done; \
	  $(JUPYTER) nbconvert --clear-output --inplace $(NBS); \
	else echo "no notebooks to strip"; fi