# ML Notes

A single running file of statistical-learning and ML concepts, in Definition / Intuition / Notes form. Written to stand alone — no book required to read them. Math is kept light: a one-line formula plus plain English, not derivations.

---

## Contents

**Statistical Learning — Foundations** *(ISL ch. 2)*
- [Statistical learning](#statistical-learning)
- [Supervised vs unsupervised learning](#supervised-vs-unsupervised-learning)
- [Semi-supervised learning](#semi-supervised-learning)
- [Prediction vs inference](#prediction-vs-inference)
- [Reducible vs irreducible error](#reducible-vs-irreducible-error)
- [Quantitative vs qualitative variables](#quantitative-vs-qualitative-variables)
- [Regression vs classification](#regression-vs-classification)

**Estimating f** *(ISL ch. 2)*
- [Parametric methods](#parametric-methods)
- [Non-parametric methods](#non-parametric-methods)
- [Overfitting](#overfitting)
- [Flexibility vs interpretability](#flexibility-vs-interpretability)

**Assessing Model Accuracy** *(ISL ch. 2)*
- [Mean squared error (MSE)](#mean-squared-error-mse)
- [Training MSE vs test MSE](#training-mse-vs-test-mse)
- [Bias–variance trade-off](#biasvariance-trade-off)
- [Bias](#bias)
- [Variance](#variance)
- [Classification error rate](#classification-error-rate)
- [Bayes classifier](#bayes-classifier)
- [Bayes decision boundary](#bayes-decision-boundary)
- [Bayes error rate](#bayes-error-rate)
- [K-nearest neighbors (KNN)](#k-nearest-neighbors-knn)

**Linear Regression** *(ISL ch. 3)*
- [Simple linear regression](#simple-linear-regression)
- [Least squares and residuals (RSS)](#least-squares-and-residuals-rss)
- [Population regression line vs least squares line](#population-regression-line-vs-least-squares-line)
- [Standard error of a coefficient](#standard-error-of-a-coefficient)
- [Confidence interval](#confidence-interval)
- [Hypothesis test, t-statistic, and p-value](#hypothesis-test-t-statistic-and-p-value)
- [Residual standard error (RSE)](#residual-standard-error-rse)
- [R-squared and correlation](#r-squared-and-correlation)
- [Multiple linear regression](#multiple-linear-regression)
- [F-statistic](#f-statistic)
- [Qualitative predictors and dummy variables](#qualitative-predictors-and-dummy-variables)
- [Additivity and linearity assumptions](#additivity-and-linearity-assumptions)
- [Interaction terms](#interaction-terms)
- [Potential problems in linear regression](#potential-problems-in-linear-regression)
- [KNN regression](#knn-regression)

**Classification** *(ISL ch. 4)*
- [Why not linear regression for classification](#why-not-linear-regression-for-classification)
- [Logistic regression](#logistic-regression)
- [Odds and log-odds (logit)](#odds-and-log-odds-logit)
- [Maximum likelihood](#maximum-likelihood)
- [Multinomial logistic regression](#multinomial-logistic-regression)
- [Confounding](#confounding)
- [Generative classifiers and Bayes' theorem](#generative-classifiers-and-bayes-theorem)
- [Linear discriminant analysis (LDA)](#linear-discriminant-analysis-lda)
- [Quadratic discriminant analysis (QDA)](#quadratic-discriminant-analysis-qda)
- [Naive Bayes](#naive-bayes)
- [Confusion matrix and error types](#confusion-matrix-and-error-types)
- [ROC curve](#roc-curve)
- [Poisson regression](#poisson-regression)
- [Generalized linear models (GLMs)](#generalized-linear-models-glms)

**Resampling Methods** *(ISL ch. 5)*
- [Resampling methods](#resampling-methods)
- [Model assessment vs model selection](#model-assessment-vs-model-selection)
- [Validation set approach](#validation-set-approach)
- [Leave-one-out cross-validation (LOOCV)](#leave-one-out-cross-validation-loocv)
- [k-fold cross-validation](#k-fold-cross-validation)
- [The bootstrap](#the-bootstrap)

**Linear Model Selection and Regularization** *(ISL ch. 6)*
- [Why move beyond least squares](#why-move-beyond-least-squares)
- [Best subset selection](#best-subset-selection)
- [Forward and backward stepwise selection](#forward-and-backward-stepwise-selection)
- [Cp, AIC, BIC, and adjusted R-squared](#cp-aic-bic-and-adjusted-r-squared)
- [One-standard-error rule](#one-standard-error-rule)
- [Ridge regression](#ridge-regression)
- [The lasso](#the-lasso)
- [Ridge vs lasso](#ridge-vs-lasso)
- [Principal components regression (PCR)](#principal-components-regression-pcr)
- [Partial least squares (PLS)](#partial-least-squares-pls)
- [High-dimensional data](#high-dimensional-data)

**Moving Beyond Linearity** *(ISL ch. 7)*
- [Polynomial regression](#polynomial-regression)
- [Step functions](#step-functions)
- [Basis functions](#basis-functions)
- [Regression splines](#regression-splines)
- [Natural splines](#natural-splines)
- [Smoothing splines](#smoothing-splines)
- [Local regression](#local-regression)
- [Generalized additive models (GAMs)](#generalized-additive-models-gams)

**Tree-Based Methods** *(ISL ch. 8)*
- [Decision trees](#decision-trees)
- [Regression trees](#regression-trees)
- [Tree pruning (cost-complexity)](#tree-pruning-cost-complexity)
- [Classification trees](#classification-trees)
- [Trees: strengths and weaknesses](#trees-strengths-and-weaknesses)
- [Ensemble methods and weak learners](#ensemble-methods-and-weak-learners)
- [Bagging](#bagging)
- [Out-of-bag (OOB) error](#out-of-bag-oob-error)
- [Random forests](#random-forests)
- [Boosting](#boosting)
- [Bayesian additive regression trees (BART)](#bayesian-additive-regression-trees-bart)

**Support Vector Machines** *(ISL ch. 9)*
- [Hyperplane](#hyperplane)
- [Maximal margin classifier](#maximal-margin-classifier)
- [Support vector classifier (soft margin)](#support-vector-classifier-soft-margin)
- [Support vector machine (kernels)](#support-vector-machine-kernels)
- [SVMs with more than two classes](#svms-with-more-than-two-classes)

**[Glossary](#glossary)** — alphabetical index

---

## Statistical Learning — Foundations *(ISL ch. 2)*

### Statistical learning

**Definition.** A set of approaches for estimating an unknown function `f` that links inputs `X = (X_1, …, X_p)` to an output `Y`, modeled as `Y = f(X) + ε`, where `ε` is a random error term independent of `X` with mean zero.

**Intuition.** We assume some systematic relationship `f` connects inputs to output, but it's buried in noise. Statistical learning is the toolkit for recovering as much of `f` as the data allows — either to predict new outputs, or to understand how the inputs drive the output.

**Notes.** `ε` is the *irreducible error* (→ Reducible vs irreducible error). If every observation pairs inputs with a known `Y`, the problem is *supervised*; with no `Y`, it's *unsupervised*. Everything downstream — parametric vs non-parametric, the bias–variance trade-off — is about how well we can pin down `f̂`.

### Supervised vs unsupervised learning

**Definition.** Supervised learning fits a model from observations that each pair predictors `x_i` with a response `y_i`, for prediction or inference. Unsupervised learning has predictors `x_i` but no response `y_i`, so the goal is to find structure among observations rather than predict a labeled output.

**Intuition.** Supervised = learning with an answer key: every example tells you the right output, so you can measure and correct error. Unsupervised = working blind: no answer key, so you look for patterns, groupings, or structure in the inputs themselves.

**Notes.** Supervised methods in this file: linear regression, logistic regression, LDA/QDA, trees, GAMs, boosting, SVMs. Unsupervised can't fit a regression model — there's no response to supervise it. Almost all model-accuracy machinery (MSE, error rate, bias–variance) presumes a response to compare against. See Semi-supervised learning for the in-between case.

### Semi-supervised learning

**Definition.** A setting with `n` observations where `m < n` have both predictors and a response, and the remaining `n − m` have predictors only; the aim is a method that uses both.

**Intuition.** Common when predictors are cheap to measure but responses are expensive to collect. You don't want to discard the unlabeled majority, so you use them to sharpen an estimate anchored by the labeled few.

**Notes.** Sits between supervised and unsupervised. *(beyond ISL ch. 2 — named but not developed there.)*

### Prediction vs inference

**Definition.** In prediction, the goal is an accurate `f̂` giving good output estimates `Ŷ = f̂(X)` for new inputs, and `f̂` may be a black box. In inference, the goal is to understand the relationship between `X` and `Y` — which predictors matter, in what direction, how — so `f̂` must be interpretable.

**Intuition.** Prediction only cares that the answer is right; you needn't know why. Inference cares about the "why," and will trade some predictive accuracy for a model you can read.

**Notes.** This goal drives model choice: prediction can justify flexible black boxes; inference favors restrictive, interpretable models like linear regression. → Flexibility vs interpretability.

### Reducible vs irreducible error

**Definition.** For `Ŷ = f̂(X)`, expected squared prediction error splits as `E[(Y − Ŷ)²] = [f(X) − f̂(X)]² + Var(ε)`. The first term is *reducible error* (shrinks as `f̂` improves); `Var(ε)` is the *irreducible error*, a floor no model can remove.

**Intuition.** Some error is your model's fault (a wrong or imprecise `f̂`) — you can chip away at it with better methods and more data. The rest is baked into the problem: unmeasured variables, inherent randomness. Even the true `f` wouldn't predict perfectly.

**Notes.** *Emphasized:* you can only ever attack the reducible part. `Var(ε)` reappears as the noise floor in the Bias–variance trade-off, and it's why test error bottoms out above zero. Its classification analogue is the Bayes error rate.

### Quantitative vs qualitative variables

**Definition.** Quantitative variables take numerical values (e.g. salary, age). Qualitative (categorical) variables take values in one of `K` classes or categories (e.g. brand, disease status).

**Intuition.** Numbers you can average vs labels you can only count. The type of the *response* decides what kind of problem you have.

**Notes.** Quantitative response → regression; qualitative response → classification. → Regression vs classification.

### Regression vs classification

**Definition.** Problems with a quantitative response are *regression* problems; problems with a qualitative response are *classification* problems.

**Intuition.** Predicting "how much / how many" (a number) is regression; predicting "which class" (a label) is classification.

**Notes.** The line isn't crisp. Logistic regression is a classification method but estimates class probabilities, so it has a regression flavor. Some methods — KNN, boosting — handle either response type. Two classification methods for qualitative responses covered later: Logistic regression (models the log-odds directly) and Linear discriminant analysis (models each class's distribution, then applies Bayes' rule) — distinct methods, both detailed in the Classification section.

---

## Estimating f *(ISL ch. 2)*

### Parametric methods

**Definition.** A two-step, model-based approach to estimating `f`: (1) assume a functional form for `f` (e.g. linear, `f(X) = β_0 + β_1 X_1 + … + β_p X_p`); (2) use training data to fit/train the model — i.e. estimate its parameters (for the linear form, the `p + 1` coefficients).

**Intuition.** Rather than searching all possible functions, commit to a shape and tune its dials. Assuming linearity collapses an arbitrary `p`-dimensional function into just `p + 1` numbers to estimate — far easier, and less data-hungry.

**Notes.** The risk: if the assumed form is wrong, `f̂` stays biased no matter how much data you have. Advantages: easy to fit, few coefficients, interpretable, easy significance tests. The linear form is typically fit by least squares. Contrast Non-parametric methods.

### Non-parametric methods

**Definition.** Methods that make no explicit assumption about the functional form of `f`; they seek an `f̂` that gets as close to the data as possible without being too rough or wiggly.

**Intuition.** You don't pre-commit to a shape — you let the data draw `f`. This can capture relationships a linear model would miss.

**Notes.** Cost: with no small parameter set to pin down, you need far more observations to estimate `f` accurately. You trade freedom from a possibly-wrong assumption for data-hunger and overfitting risk. KNN is the flagship example. Contrast Parametric methods.

### Overfitting

**Definition.** When a model follows the training data's noise too closely, producing a small training error but a large test error.

**Intuition.** The model memorizes quirks of the training sample — noise that won't recur — instead of the underlying signal. Great on data it has seen, poor on data it hasn't.

**Notes.** More flexible methods are more prone to it. It's why the test-error curve is U-shaped: past a point, added flexibility buys noise-fitting, not signal. In bias–variance terms, overfitting = low bias but high variance. → Bias–variance trade-off, Training MSE vs test MSE.

### Flexibility vs interpretability

**Definition.** Methods trade off along a spectrum from inflexible/interpretable to flexible/opaque. Least squares linear regression is inflexible but highly interpretable; the lasso is similar; GAMs are more flexible while staying fairly interpretable; bagging, boosting, SVMs with non-linear kernels, and neural nets are highly flexible but hard to interpret.

**Intuition.** Flexibility = how many shapes a method can bend to. More flexibility can fit complex truths but makes the model harder to read and easier to overfit. Want to explain the `X`–`Y` relationship (inference)? Reach for a restrictive, readable model.

**Notes.** *Emphasized:* more flexible ≠ more accurate. Because of overfitting, a less flexible method often predicts better on test data. Choosing the right level of flexibility is the central practical problem, in both regression and classification. → Prediction vs inference, Bias–variance trade-off.

---

## Assessing Model Accuracy *(ISL ch. 2)*

### Mean squared error (MSE)

**Definition.** In regression, the standard measure of fit quality: `MSE = (1/n) Σ (y_i − f̂(x_i))²` — the average squared gap between observed responses and predictions.

**Intuition.** How far off predictions are on average, with big misses penalized disproportionately (squaring). Small MSE = predictions land close to the truth.

**Notes.** On the fitting data it's *training MSE*; what matters is *test MSE* on unseen data. → Training MSE vs test MSE.

### Training MSE vs test MSE

**Definition.** Training MSE is computed on the data used to fit the model; test MSE on previously unseen data. The objective is to minimize test MSE.

**Intuition.** Acing questions you studied (training) doesn't prove you'll ace the exam (test). Only fresh data measures real predictive skill.

**Notes.** *Emphasized:* no guarantee the lowest-training-MSE method has the lowest test MSE — often it's the reverse. As flexibility rises, training MSE falls *monotonically* while test MSE traces a *U-shape* (falls, then rises as overfitting sets in). A widening gap is the signature of overfitting. When no test set exists, resampling estimates test error → Cross-validation (Resampling Methods).

### Bias–variance trade-off

**Definition.** Expected test MSE at a point `x_0` decomposes as `E[(y_0 − f̂(x_0))²] = Var(f̂(x_0)) + [Bias(f̂(x_0))]² + Var(ε)`. Minimizing expected test error requires *simultaneously* low variance and low bias; as flexibility changes, the two typically move in opposite directions.

**Intuition.** Expected test error has three ingredients: how much the fit jumps around across different training sets (*variance*), how much the model's simplifying assumptions distort the truth (*bias²*), and the irreducible noise floor (`Var(ε)`). You can't zero out both bias and variance at once — tighten one and the other usually loosens — so you aim for the sweet spot that minimizes their sum. As flexibility rises, bias falls but variance rises. The U-shaped test-MSE curve *is* this trade-off made visible.

**Notes.** `Var(ε)` is the irreducible error; expected test MSE can never drop below it. Underfitting = high bias; overfitting = high variance. This trade-off touches nearly every method here. → Bias, Variance, Overfitting.

### Bias

**Definition.** The error introduced by approximating a complicated real-world relationship with a much simpler model. As methods get more flexible, bias generally decreases.

**Intuition.** Force a straight line through a curvy truth and you're systematically wrong in a way more data won't fix — that's bias. Simpler, more restrictive models carry more of it.

**Notes.** Low bias is one of the two things low test error needs; the other is low Variance. High bias = underfitting. Trades off against Variance.

### Variance

**Definition.** The amount by which `f̂` would change if estimated on a different training set. More flexible methods generally have higher variance.

**Intuition.** Refit on a fresh sample from the same population — does the fit barely move, or swing wildly? Wild swings = high variance. Flexible methods bend to each sample's noise, so they're less stable.

**Notes.** *Emphasized:* small changes in training data producing large changes in `f̂` is the hallmark of high variance. High variance = overfitting. Trades off against Bias. Bagging exists specifically to reduce variance.

### Classification error rate

**Definition.** In classification, fit quality is measured by the error rate — the fraction of misclassified observations. The *test error rate* on test observations `(x_0, y_0)` is `Ave(I(y_0 ≠ ŷ_0))`, the average of the indicator that the predicted label differs from the true one; the *training error rate* is the same on training data.

**Intuition.** The classification analogue of MSE — instead of averaging squared numeric misses, you count how often you named the wrong class.

**Notes.** `I(·)` is the indicator function (1 if true, else 0). As with MSE, training error falls with flexibility while test error is U-shaped. A good classifier has a small *test* error rate; its theoretical floor is the Bayes error rate.

### Bayes classifier

**Definition.** The classifier that assigns each observation with predictors `x_0` to the most probable class given those predictors — the class `j` maximizing `Pr(Y = j | X = x_0)`. Two-class: predict class 1 if `Pr(Y = 1 | X = x_0) > 0.5`, else class 2.

**Intuition.** If you knew the true class probabilities at every point, your best bet is always the most likely class. That's the Bayes classifier — the gold standard every real classifier tries to approximate.

**Notes.** In practice we don't know the true conditional distribution and must estimate it — KNN estimates it locally; LDA/QDA/naive Bayes estimate it via Bayes' theorem. Produces the lowest possible test error rate (→ Bayes error rate) and defines the Bayes decision boundary.

### Bayes decision boundary

**Definition.** The set of points where the Bayes classifier is indifferent between classes — two-class: where `Pr(Y = 1 | X = x_0) = 0.5`. It partitions the predictor space into per-class regions.

**Intuition.** The dividing line the perfect classifier would draw. One side, one class wins; the other side, the other.

**Notes.** Real classifiers try to approximate it. A too-flexible fit (KNN with `K = 1`) yields a jagged boundary that chases noise instead of tracking the true boundary.

### Bayes error rate

**Definition.** The lowest possible test error rate, achieved by the Bayes classifier. Overall it equals `1 − E[max_j Pr(Y = j | X)]` (expectation over `X`); at a single point `x_0` it is `1 − max_j Pr(Y = j | X = x_0)`.

**Intuition.** Even the perfect classifier is wrong sometimes, because classes overlap — at some `x`, more than one class carries real probability. The Bayes error rate is that unavoidable minimum.

**Notes.** The classification analogue of irreducible error — a floor no classifier beats.

### K-nearest neighbors (KNN)

**Definition.** A non-parametric classifier. Given `K` and a test point `x_0`, KNN finds the `K` closest training points (`N_0`), estimates each class's probability as the fraction of `N_0` in that class, and assigns `x_0` to the most probable class.

**Intuition.** Ask the `K` nearest neighbors to vote; go with the majority. It approximates the Bayes classifier by estimating the true probabilities locally from whoever's nearby.

**Notes.** *Emphasized:* the choice of `K` matters a lot, and `1/K` acts as the flexibility knob. `K = 1` is maximally flexible — jagged, low-bias/high-variance, chasing noise; large `K` is smoother, higher-bias/lower-variance. Choosing `K` well *is* the bias–variance trade-off in action. KNN also does regression → KNN regression. → Non-parametric methods, Bayes classifier.

---

## Linear Regression *(ISL ch. 3)*

### Simple linear regression

**Definition.** Predicting a quantitative response `Y` from a single predictor `X` by assuming a straight-line relationship: `Y ≈ β_0 + β_1 X`, where `β_0` is the intercept and `β_1` the slope. Fitting this is "regressing `Y` on `X`."

**Intuition.** Draw the best straight line through a scatter of points. `β_1` says how much `Y` moves per one-unit rise in `X`; `β_0` is where the line crosses when `X = 0`.

**Notes.** The simplest parametric model. Extends to many predictors → Multiple linear regression. Coefficients are estimated by least squares.

### Least squares and residuals (RSS)

**Definition.** The `i`-th *residual* is `e_i = y_i − ŷ_i`, the gap between the observed and predicted response. The *residual sum of squares* is `RSS = e_1² + … + e_n²`. The *least squares* fit chooses `β̂_0, β̂_1` to minimize RSS.

**Intuition.** Each residual is a miss; squaring makes big misses count more and keeps signs from cancelling. Least squares picks the line that makes the total squared miss as small as possible — the line that "splits the difference" best.

**Notes.** For simple regression there are closed-form solutions for `β̂_0` and `β̂_1` in terms of the sample means and the spread of `X`. RSS also drives R², the F-statistic, and tree splitting. → R-squared and correlation.

### Population regression line vs least squares line

**Definition.** The *population regression line* is the true best-fit line `Y = β_0 + β_1 X + ε` for the whole population (unobserved). The *least squares line* is the estimate `ŷ = β̂_0 + β̂_1 x` computed from one sample.

**Intuition.** Same idea as estimating a population mean from a sample mean: on one sample your line sits a little high, on another a little low, but averaged over many samples it lands on the truth. The least squares line is an *unbiased* estimate of the population line.

**Notes.** The gap between the two is why we need standard errors and confidence intervals — to say how far our sample line might be from the truth.

### Standard error of a coefficient

**Definition.** The standard error `SE(β̂)` measures how much an estimated coefficient would vary across different samples. For the sample mean, `SE(μ̂)² = σ²/n`, where `σ` is the standard deviation of each observation; analogous formulas give `SE(β̂_0)` and `SE(β̂_1)`, with `σ² = Var(ε)`.

**Intuition.** It's the typical wobble in an estimate. More data (larger `n`) and predictors spread over a wider range both shrink the wobble, giving you a more precise coefficient.

**Notes.** Standard errors feed confidence intervals and hypothesis tests. Small `SE(β̂_1)` means even a modest `β̂_1` is convincing evidence of a real relationship. → Confidence interval, Hypothesis test.

### Confidence interval

**Definition.** A range that contains the true parameter with a stated probability. The 95% CI for `β_1` is approximately `β̂_1 ± 2·SE(β̂_1)`.

**Intuition.** "We're 95% confident the truth lies in here." More precisely: if you repeated the sampling many times, about 95% of the intervals you'd build this way would contain the true value.

**Notes.** Width scales with the standard error, so more data → tighter interval. The `± 2` is an approximation (holds when observations are uncorrelated). → Standard error of a coefficient.

### Hypothesis test, t-statistic, and p-value

**Definition.** To test whether `X` is related to `Y`, test the *null hypothesis* `H_0: β_1 = 0` (no relationship) against the *alternative* `H_a: β_1 ≠ 0`. Compute the *t-statistic* `t = (β̂_1 − 0) / SE(β̂_1)`, compare it to a t-distribution, and read off the *p-value*.

**Intuition.** The t-statistic asks: how many standard errors is our estimate away from zero? Far from zero → unlikely to be a fluke. The p-value is the probability of seeing an association this strong purely by chance if there were really no relationship. Small p-value → reject the null and declare a real relationship.

**Notes.** Small `SE(β̂_1)` makes even small `β̂_1` significant; large SE demands a big `β̂_1`. In classification (logistic regression) the analogue is the z-statistic. "Significant" ≠ "large effect."

### Residual standard error (RSE)

**Definition.** An estimate of the standard deviation of the noise `ε` — roughly, the average amount the response deviates from the true regression line: `RSE = sqrt(RSS / (n − 2))` for simple regression.

**Intuition.** Even a perfect model can't predict exactly, because of `ε`. RSE says how big those inherent misses are, in the units of `Y`. Smaller RSE = tighter fit.

**Notes.** An *absolute* measure of lack of fit — but being in `Y`'s units, "good" is context-dependent, which is why R² (a unit-free proportion) is often reported alongside. → R-squared and correlation.

### R-squared and correlation

**Definition.** `R² = 1 − RSS/TSS`, where `TSS = Σ(y_i − ȳ)²` is the total sum of squares. It's the *proportion of variance in `Y` explained* by the regression, always between 0 and 1. Correlation `Cor(X, Y)` also measures the linear relationship; in *simple* linear regression, `R² = r²`.

**Intuition.** TSS is how much `Y` varies on its own; RSS is how much variation is left after the model. Their ratio is the fraction the model mopped up. R² near 1 = the model explains most of the variability; near 0 = it explains little (wrong model, high noise, or both).

**Notes.** Unit-free, unlike RSE, so easier to interpret across problems. The `R² = r²` identity only holds for a single predictor; with multiple predictors R² generalizes but correlation doesn't directly. Adding predictors never decreases R² → motivates adjusted R². → Adjusted R-squared.

### Multiple linear regression

**Definition.** Extends simple regression to `p` predictors: `Y = β_0 + β_1 X_1 + … + β_p X_p + ε`. Each `β_j` is the *average effect on `Y` of a one-unit increase in `X_j`, holding all other predictors fixed*. Coefficients are again estimated by least squares (minimizing RSS).

**Intuition.** Instead of one slope you have one per predictor, each isolating that predictor's effect while the others are held constant. That "holding fixed" is crucial — a predictor's solo effect can differ sharply from its effect alongside correlated others (→ Confounding).

**Notes.** To ask whether *any* predictor matters, use the F-statistic, not `p` individual t-tests. Which predictors to include is Variable selection (→ Best subset selection, Forward and backward stepwise selection).

### F-statistic

**Definition.** Tests whether *all* coefficients are zero: `H_0: β_1 = … = β_p = 0` vs `H_a:` at least one is non-zero. `F = [(TSS − RSS)/p] / [RSS/(n − p − 1)]`. Under `H_0`, `F` is around 1; if some predictor matters, `F > 1`.

**Intuition.** It compares variance the model explains against variance it leaves behind. If the predictors do nothing, both are just noise and the ratio hovers near 1; if they help, the numerator swells and F climbs.

**Notes.** Why not just look at individual t-tests? With many predictors, some will look significant by chance; the F-test gives one honest overall verdict. → Multiple linear regression.

### Qualitative predictors and dummy variables

**Definition.** To use a categorical predictor (e.g. student: yes/no) in a regression, encode it as a *dummy variable* (e.g. 1 if student, 0 otherwise) and include it like any numeric predictor. A `K`-level category needs `K − 1` dummies.

**Intuition.** A dummy lets the model shift the intercept for one group versus a baseline — "students carry, on average, this much more balance than non-students." Quantitative and qualitative predictors mix freely in one model.

**Notes.** Different coding schemes give different coefficients but equivalent fits; interpretation depends on the chosen baseline. Interactions between a dummy and a numeric predictor let the *slope* differ by group, not just the intercept. → Interaction terms.

### Additivity and linearity assumptions

**Definition.** Standard linear regression assumes (1) *additivity* — the effect of `X_j` on `Y` doesn't depend on the other predictors' values; and (2) *linearity* — the change in `Y` per one-unit change in `X_j` is constant regardless of `X_j`.

**Intuition.** Additivity says predictors don't team up (no "this drug only works with that one"). Linearity says the effect is a straight line, never bending. Both are convenient approximations that reality often violates.

**Notes.** Relax additivity with Interaction terms; relax linearity with Polynomial regression and the methods in Moving Beyond Linearity.

### Interaction terms

**Definition.** A product predictor `X_1 · X_2` (with coefficient `β_3`) added to a model to capture a *synergy* / *interaction effect* — where the effect of one predictor depends on another. Its presence makes the effective slope on `X_1` a function of `X_2`.

**Intuition.** Rewriting shows the `X_1` slope becomes `(β_1 + β_3 X_2)` — so changing `X_2` changes how strongly `X_1` acts on `Y`. That's exactly "these two work together." Radio ad spend might boost the payoff of TV ad spend, and vice versa.

**Notes.** *Hierarchical principle:* if you include an interaction, include both main effects too, even if their own p-values look insignificant. Interactions apply to qualitative predictors as well (a quant×qual interaction lets a group have its own slope). → Additivity and linearity assumptions.

### Potential problems in linear regression

**Definition.** Six common ways a linear fit can go wrong, each with a diagnostic:
- **Non-linearity** — the true relationship isn't a straight line. Diagnose with a *residual plot* (residuals vs fitted values); a clear pattern signals trouble. Fix with transformations or Polynomial regression.
- **Correlated error terms** — errors that track each other (common in *time series*), which makes standard errors too small and confidence falsely tight.
- **Heteroscedasticity** — non-constant error variance, often a *funnel shape* in the residual plot. Fix by transforming `Y` (e.g. `log Y` or `√Y`) or by *weighted least squares* (weight each point by the inverse of its variance).
- **Outliers** — points with an unusually large residual. Spot them with *studentized residuals* (residual ÷ its estimated SE); a rule of thumb flags values beyond about ±3.
- **High-leverage points** — points with unusual *predictor* values that yank the fitted line. Quantify with the *leverage statistic* `h_i` (always between `1/n` and 1); high leverage + large residual is especially dangerous.
- **Collinearity** — two or more predictors closely related, inflating coefficient standard errors so effects can't be separated. Spot pairwise collinearity in the correlation matrix; spot *multicollinearity* (three-plus variables jointly related, invisible pairwise) with the *variance inflation factor* (VIF). VIF ≥ 1, and a VIF above ~5–10 flags a problem. Fix by dropping one variable or combining the collinear ones.

**Intuition.** The residual plot is your first stop for most of these — a good fit leaves residuals looking like structureless static. Outliers hurt your error estimates; high-leverage points hurt the line itself; collinearity doesn't bias the fit but makes it impossible to say *which* correlated predictor is responsible.

**Notes.** An outlier may signal a data error (safe to remove) or a missing predictor (don't just delete it). VIF for `X_j` is `1/(1 − R²)` from regressing `X_j` on the other predictors — high shared variance → high VIF. Correlated errors connect to resampling caveats and, later, to time-series methods *(beyond ISL ch. 3)*.

### KNN regression

**Definition.** The regression version of KNN: for a prediction point `x_0`, find the `K` nearest training points `N_0` and predict the *average* of their responses, `f̂(x_0) = (1/K) Σ_{i ∈ N_0} y_i`.

**Intuition.** "What did the `K` most similar cases do? Average them." No functional form assumed — the fit follows the data. Small `K` = wiggly and flexible; large `K` = smooth.

**Notes.** Parametric (least squares) vs non-parametric (KNN) trade-off: KNN wins when the true `f` is far from linear; least squares wins when the assumed form is close to true, *and* when there are few observations per predictor. KNN degrades badly as `p` grows (the *curse of dimensionality* — neighbors stop being near) *(beyond ISL ch. 3)*. → K-nearest neighbors (classifier version).

---

## Classification *(ISL ch. 4)*

### Why not linear regression for classification

**Definition.** Two reasons linear regression is unsuitable for a qualitative response: (a) it can't handle a response with more than two classes (any numeric coding imposes a fake ordering and spacing); (b) even with two classes it can produce probability estimates outside `[0, 1]`, which are meaningless.

**Intuition.** Coding classes as 1, 2, 3 tells the model "class 3 is three times class 1 and sits between 2 and beyond," which is nonsense for unordered labels. And a straight line, extended far enough, predicts probabilities below 0 or above 1.

**Notes.** Motivates logistic regression (squashes output into `[0, 1]`) and the generative classifiers (LDA, QDA, naive Bayes). → Logistic regression, Generative classifiers and Bayes' theorem.

### Logistic regression

**Definition.** Models the *probability* of a class rather than the class itself, using the logistic function `p(X) = e^(β_0 + β_1 X) / (1 + e^(β_0 + β_1 X))`, which always outputs a value in `(0, 1)`. Fit by maximum likelihood.

**Intuition.** The logistic function bends any straight-line score into an S-curve pinned between 0 and 1, so you always get a sensible probability. Predict the class by thresholding (usually at 0.5). Despite the name, it's a *classification* method.

**Notes.** Coefficient significance is judged with a z-statistic (`β̂_1 / SE(β̂_1)`), the classification analogue of the t-statistic. Extends to many predictors and to more than two classes. → Odds and log-odds, Maximum likelihood, Multinomial logistic regression.

### Odds and log-odds (logit)

**Definition.** The *odds* are `p(X) / (1 − p(X))`, ranging from 0 to ∞. Taking the log gives the *log-odds* or *logit*: `log[p(X)/(1 − p(X))] = β_0 + β_1 X` — linear in `X`.

**Intuition.** Probability lives on `[0,1]`, which is awkward for a linear model. Odds stretch that to `[0, ∞)`, and the log stretches it to the full number line — so the logit is where logistic regression is actually "linear." A one-unit rise in `X` adds `β_1` to the log-odds (multiplies the odds by `e^(β_1)`).

**Notes.** Odds near 0 = very unlikely; near ∞ = near-certain. This log-odds-is-linear structure is what generalizes to multinomial logistic regression and, more broadly, GLMs.

### Maximum likelihood

**Definition.** The method used to fit logistic regression (and GLMs): choose the coefficients that make the *likelihood* — the probability of the observed data under the model — as large as possible.

**Intuition.** Find the coefficients under which the events that actually happened look most probable: push predicted probabilities toward 1 for the cases that were positive and toward 0 for the cases that were negative. Least squares is actually a special case of maximum likelihood (for Gaussian errors).

**Notes.** Preferred over ad-hoc fitting because of its good statistical properties. Also fits Poisson regression and the coefficients in GLMs generally.

### Multinomial logistic regression

**Definition.** Extends logistic regression to `K > 2` classes. Pick one class as a *baseline*, then model the log-odds of every other class against it as linear in the predictors. The *softmax* coding is an equivalent, symmetric alternative that avoids singling out a baseline.

**Intuition.** With three-plus labels you can't use a single yes/no probability. So you model each class's probability relative to a reference, and the pieces are constrained to sum to 1. Softmax does the same thing without a privileged reference class.

**Notes.** Coefficient interpretation depends on the baseline choice, so read them with care. Key model outputs (fitted probabilities, pairwise log-odds) are the same under either coding. Softmax reappears as the output layer of classification neural nets *(beyond ISL ch. 4)*.

### Confounding

**Definition.** When the result of a single-predictor regression differs from — even reverses — the result using multiple predictors, because predictors are correlated and one stands in for another.

**Intuition.** Alone, a predictor can look guilty just because it travels with the real culprit. Add the real driver and the impostor's effect shrinks or flips. The classic reminder that "correlated with `Y`" and "causes `Y` holding others fixed" are different questions.

**Notes.** Why multiple regression coefficients ("holding others fixed") can tell a very different story than simple regressions run one predictor at a time. → Multiple linear regression, Collinearity (in Potential problems).

### Generative classifiers and Bayes' theorem

**Definition.** Instead of modeling `Pr(Y | X)` directly (as logistic regression does), *generative* classifiers model how the predictors are distributed *within each class* and invert with Bayes' theorem: `Pr(Y = k | X = x) = π_k f_k(x) / Σ_l π_l f_l(x)`. Here `π_k` is the *prior* (overall fraction in class `k`), `f_k(x)` is the *density* of `X` in class `k`, and the result `p_k(x)` is the *posterior* probability.

**Intuition.** Learn what each class's data typically looks like, then for a new point ask "which class's profile does this most resemble, weighted by how common that class is?" Bayes' theorem turns "what does class `k` look like" into "given this look, how likely is class `k`."

**Notes.** `π_k` is easy to estimate (class frequencies). The three generative methods — LDA, QDA, naive Bayes — differ only in how they estimate `f_k(x)`. All approximate the Bayes classifier. → Linear discriminant analysis, Quadratic discriminant analysis, Naive Bayes.

### Linear discriminant analysis (LDA)

**Definition.** A generative classifier that assumes each class's predictors follow a Gaussian (normal) distribution with class-specific means but a *shared covariance* across classes, then plugs the estimated means, shared variance, and priors into Bayes' theorem. The resulting discriminant scores are *linear* in `x`.

**Intuition.** Model each class as a bell curve of the same shape, just centered differently. Assign a new point to the class whose bell it falls under most, nudged by how common that class is. "Linear" because equal-shaped bells produce straight-line boundaries.

**Notes.** Useful when classes are well-separated (where logistic regression coefficients get unstable), when predictors are roughly normal with small samples, and it extends naturally to `K > 2` classes. The multivariate case uses a `p`-dimensional Gaussian with a covariance matrix — kept conceptual here. → Generative classifiers, QDA (drops the shared-covariance assumption).

### Quadratic discriminant analysis (QDA)

**Definition.** Like LDA, but each class gets its *own* covariance matrix. This makes the discriminant scores *quadratic* in `x`, allowing curved decision boundaries.

**Intuition.** Let each class's bell have its own shape and spread, not just its own center. More flexible than LDA, so it can trace curved boundaries — at the cost of estimating many more parameters.

**Notes.** Bias–variance trade-off between the two: use LDA when data are scarce (fewer parameters, lower variance) or the shared-covariance assumption is reasonable; use QDA when the training set is large or that assumption is clearly wrong. → LDA, Bias–variance trade-off.

### Naive Bayes

**Definition.** A generative classifier that assumes, *within each class, the predictors are independent* — so the class density factorizes: `f_k(x) = f_{k1}(x_1) × … × f_{kp}(x_p)`. Each one-dimensional density `f_{kj}` is estimated separately (Gaussian, histogram, kernel, or class proportions for categoricals).

**Intuition.** Rather than model how predictors move together (hard in high dimensions), pretend they don't — estimate each predictor's behavior per class on its own and multiply. The independence assumption is usually false, yet the classifier often works well because you only need the *ranking* of class probabilities to be right.

**Notes.** Trades bias for variance: the independence assumption adds bias but slashes the number of parameters, which helps when `p` is large or `n` is small. Related to LDA/QDA: LDA is a special case of naive Bayes with Gaussian, and naive Bayes with a Gaussian is LDA with a diagonal covariance; naive Bayes and QDA are not special cases of each other. → Generative classifiers.

### Confusion matrix and error types

**Definition.** A table cross-tabulating predicted vs actual classes, exposing the *two types of error* in binary classification: false positives (predict yes when actually no) and false negatives (predict no when actually yes).

**Intuition.** A single error rate hides *which* mistakes you make. In many settings (disease screening, default prediction) one error type is far costlier than the other, and the confusion matrix lets you see and trade between them.

**Notes.** Moving the classification threshold away from 0.5 trades one error type for the other. Sweeping all thresholds produces the ROC curve. → ROC curve.

### ROC curve

**Definition.** A plot that displays the two classification error types across *all* possible thresholds simultaneously, tracing true-positive rate against false-positive rate. (ROC = "receiver operating characteristics," a name inherited from communications theory.)

**Intuition.** Instead of committing to one threshold, see the whole menu of trade-offs at once. A curve hugging the top-left corner is excellent; the diagonal is random guessing. The area under it summarizes overall performance in one number.

**Notes.** Useful for comparing classifiers independent of a specific threshold. → Confusion matrix and error types.

### Poisson regression

**Definition.** A GLM for *count* responses (`Y ∈ {0, 1, 2, …}`), which assumes `Y` follows a Poisson distribution and models its mean as `λ(X) = e^(β_0 + β_1 X_1 + … + β_p X_p)`. Fit by maximum likelihood.

**Intuition.** Counts can't be negative and their spread grows with their average — a straight-line model ignores both. Poisson regression uses a log link so predictions stay non-negative, and a one-unit rise in `X_j` *multiplies* the expected count by `e^(β_j)` (e.g. `e^(−0.08) ≈ 0.923` → about 8% fewer). 

**Intuition (mean–variance).** The Poisson assumes mean = variance, so it naturally lets variability grow where the counts are large — something ordinary linear regression, with its constant variance, can't do.

**Notes.** Advantages over linear regression on counts: never predicts negative values, and it captures the mean–variance link. An example GLM alongside linear and logistic regression. → Generalized linear models.

### Generalized linear models (GLMs)

**Definition.** A family that unifies linear, logistic, and Poisson regression: model a transformation (link) of the response's mean as a linear function of the predictors, and fit by maximum likelihood.

**Intuition.** Same linear-predictor engine, different link and response distribution: identity link + Gaussian = linear regression; logit link + binomial = logistic regression; log link + Poisson = Poisson regression. One framework, three familiar tools.

**Notes.** Logistic regression served as the jumping-off point for this generalization. → Logistic regression, Poisson regression.

---

## Resampling Methods *(ISL ch. 5)*

### Resampling methods

**Definition.** Techniques that repeatedly draw different subsets from the training data, refit the model on each, and examine how the fits vary — to estimate test error or the variability of an estimate. The two workhorses are cross-validation and the bootstrap.

**Intuition.** You usually can't get fresh data on demand, so you *simulate* having many datasets by resampling the one you have. The spread of the refits tells you how much your model or estimate would wobble on new data.

**Notes.** Computationally expensive (fit the same method many times) but broadly applicable. → Validation set approach, k-fold cross-validation, The bootstrap.

### Model assessment vs model selection

**Definition.** *Model assessment* = estimating a chosen model's test-set performance. *Model selection* = choosing the right level of flexibility (or the right model) in the first place.

**Intuition.** Two different jobs resampling handles: "how good is this model?" vs "which model should I use?" Cross-validation serves both.

**Notes.** Selection often means picking a tuning parameter (K in KNN, λ in ridge/lasso, tree size, `d` in polynomials) by minimizing cross-validated error. → k-fold cross-validation.

### Validation set approach

**Definition.** Randomly split the data into a training set and a *validation (hold-out) set*; fit on the training half and estimate test error on the validation half.

**Intuition.** The simplest honest test: keep some data hidden, train, then grade yourself on the hidden part. Easy to implement, but crude.

**Notes.** Two drawbacks: (1) the estimate is *highly variable* — it swings depending on which points landed in which half; (2) it *overestimates* test error, because the model was trained on only part of the data and methods do worse with less data. Cross-validation fixes both. → Leave-one-out cross-validation, k-fold cross-validation.

### Leave-one-out cross-validation (LOOCV)

**Definition.** Hold out a *single* observation as the validation set, train on the other `n − 1`, predict the held-out point, and repeat for all `n` points; average the `n` errors.

**Intuition.** Push the validation idea to its extreme: hold out just one point at a time, so almost all the data trains the model every round. This removes the "trained on too little data" bias of the validation-set approach and, by averaging over every point, removes the randomness of the split.

**Notes.** Downside: it fits the model `n` times, which is costly for large `n` — *except* for least-squares linear/polynomial regression, where a shortcut formula (using leverage `h_i`) makes LOOCV cost the same as a single fit. Each single-point error is unbiased but very noisy on its own; the average is what's useful. → k-fold cross-validation (cheaper alternative).

### k-fold cross-validation

**Definition.** Randomly split the data into `k` roughly equal *folds*. In turn, hold out each fold as the validation set, train on the other `k − 1`, and record the error; average the `k` errors for the CV estimate. (LOOCV is the special case `k = n`.)

**Intuition.** A middle ground: hold out a chunk at a time instead of one point, so you refit only `k` times (typically 5 or 10) instead of `n`. You still train on most of the data each round, so bias stays low, and averaging tames the split randomness.

**Notes.** Cheaper than LOOCV and often has *lower variance* as a test-error estimate. In classification, swap MSE for the misclassification rate. This is the standard tool for tuning-parameter selection (λ, K, tree size). → Model assessment vs model selection.

### The bootstrap

**Definition.** A general tool for quantifying the uncertainty of an estimator: repeatedly draw *bootstrap samples* of size `n` from the data *with replacement*, recompute the estimate on each, and use the spread of those `B` estimates as a standard error.

**Intuition.** Treat your sample as a stand-in for the population and "resample your resample." Because it draws with replacement, each bootstrap dataset repeats some points and omits others, mimicking the variation you'd see across genuinely new samples. The scatter of the recomputed estimates is your uncertainty.

**Notes.** Works for quantities with no tidy standard-error formula. Sampling *with* replacement is the crux — it's what makes each bootstrap dataset differ. Bootstrap aggregation of this idea over trees is Bagging. → Bagging, Standard error of a coefficient.

---

## Linear Model Selection and Regularization *(ISL ch. 6)*

### Why move beyond least squares

**Definition.** Alternatives to plain least squares can improve *prediction accuracy* and *interpretability*. Three families: *subset selection* (keep a subset of predictors), *shrinkage/regularization* (fit all predictors but shrink coefficients toward zero), and *dimension reduction* (project predictors into fewer combined directions).

**Intuition.** Least squares has low bias but can have high variance when `n` isn't much bigger than `p` (overfitting), and when `p > n` it has no unique solution at all. It also never zeroes out useless predictors, leaving cluttered models. These methods trade a little bias for a lot less variance and simpler models.

**Notes.** Shrinkage reduces variance; subset selection and the lasso also improve interpretability by dropping variables. → Best subset selection, Ridge regression, The lasso, Principal components regression.

### Best subset selection

**Definition.** Fit a separate least-squares model for *every* possible subset of the `p` predictors (`2^p` models), pick the best model of each size by RSS/R², then choose among those using an estimate of *test* error (Cp, AIC, BIC, adjusted R², or cross-validation).

**Intuition.** Brute force: try every combination and keep the best. Note the two-stage logic — RSS picks the best model *within* a size (bigger is always better on training data), but you need a test-error estimate to compare *across* sizes fairly.

**Notes.** Conceptually clean but computationally explosive (`2^p` grows fast), and a huge search space risks finding models that look good by chance → overfitting. Stepwise methods are the practical alternatives. → Forward and backward stepwise selection.

### Forward and backward stepwise selection

**Definition.** Greedy alternatives that explore far fewer models. *Forward*: start with no predictors, add the one that most improves the fit, repeat. *Backward*: start with all predictors, drop the least useful (largest p-value), repeat. *Mixed*: add like forward but drop any variable whose value fades as others enter.

**Intuition.** Instead of all `2^p` models, walk a path one variable at a time. Much faster and usable when `p` is large. Forward can even start when `p > n`; backward needs `n > p` to begin.

**Notes.** Not guaranteed to find the best subset — an early greedy choice can lock out a better later combination. Mixed selection mimics best-subset behavior while keeping the speed. As with best subset, compare across sizes with Cp/AIC/BIC/adjusted R² or CV. → Cp, AIC, BIC, and adjusted R-squared.

### Cp, AIC, BIC, and adjusted R-squared

**Definition.** Four ways to estimate *test* error from *training* error by penalizing model size, so you can compare models with different numbers of predictors. Mallow's `Cp` and `AIC` add a `2dσ̂²`-style penalty to training RSS; `BIC` uses a heavier `log(n)·dσ̂²` penalty; *adjusted R²* modifies R² to reward fit only when a new variable earns its keep.

**Intuition.** Training error always drops as you add predictors, so it's a biased, over-optimistic gauge. These criteria charge a "complexity tax" per predictor to undo that bias. For Cp/AIC/BIC, *smaller is better*; for adjusted R², *larger is better*.

**Notes.** BIC's steeper penalty favors smaller models than Cp/AIC. All are cheaper than cross-validation but rest on more assumptions. → Best subset selection, One-standard-error rule.

### One-standard-error rule

**Definition.** After computing cross-validated (or penalized) test-error estimates for each model size, pick the *simplest* model whose error is within one standard error of the best model's error.

**Intuition.** If several models are statistically tied, prefer the simplest one. A slightly smaller, more interpretable model that's essentially as accurate beats a bigger one whose edge is within the noise.

**Notes.** A general principle, applicable wherever you tune complexity by cross-validation (subset size, λ, tree size). → k-fold cross-validation.

### Ridge regression

**Definition.** Fits all `p` predictors but minimizes `RSS + λ Σ β_j²` (a squared, "L2" *shrinkage penalty*). The tuning parameter `λ ≥ 0` controls how hard coefficients are pulled toward zero; `λ = 0` recovers least squares, `λ → ∞` drives coefficients toward (but not exactly to) zero.

**Intuition.** Add a cost for large coefficients, so the fit prefers smaller, steadier ones. This *shrinkage* trades a bit of bias for a big drop in variance — very helpful when predictors are many or correlated. Because the penalty scales with coefficient size, standardize predictors first so the penalty is fair across them.

**Notes.** Ridge keeps *all* `p` predictors (coefficients shrink but rarely hit zero), so it doesn't simplify interpretation. Efficient: fitting for all `λ` at once costs about as much as one least-squares fit. Choose `λ` by cross-validation. → The lasso (which does drop variables), Ridge vs lasso.

### The lasso

**Definition.** Like ridge, but with an absolute-value ("L1") penalty: minimize `RSS + λ Σ |β_j|`. This penalty can force some coefficients *exactly* to zero, so the lasso performs *variable selection* and yields *sparse* models.

**Intuition.** The corner-shaped L1 penalty makes it optimal to snap small coefficients all the way to zero rather than merely shrink them. So the lasso both regularizes *and* selects — you get a smaller, more interpretable model automatically. Mechanically it shaves a constant `λ/2` off each coefficient (soft-thresholding), zeroing any smaller than that.

**Notes.** Big interpretability win over ridge (a subset of predictors, not all `p`). Choose `λ` by cross-validation. → Ridge vs lasso.

### Ridge vs lasso

**Definition.** Ridge shrinks all coefficients proportionally and keeps every predictor; the lasso can zero coefficients out and select a subset. Neither dominates.

**Intuition.** Lasso tends to win when only a *few* predictors truly matter and the rest are near-zero — it cleanly discards the deadwood. Ridge tends to win when the response depends on *many* predictors of similar size — it keeps them all, gently. Since you never know which regime you're in, let cross-validation decide.

**Notes.** Both reduce variance via shrinkage; the difference is sparsity. → Ridge regression, The lasso.

### Principal components regression (PCR)

**Definition.** A dimension-reduction method: replace the `p` predictors with `M < p` *principal components* (linear combinations capturing the most variance in the predictors), then regress `Y` on those `M` components by least squares.

**Intuition.** Instead of using every correlated predictor, distill them into a few directions that capture most of their variation, and regress on those. Fewer, uncorrelated inputs → lower variance. The first component is the single direction the data spread out along most; each next one is the biggest remaining spread, perpendicular to the earlier ones.

**Notes.** The components are chosen *unsupervised* — using only `X`, ignoring `Y` — so there's no guarantee the highest-variance directions are the best *predictors*. PCA itself also serves unsupervised learning. Partial least squares fixes the "ignores `Y`" gap. → Partial least squares, High-dimensional data.

### Partial least squares (PLS)

**Definition.** A *supervised* dimension-reduction method: like PCR, but the derived directions are chosen using `Y` too, weighting each predictor by its correlation with the response.

**Intuition.** Same "combine predictors into a few directions" idea as PCR, but aimed — build directions that not only summarize the predictors but also relate to the response. It places the most weight on the predictors most correlated with `Y`.

**Notes.** Addresses PCR's blind spot (high-variance ≠ high-predictive directions), though in practice the gain over PCR is often modest. → Principal components regression.

### High-dimensional data

**Definition.** The setting where the number of features `p` is large relative to (or exceeds) the number of observations `n` — increasingly common in genomics, finance, marketing.

**Intuition.** When `p ≥ n`, least squares can fit the training data *perfectly* regardless of whether the predictors mean anything — which is disastrous overfitting. Training R² marches to 1 and training MSE to 0 as you add features, while *test* error explodes, because each added predictor inflates variance.

**Notes.** *Emphasized:* adding features truly related to `Y` helps; adding noise features hurts, raising test error. Methods that regularize or reduce dimension (forward selection, ridge, lasso, PCR) are essential here. A face of the curse of dimensionality. → Why move beyond least squares, The lasso.

---

## Moving Beyond Linearity *(ISL ch. 7)*

### Polynomial regression

**Definition.** Extends linear regression by adding powers of a predictor as extra terms — e.g. cubic regression uses `X`, `X²`, `X³`. Still a linear model (in the coefficients), just with transformed predictors.

**Intuition.** Let the fit curve by feeding it `X` and its powers. A cheap first step toward non-linearity.

**Notes.** Degrees above 3–4 are rarely used — high-degree polynomials wiggle wildly, especially near the edges of the data. They impose one *global* shape on the whole range, which motivates the piecewise methods below. → Step functions, Regression splines.

### Step functions

**Definition.** Cut the range of `X` into `K` bins and fit a separate *constant* in each — a piecewise-constant fit. This turns a continuous predictor into an ordered categorical one.

**Intuition.** Rather than one global curve, chop `X` into regions and predict a flat level in each, like a staircase. Avoids imposing a single global structure.

**Notes.** Weakness: unless there are natural breakpoints, the flat steps can miss the action between cut points. A special case of the basis-function approach. → Basis functions.

### Basis functions

**Definition.** A general framework: fit `y = β_0 + β_1 b_1(X) + … + β_K b_K(X)`, where the `b_j` are *fixed, known* transformations of `X`. Polynomials and step functions are both special cases (with `b_j(X) = X^j` or bin indicators); other choices include splines, wavelets, and Fourier series.

**Intuition.** Pick a menu of building-block functions, transform `X` through them, then fit an ordinary linear model on the transformed inputs. All the machinery of linear regression (standard errors, tests) still applies.

**Notes.** The unifying idea behind polynomial regression, step functions, and Regression splines.

### Regression splines

**Definition.** Divide the range of `X` at *knots* into regions and fit a low-degree polynomial (commonly cubic) within each, constrained so the pieces join *smoothly* at the knots (continuous value and first/second derivatives). More knots → more flexibility.

**Intuition.** Instead of one high-degree polynomial across the whole range, stitch together many low-degree ones and force smooth seams — flexible where you need it without the wild global swings of high-degree polynomials. Each smoothness constraint at a knot "costs" one degree of freedom.

**Notes.** Splines add flexibility by adding *knots* while keeping the degree fixed — usually giving better results than raising a polynomial's degree. Where to place knots: often uniformly; more knots where the function changes fast. → Natural splines, Smoothing splines, Basis functions.

### Natural splines

**Definition.** A regression spline with extra *boundary constraints*: the fit is forced to be linear beyond the outermost knots.

**Intuition.** Regular splines behave erratically at the edges (little data, high variance). Pinning the tails to straight lines tames that boundary wildness, giving more sensible extrapolation.

**Notes.** Typically produces a more reasonable fit near the boundaries than an unconstrained spline or a high-degree polynomial. → Regression splines.

### Smoothing splines

**Definition.** Fit a smooth curve `g` by minimizing `Σ(y_i − g(x_i))² + λ ∫ g''(t)² dt` — a fit-the-data term plus a *roughness penalty* on the curve's wiggliness, tuned by `λ`.

**Intuition.** You could make training error zero by threading a curve through every point, but that curve would be absurdly wiggly. The penalty on the second derivative (a measure of roughness) discourages that. `λ` sets the trade-off: `λ = 0` interpolates everything; `λ → ∞` forces a straight line.

**Notes.** No need to choose knots — there's effectively one at every observation; instead you choose `λ`, naturally by cross-validation (LOOCV is especially cheap here). The second derivative measures roughness because it tracks how fast the slope changes. → Regression splines, k-fold cross-validation.

### Local regression

**Definition.** Estimate the fit at a target point `x_0` using only nearby training points, weighting closer points more, and fitting a small (often linear) weighted least-squares model there. Repeat for each target point. The key tuning knob is the *span* `s` — the fraction of points used.

**Intuition.** Slide a weighted window along `X`; at each spot fit a tiny local line using mostly the neighbors. Small span → local and wiggly; large span → smooth and global. Like KNN, it's "memory-based": it needs all the training data at prediction time.

**Notes.** Neighborhoods overlap smoothly, unlike the hard bins of step functions. Extends to *varying-coefficient* models. → K-nearest neighbors, Step functions.

### Generalized additive models (GAMs)

**Definition.** Extend multiple linear regression by replacing each linear term `β_j x_j` with a smooth non-linear function `f_j(x_j)`, then *adding* the pieces: `y = β_0 + f_1(x_1) + … + f_p(x_p) + ε`. Works for quantitative and qualitative responses.

**Intuition.** Give every predictor its own flexible curve, but keep them *additive* — the total is still a sum of per-predictor effects. This automatically models non-linearities linear regression would miss, without hand-picking transformations, while staying readable: you can see each predictor's individual effect holding others fixed.

**Notes.** Each `f_j`'s flexibility is summarized by its degrees of freedom. Main limitation: pure additivity misses interactions among predictors — though you can add interaction terms or low-dimensional joint functions `f_{jk}(x_j, x_k)` by hand. A sweet spot between interpretable linear models and fully flexible black boxes. → Flexibility vs interpretability, Smoothing splines.

---

## Tree-Based Methods *(ISL ch. 8)*

### Decision trees

**Definition.** Methods that split the predictor space into simple, non-overlapping regions and make a constant prediction in each. Drawn upside down: split points are *internal nodes*, the final regions are *terminal nodes* (leaves).

**Intuition.** A flowchart of yes/no questions on the predictors; follow the branches to a leaf, and the leaf gives your prediction. Splits carve the feature space into boxes.

**Notes.** Come in regression and classification flavors. → Regression trees, Classification trees, Trees: strengths and weaknesses.

### Regression trees

**Definition.** Divide the predictor space into `J` boxes `R_1, …, R_J`; predict, for any point in `R_j`, the *mean* response of the training observations in `R_j`. Boxes are found by *recursive binary splitting* — a top-down, greedy procedure that at each step picks the predictor and cutpoint giving the greatest immediate reduction in RSS.

**Intuition.** Repeatedly ask "which single split most reduces error right now?" and take it, never looking ahead. Greedy because it optimizes the current step, not the whole tree; top-down because it starts with all data in one region and keeps splitting.

**Notes.** A fully grown tree overfits (too many boxes → low bias, high variance). The fix is to grow big then prune. → Tree pruning.

### Tree pruning (cost-complexity)

**Definition.** Grow a large tree, then trim it back to a subtree using *cost-complexity (weakest-link) pruning*: minimize `RSS + α·|T|`, where `|T|` is the number of leaves and `α ≥ 0` is a tuning parameter. Choose `α` by cross-validation.

**Intuition.** A big tree fits noise; a smaller one generalizes better at the cost of a little bias. The `α·|T|` term charges rent per leaf, so raising `α` prunes branches in a predictable nested order. Cross-validation finds the `α` (tree size) that minimizes test error.

**Notes.** Cheaper than evaluating every possible subtree — increasing `α` sweeps out the whole useful sequence of subtrees automatically. → Regression trees, k-fold cross-validation.

### Classification trees

**Definition.** Trees for a qualitative response: predict the *most common class* in each leaf. Splits are chosen to increase node *purity*, measured by the *Gini index* or *entropy* rather than RSS.

**Intuition.** Same recursive splitting, but "good split" now means "makes the resulting groups more class-pure." Gini and entropy both reward leaves dominated by a single class.

**Notes.** Purity measures are more sensitive than raw error rate for growing the tree. → Regression trees, Decision trees.

### Trees: strengths and weaknesses

**Definition.** Single trees are highly interpretable but relatively weak predictors.

**Intuition.** *Strengths:* easy to explain (even easier than linear regression), arguably mirror human decision-making, display graphically, and handle qualitative predictors without dummy variables. *Weaknesses:* lower predictive accuracy than top methods, and *non-robust* — a small data change can swing the whole tree.

**Notes.** Aggregating many trees — bagging, random forests, boosting, BART — trades away interpretability for big accuracy gains, largely by cutting the high variance of single trees. → Bagging, Random forests, Boosting.

### Ensemble methods and weak learners

**Definition.** An *ensemble* combines many simple "building-block" models (*weak learners*, individually mediocre) into one much stronger model.

**Intuition.** Many weak, uncorrelated opinions, aggregated, beat any single one — a crowd-of-experts effect. Trees make ideal building blocks. Bagging, random forests, boosting, and BART are all tree ensembles.

**Notes.** They differ in *how* they build and combine the trees. → Bagging, Random forests, Boosting, BART.

### Bagging

**Definition.** *Bootstrap aggregation*: draw `B` bootstrap samples, fit a tree on each, and average their predictions (regression) or take a majority vote (classification). A general variance-reduction procedure, especially useful for trees.

**Intuition.** Averaging many noisy estimates cuts variance (`Var` of an average of `n` items is `σ²/n`). Single trees are high-variance; average many bootstrap-grown trees and the wobble largely cancels, boosting accuracy. Trees are grown deep and *unpruned* — bagging handles the resulting variance.

**Notes.** More trees `B` never causes overfitting (it just stops helping), so pick `B` large enough to level off. Cost: you lose the single-tree picture, so interpretability drops; recover some via variable-importance measures (total RSS or Gini reduction per predictor). Test error comes free via OOB. → Out-of-bag error, Random forests.

### Out-of-bag (OOB) error

**Definition.** Each bagged tree is trained on a bootstrap sample that omits about one-third of observations (its *out-of-bag* points). Predict each observation using only the trees where it was OOB, and average — yielding a test-error estimate without cross-validation.

**Intuition.** Every point is naturally a hold-out for the roughly one-third of trees that never saw it. Grading each point on exactly those trees gives an honest, essentially free validation.

**Notes.** Convenient for large bagged models where cross-validation would be expensive. → Bagging.

### Random forests

**Definition.** Bagging with a twist that *decorrelates* the trees: at each split, only a random subset of `m` predictors (out of `p`) is allowed as split candidates.

**Intuition.** In plain bagging, if one predictor is very strong, nearly every tree splits on it first, so the trees look alike and averaging them barely helps. Forcing each split to ignore most predictors lets other variables lead sometimes, making the trees different — and averaging *different* trees cuts variance far more.

**Notes.** `m` is the key knob: `m = p` is just bagging; small `m` helps most when predictors are many and correlated (a common default is `m ≈ √p`). → Bagging, Variance.

### Boosting

**Definition.** Grows trees *sequentially*, each one fit to the *residuals* (the signal left over) from the current model, then added in shrunken. No bootstrap sampling — each tree uses a modified version of the original data. Three tuning parameters: number of trees `B`, shrinkage `λ` (learning rate), and tree depth `d` (interaction depth).

**Intuition.** Learn slowly and correct your own mistakes: fit a small tree, see what it still gets wrong, fit the next tree to *those* errors, and repeat, nudging the model a little each time. Small trees (even `d = 1` stumps) added gradually build a strong additive model.

**Notes.** Unlike bagging/forests, boosting *can* overfit if `B` is too large (slowly) — choose `B` by cross-validation. Small `λ` (0.01, 0.001) learns more carefully but needs larger `B`. `d` controls how many predictors can interact. → Bagging, Random forests, BART.

### Bayesian additive regression trees (BART)

**Definition.** A tree ensemble that blends the ideas of forests and boosting: trees are grown with randomness (like forests) *and* successively try to capture signal the current model misses (like boosting), by *perturbing* each tree from the previous iteration. Predictions are averaged over many iterations after a burn-in.

**Intuition.** Each round, randomly tweak the existing trees, keeping changes that improve the fit — a guided random search that both chases leftover signal and avoids getting stuck in one solution, exploring the space of models thoroughly.

**Notes.** Places among the tree ensembles by how it builds trees: bagging (independent, on resamples), random forests (independent, random feature subsets), boosting (sequential, no resampling, slow learning), BART (sequential, perturbation-based, avoids local minima). → Ensemble methods, Boosting.

---

## Support Vector Machines *(ISL ch. 9)*

### Hyperplane

**Definition.** In `p`-dimensional space, a hyperplane is a flat `(p − 1)`-dimensional subspace — a line in 2D, a plane in 3D. A *separating hyperplane* perfectly divides two labeled classes, with `β_0 + β_1 x_1 + … + β_p x_p > 0` on one side and `< 0` on the other (equivalently `y_i·(that expression) > 0` for all `i`).

**Intuition.** A flat divider one dimension below the space it lives in. If two classes can be split by such a divider, the sign of the linear score tells you which side — and which class — a point is on.

**Notes.** When a separating hyperplane exists, infinitely many do (nudge or tilt it slightly). Picking the "best" one motivates the maximal margin classifier. → Maximal margin classifier.

### Maximal margin classifier

**Definition.** Among all separating hyperplanes, the one *farthest* from the training points. The *margin* is the smallest distance from any point to the hyperplane; the maximal margin (optimal separating) hyperplane makes that smallest distance as large as possible. The points touching the margin are the *support vectors*.

**Intuition.** Split the classes with the widest possible buffer zone, giving the most breathing room on both sides. Only the closest points — the support vectors — define the boundary; the rest don't matter.

**Notes.** Fatal limitation: it *requires* the classes to be linearly separable, and even then it's hypersensitive to individual points (the boundary is fixed by a few support vectors). The support vector classifier relaxes both problems. → Support vector classifier.

### Support vector classifier (soft margin)

**Definition.** A *soft-margin* generalization of the maximal margin classifier: allow some points to sit inside the margin, or even on the wrong side of the hyperplane, controlled by a budget/tuning parameter `C`.

**Intuition.** Insisting on perfect separation makes the boundary brittle and often impossible. Deliberately tolerating a few violations buys a boundary that classifies *most* points more robustly. `C` sets how many/how severe the violations allowed — a bias–variance dial (loose `C` = wider margin, more bias, less variance).

**Notes.** Only points on or violating the margin (the support vectors) affect the boundary, making it robust to distant points. Still linear, though. → Maximal margin classifier, Support vector machine, Bias–variance trade-off.

### Support vector machine (kernels)

**Definition.** Extends the support vector classifier to *non-linear* boundaries by enlarging the feature space through *kernels* — functions that act as inner products in a higher-dimensional space without computing the coordinates explicitly. Common choices: the *polynomial kernel* and the *radial (RBF) kernel*.

**Intuition.** A straight divider in a cleverly expanded feature space is a curved divider in the original space. Kernels let you get that curvature efficiently, without ever building the huge expanded features. The radial kernel is *local*: only training points near a test point influence its label (distant points contribute almost nothing).

**Notes.** Often one of the best "out-of-the-box" classifiers. Kernel choice and its parameters (e.g. `γ` for radial, degree for polynomial) tune flexibility. → Support vector classifier, SVMs with more than two classes.

### SVMs with more than two classes

**Definition.** SVMs are inherently two-class; two schemes extend them to `K > 2`. *One-versus-one*: build an SVM for every pair of classes and let them vote. *One-versus-all (one-versus-rest)*: build `K` SVMs, each separating one class from the rest, and assign the class most confidently positive.

**Intuition.** Break a many-class problem into many two-class problems, then aggregate. One-vs-one runs more classifiers but on cleaner pairwise splits; one-vs-all runs fewer but on harder one-against-everything splits.

**Notes.** The separating-hyperplane idea doesn't generalize cleanly beyond two classes, which is why these workarounds exist. → Support vector machine.

---

## Glossary

- **Additivity assumption** — a predictor's effect on `Y` doesn't depend on other predictors' values. → [Additivity and linearity assumptions](#additivity-and-linearity-assumptions).
- **Adjusted R-squared** — R² modified to penalize useless predictors; larger = better. → [Cp, AIC, BIC, and adjusted R-squared](#cp-aic-bic-and-adjusted-r-squared).
- **AIC / Cp** — test-error estimates that tax training RSS by model size; smaller = better. → [Cp, AIC, BIC, and adjusted R-squared](#cp-aic-bic-and-adjusted-r-squared).
- **Bagging** — averaging trees fit on bootstrap samples to cut variance. → [Bagging](#bagging).
- **BART** — tree ensemble combining random perturbation with boosting-style residual fitting. → [Bayesian additive regression trees (BART)](#bayesian-additive-regression-trees-bart).
- **Basis functions** — fixed transformations of `X` fed into a linear model. → [Basis functions](#basis-functions).
- **Bayes classifier** — assigns each point to its most probable class; the ideal classifier. → [Bayes classifier](#bayes-classifier).
- **Bayes error rate** — lowest possible test error rate; classification analogue of irreducible error. → [Bayes error rate](#bayes-error-rate).
- **Bayes' theorem** — inverts within-class densities into class probabilities for generative classifiers. → [Generative classifiers and Bayes' theorem](#generative-classifiers-and-bayes-theorem).
- **BIC** — like Cp/AIC but with a heavier size penalty, favoring smaller models. → [Cp, AIC, BIC, and adjusted R-squared](#cp-aic-bic-and-adjusted-r-squared).
- **Bias** — error from approximating a complex truth with a simpler model. → [Bias](#bias).
- **Bias–variance trade-off** — expected test MSE `= Var(f̂) + Bias² + Var(ε)`. → [Bias–variance trade-off](#biasvariance-trade-off).
- **Boosting** — sequential trees each fit to the previous model's residuals. → [Boosting](#boosting).
- **Bootstrap** — resampling with replacement to quantify an estimate's uncertainty. → [The bootstrap](#the-bootstrap).
- **Classification** — predicting a qualitative (label) response. → [Regression vs classification](#regression-vs-classification).
- **Classification error rate** — fraction misclassified; test rate `= Ave(I(y_0 ≠ ŷ_0))`. → [Classification error rate](#classification-error-rate).
- **Collinearity / VIF** — predictors too related to separate; VIF > 5–10 flags it. → [Potential problems in linear regression](#potential-problems-in-linear-regression).
- **Confidence interval** — range holding the true parameter with stated probability; `≈ β̂ ± 2·SE`. → [Confidence interval](#confidence-interval).
- **Confounding** — single- vs multiple-predictor results differ due to correlated predictors. → [Confounding](#confounding).
- **Confusion matrix** — table of predicted vs actual classes, exposing error types. → [Confusion matrix and error types](#confusion-matrix-and-error-types).
- **Cross-validation** — resampling to estimate test error (LOOCV, k-fold). → [k-fold cross-validation](#k-fold-cross-validation).
- **Decision tree** — flowchart of splits carving the predictor space into regions. → [Decision trees](#decision-trees).
- **Dimension reduction** — regress on a few combined directions instead of all predictors. → [Principal components regression (PCR)](#principal-components-regression-pcr).
- **Dummy variable** — 0/1 encoding of a categorical predictor. → [Qualitative predictors and dummy variables](#qualitative-predictors-and-dummy-variables).
- **Ensemble / weak learner** — combine many mediocre models into a strong one. → [Ensemble methods and weak learners](#ensemble-methods-and-weak-learners).
- **F-statistic** — tests whether all regression coefficients are zero. → [F-statistic](#f-statistic).
- **Flexibility vs interpretability** — the spectrum from restrictive/readable to flexible/opaque. → [Flexibility vs interpretability](#flexibility-vs-interpretability).
- **GAM** — additive model with a smooth non-linear function per predictor. → [Generalized additive models (GAMs)](#generalized-additive-models-gams).
- **Generalized linear model (GLM)** — linear predictor + link + response distribution (linear/logistic/Poisson). → [Generalized linear models (GLMs)](#generalized-linear-models-glms).
- **Heteroscedasticity** — non-constant error variance (funnel-shaped residuals). → [Potential problems in linear regression](#potential-problems-in-linear-regression).
- **High-dimensional data** — `p` large relative to `n`; overfitting danger. → [High-dimensional data](#high-dimensional-data).
- **Hyperplane** — flat `(p−1)`-dimensional divider; basis of SVMs. → [Hyperplane](#hyperplane).
- **Interaction term** — product predictor capturing predictor synergy. → [Interaction terms](#interaction-terms).
- **Irreducible error** — `Var(ε)`, the noise floor no model removes. → [Reducible vs irreducible error](#reducible-vs-irreducible-error).
- **k-fold cross-validation** — average test error over `k` held-out folds. → [k-fold cross-validation](#k-fold-cross-validation).
- **K-nearest neighbors (KNN)** — majority vote / average of the `K` closest points; `1/K` sets flexibility. → [K-nearest neighbors (KNN)](#k-nearest-neighbors-knn) / [KNN regression](#knn-regression).
- **Lasso** — L1-penalized regression that zeros out coefficients (variable selection). → [The lasso](#the-lasso).
- **Least squares (RSS)** — fit that minimizes the residual sum of squares. → [Least squares and residuals (RSS)](#least-squares-and-residuals-rss).
- **Leverage** — a point's unusualness in the predictors; high leverage yanks the fit. → [Potential problems in linear regression](#potential-problems-in-linear-regression).
- **Linear discriminant analysis (LDA)** — Gaussian generative classifier, shared covariance → linear boundary. → [Linear discriminant analysis (LDA)](#linear-discriminant-analysis-lda).
- **Local regression** — fit at each point from weighted nearby points; span sets smoothness. → [Local regression](#local-regression).
- **Logistic regression** — models class probability via the logistic (S-curve) function. → [Logistic regression](#logistic-regression).
- **Log-odds (logit)** — `log[p/(1−p)]`; linear in `X` for logistic regression. → [Odds and log-odds (logit)](#odds-and-log-odds-logit).
- **LOOCV** — cross-validation holding out one point at a time. → [Leave-one-out cross-validation (LOOCV)](#leave-one-out-cross-validation-loocv).
- **Maximal margin classifier** — separating hyperplane with the widest buffer. → [Maximal margin classifier](#maximal-margin-classifier).
- **Maximum likelihood** — fit by maximizing the probability of the observed data. → [Maximum likelihood](#maximum-likelihood).
- **Mean squared error (MSE)** — `(1/n) Σ (y_i − f̂(x_i))²`; standard regression fit measure. → [Mean squared error (MSE)](#mean-squared-error-mse).
- **Model assessment vs selection** — grading a model vs choosing its flexibility. → [Model assessment vs model selection](#model-assessment-vs-model-selection).
- **Multiple linear regression** — regression on `p` predictors, each effect "holding others fixed." → [Multiple linear regression](#multiple-linear-regression).
- **Multinomial logistic regression** — logistic regression for more than two classes (softmax coding). → [Multinomial logistic regression](#multinomial-logistic-regression).
- **Naive Bayes** — generative classifier assuming within-class predictor independence. → [Naive Bayes](#naive-bayes).
- **Natural spline** — regression spline forced linear beyond the outer knots. → [Natural splines](#natural-splines).
- **Non-parametric methods** — no assumed form for `f`; flexible but data-hungry. → [Non-parametric methods](#non-parametric-methods).
- **Odds** — `p/(1−p)`, ranging 0 to ∞. → [Odds and log-odds (logit)](#odds-and-log-odds-logit).
- **One-standard-error rule** — pick the simplest model within 1 SE of the best. → [One-standard-error rule](#one-standard-error-rule).
- **Out-of-bag (OOB) error** — free test-error estimate from bagging's unused points. → [Out-of-bag (OOB) error](#out-of-bag-oob-error).
- **Overfitting** — following training noise; low training error, high test error. → [Overfitting](#overfitting).
- **Parametric methods** — assume a form for `f`, then estimate its parameters. → [Parametric methods](#parametric-methods).
- **Partial least squares (PLS)** — supervised dimension reduction using `Y`. → [Partial least squares (PLS)](#partial-least-squares-pls).
- **Poisson regression** — GLM for count responses (log link, mean = variance). → [Poisson regression](#poisson-regression).
- **Polynomial regression** — linear model with powers of `X` as predictors. → [Polynomial regression](#polynomial-regression).
- **Population vs least squares line** — true line vs its sample estimate. → [Population regression line vs least squares line](#population-regression-line-vs-least-squares-line).
- **Prediction vs inference** — accurate outputs vs understanding the relationship. → [Prediction vs inference](#prediction-vs-inference).
- **Principal components regression (PCR)** — regress on top unsupervised variance directions. → [Principal components regression (PCR)](#principal-components-regression-pcr).
- **p-value** — chance of an association this strong under the null; small → reject. → [Hypothesis test, t-statistic, and p-value](#hypothesis-test-t-statistic-and-p-value).
- **Pruning (cost-complexity)** — trim a grown tree via an `α·|T|` penalty. → [Tree pruning (cost-complexity)](#tree-pruning-cost-complexity).
- **QDA** — Gaussian generative classifier with per-class covariance → curved boundary. → [Quadratic discriminant analysis (QDA)](#quadratic-discriminant-analysis-qda).
- **Qualitative / quantitative variables** — labels vs numbers. → [Quantitative vs qualitative variables](#quantitative-vs-qualitative-variables).
- **R-squared** — proportion of variance explained; `1 − RSS/TSS`. → [R-squared and correlation](#r-squared-and-correlation).
- **Random forests** — bagging + random feature subsets per split to decorrelate trees. → [Random forests](#random-forests).
- **Reducible vs irreducible error** — error you can attack vs the noise floor. → [Reducible vs irreducible error](#reducible-vs-irreducible-error).
- **Regression** — predicting a quantitative response. → [Regression vs classification](#regression-vs-classification).
- **Regression splines** — piecewise polynomials joined smoothly at knots. → [Regression splines](#regression-splines).
- **Regression tree** — tree predicting the mean response in each region. → [Regression trees](#regression-trees).
- **Residual standard error (RSE)** — estimated SD of the noise; average deviation from the line. → [Residual standard error (RSE)](#residual-standard-error-rse).
- **Resampling** — refitting on repeated data subsets to estimate error/variability. → [Resampling methods](#resampling-methods).
- **Ridge regression** — L2-penalized regression that shrinks all coefficients. → [Ridge regression](#ridge-regression).
- **ROC curve** — error-type trade-offs across all thresholds. → [ROC curve](#roc-curve).
- **Semi-supervised learning** — some observations labeled, some not. → [Semi-supervised learning](#semi-supervised-learning).
- **Shrinkage / regularization** — penalize coefficient size to reduce variance. → [Ridge regression](#ridge-regression).
- **Simple linear regression** — straight-line fit from one predictor. → [Simple linear regression](#simple-linear-regression).
- **Smoothing spline** — curve minimizing RSS + a roughness penalty. → [Smoothing splines](#smoothing-splines).
- **Standard error** — typical sampling wobble of an estimate. → [Standard error of a coefficient](#standard-error-of-a-coefficient).
- **Statistical learning** — approaches for estimating `f` in `Y = f(X) + ε`. → [Statistical learning](#statistical-learning).
- **Step functions** — piecewise-constant fit over bins of `X`. → [Step functions](#step-functions).
- **Stepwise selection** — greedily add (forward) or drop (backward) predictors. → [Forward and backward stepwise selection](#forward-and-backward-stepwise-selection).
- **Subset selection (best)** — try all `2^p` predictor subsets. → [Best subset selection](#best-subset-selection).
- **Supervised / unsupervised learning** — labeled `(x,y)` vs predictors-only. → [Supervised vs unsupervised learning](#supervised-vs-unsupervised-learning).
- **Support vector classifier** — soft-margin linear classifier tolerating violations. → [Support vector classifier (soft margin)](#support-vector-classifier-soft-margin).
- **Support vector machine (SVM)** — kernel-enlarged classifier for non-linear boundaries. → [Support vector machine (kernels)](#support-vector-machine-kernels).
- **t-statistic** — coefficient estimate in units of its standard error. → [Hypothesis test, t-statistic, and p-value](#hypothesis-test-t-statistic-and-p-value).
- **Test / training MSE** — error on unseen vs fitting data; test is U-shaped in flexibility. → [Training MSE vs test MSE](#training-mse-vs-test-mse).
- **Validation set approach** — single train/hold-out split. → [Validation set approach](#validation-set-approach).
- **Variance** — how much `f̂` shifts across training sets; rises with flexibility. → [Variance](#variance).
- **Variable selection** — choosing which predictors to include. → [Best subset selection](#best-subset-selection).