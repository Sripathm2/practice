# ML Notes

A single running file of statistical-learning and ML concepts, in Definition / Intuition / Notes form. Written to stand alone — no book required to read them.

---

## Contents

**Statistical Learning — Foundations** *(ISL ch. 2)*
- Statistical learning
- Supervised vs unsupervised learning
- Semi-supervised learning
- Prediction vs inference
- Reducible vs irreducible error
- Quantitative vs qualitative variables
- Regression vs classification

**Estimating f** *(ISL ch. 2)*
- Parametric methods
- Non-parametric methods
- Overfitting
- Flexibility vs interpretability

**Assessing Model Accuracy** *(ISL ch. 2)*
- Mean squared error (MSE)
- Training MSE vs test MSE
- Bias–variance trade-off
- Bias
- Variance
- Classification error rate
- Bayes classifier
- Bayes decision boundary
- Bayes error rate
- K-nearest neighbors (KNN)

**Glossary** — alphabetical index

---

## Statistical Learning — Foundations *(ISL ch. 2)*

### Statistical learning

**Definition.** A set of approaches for estimating an unknown function `f` that links inputs `X = (X_1, …, X_p)` to an output `Y`, modeled as `Y = f(X) + ε`, where `ε` is a random error term independent of `X` with mean zero.

**Intuition.** We assume some systematic relationship `f` connects inputs to output, but it's buried in noise. Statistical learning is the toolkit for recovering as much of `f` as the data allows — either to predict new outputs, or to understand how the inputs drive the output.

**Notes.** `ε` is the *irreducible error* (→ Reducible vs irreducible error). If every observation pairs inputs with a known `Y`, the problem is *supervised*; with no `Y`, it's *unsupervised*. Everything downstream — parametric vs non-parametric, the bias–variance trade-off — is about how well we can pin down `f̂`.

### Supervised vs unsupervised learning

**Definition.** Supervised learning fits a model from observations that each pair predictors `x_i` with a response `y_i`, for the purpose of prediction or inference. Unsupervised learning has predictors `x_i` but no response `y_i`, so the goal is to find structure among observations rather than predict a labeled output.

**Intuition.** Supervised = learning with an answer key: every example tells you the right output, so you can measure and correct error. Unsupervised = working blind: no answer key, so you look for patterns, groupings, or structure in the inputs themselves.

**Notes.** Supervised methods named so far: linear regression, logistic regression, classification and regression trees, GAMs, boosting, support vector machines. Unsupervised can't fit a regression model — there's no response to supervise the analysis. Almost all of the model-accuracy machinery below (MSE, error rate, bias–variance) presumes a response to compare against. See Semi-supervised learning for the in-between case.

### Semi-supervised learning

**Definition.** A setting with `n` observations where `m < n` have both predictors and a response, and the remaining `n − m` have predictors only; the aim is a method that uses both the labeled and unlabeled observations.

**Intuition.** Common when predictors are cheap to measure but responses are expensive to collect. You don't want to discard the unlabeled majority, so you use them to sharpen an estimate anchored by the labeled few.

**Notes.** Sits between supervised and unsupervised. *(beyond ISL ch. 2 — named but not developed there.)*

### Prediction vs inference

**Definition.** In prediction, the goal is an accurate `f̂` giving good output estimates `Ŷ = f̂(X)` for new inputs, and `f̂` may be a black box. In inference, the goal is to understand the relationship between `X` and `Y` — which predictors matter, in what direction, how — so `f̂` must be interpretable.

**Intuition.** Prediction only cares that the answer is right; you needn't know why. Inference cares about the "why," and will trade some predictive accuracy for a model you can read.

**Notes.** This goal drives model choice: prediction can justify flexible black-box models; inference favors restrictive, interpretable ones like linear regression. → Flexibility vs interpretability.

### Reducible vs irreducible error

**Definition.** For `Ŷ = f̂(X)`, expected squared prediction error splits as `E[(Y − Ŷ)²] = [f(X) − f̂(X)]² + Var(ε)`. The first term is *reducible error* (shrinks as `f̂` improves); `Var(ε)` is the *irreducible error*, a floor set by noise that no model can remove.

**Intuition.** Some error is your model's fault (a wrong or imprecise `f̂`) — you can chip away at it with better methods and more data. The rest is baked into the problem: unmeasured variables, inherent randomness. Even the true `f` wouldn't predict perfectly.

**Notes.** *Emphasized:* you can only ever attack the reducible part. `Var(ε)` is the same quantity that reappears as the noise floor in the Bias–variance trade-off, and it's why test error bottoms out above zero. Its classification analogue is the Bayes error rate.

### Quantitative vs qualitative variables

**Definition.** Quantitative variables take numerical values (e.g. salary, age). Qualitative (categorical) variables take values in one of `K` classes or categories (e.g. brand, disease status).

**Intuition.** Numbers you can average vs labels you can only count. The type of the *response* decides what kind of problem you have.

**Notes.** Quantitative response → regression; qualitative response → classification. → Regression vs classification.

### Regression vs classification

**Definition.** Problems with a quantitative response are *regression* problems; problems with a qualitative response are *classification* problems.

**Intuition.** Predicting "how much / how many" (a number) is regression; predicting "which class" (a label) is classification.

**Notes.** The line isn't crisp. Logistic regression is a classification method (qualitative, often binary response) but it estimates class probabilities, so it has a regression flavor. Some methods — KNN, boosting — handle either response type. Least squares linear regression is for quantitative responses.
Two classification methods named in the highlights, both for qualitative responses: **logistic regression** (models the log-odds of class membership directly) and **linear discriminant analysis (LDA)** (models each class's predictor distribution, then applies Bayes' rule). They are distinct methods — *(beyond ISL ch. 2)*.

---

## Estimating f *(ISL ch. 2)*

### Parametric methods

**Definition.** A two-step, model-based approach to estimating `f`: (1) assume a functional form for `f` (e.g. linear, `f(X) = β_0 + β_1 X_1 + … + β_p X_p`); (2) use training data to fit/train the model — i.e. estimate its parameters (for the linear form, the `p + 1` coefficients `β_0, …, β_p`).

**Intuition.** Rather than searching all possible functions, you commit to a shape and tune its dials. Assuming linearity collapses an arbitrary `p`-dimensional function into just `p + 1` numbers to estimate — far easier, and less data-hungry.

**Notes.** The risk: if the assumed form is wrong, `f̂` stays biased no matter how much data you have. More flexible parametric forms fit more shapes but risk Overfitting. The linear form is typically fit by least squares. Contrast Non-parametric methods.

### Non-parametric methods

**Definition.** Methods that make no explicit assumption about the functional form of `f`; they seek an `f̂` that gets as close to the data as possible without being too rough or wiggly.

**Intuition.** You don't pre-commit to a shape — you let the data draw `f`. This can capture relationships a linear model would miss.

**Notes.** Cost: with no small set of parameters to pin down, you need far more observations than a parametric method to estimate `f` accurately. You trade freedom from a possibly-wrong assumption for data-hunger and overfitting risk. KNN is the non-parametric example in this file. Contrast Parametric methods.

### Overfitting

**Definition.** When a model follows the training data's errors or noise too closely, producing a small training MSE but a large test MSE.

**Intuition.** The model memorizes quirks of the training sample — noise that won't recur — instead of the underlying signal. It looks great on data it has seen and stumbles on data it hasn't.

**Notes.** More flexible methods are more prone to it. It's why the test-error curve is U-shaped: past a point, added flexibility buys noise-fitting, not signal. In bias–variance terms, overfitting = low bias but high variance. → Bias–variance trade-off, Training MSE vs test MSE.

### Flexibility vs interpretability

**Definition.** Methods trade off along a spectrum from inflexible/interpretable to flexible/opaque. Least squares linear regression is inflexible but highly interpretable; the lasso is similar; GAMs are more flexible (they allow certain non-linear relationships) while staying fairly interpretable; bagging, boosting, SVMs with non-linear kernels, and neural networks (deep learning) are highly flexible but hard to interpret.

**Intuition.** Flexibility = how many shapes a method can bend to. More flexibility can fit complex truths but makes the fitted model harder to read and easier to overfit. If you want to explain the `X`–`Y` relationship (inference), reach for a restrictive, readable model.

**Notes.** *Emphasized:* more flexible ≠ more accurate. Because of overfitting, a less flexible method often predicts better on test data — counterintuitive but fundamental. Choosing the right level of flexibility is the central practical problem, in both regression and classification. → Prediction vs inference, Bias–variance trade-off.

---

## Assessing Model Accuracy *(ISL ch. 2)*

### Mean squared error (MSE)

**Definition.** In regression, the standard measure of fit quality: `MSE = (1/n) Σ_{i=1}^{n} (y_i − f̂(x_i))²` — the average squared gap between observed responses and predictions.

**Intuition.** How far off your predictions are on average, with big misses penalized disproportionately (squaring). Small MSE = predictions land close to the truth.

**Notes.** Computed on the fitting data it's the *training MSE*; what actually matters is the *test MSE* on unseen data. → Training MSE vs test MSE.

### Training MSE vs test MSE

**Definition.** Training MSE is computed on the data used to fit the model; test MSE is computed on previously unseen test observations. The objective is to minimize test MSE.

**Intuition.** Acing questions you studied (training) doesn't prove you'll ace the exam (test). Only performance on fresh data measures real predictive skill.

**Notes.** *Emphasized:* there is no guarantee the method with the lowest training MSE has the lowest test MSE — often it's the reverse. As flexibility increases, training MSE decreases *monotonically* while test MSE traces a *U-shape* (falls, then rises as overfitting sets in) — a fundamental property that holds regardless of data set or method. A widening gap between the two is the signature of overfitting. When no test set is available, resampling (e.g. cross-validation) estimates test error — *(beyond ISL ch. 2)*.

### Bias–variance trade-off

**Definition.** Expected test MSE at a point `x_0` decomposes as `E[(y_0 − f̂(x_0))²] = Var(f̂(x_0)) + [Bias(f̂(x_0))]² + Var(ε)`. Minimizing expected test error requires *simultaneously* low variance and low bias; as flexibility changes, the two typically move in opposite directions.

**Intuition.** Expected test error has three ingredients: how much the fit jumps around across different training sets (*variance*), how much the model's simplifying assumptions distort the truth (*bias²*), and the irreducible noise floor (`Var(ε)`). You can't zero out both bias and variance at once — tightening one usually loosens the other — so you aim for the sweet spot that minimizes their sum. As flexibility rises, bias falls (the model can match complex truth) but variance rises (the fit chases the particular sample). The U-shaped test-MSE curve *is* this trade-off made visible.

**Notes.** `Var(ε)` is the irreducible error — the same floor from Reducible vs irreducible error; expected test MSE can never drop below it. Underfitting = high bias; overfitting = high variance. This trade-off touches nearly every method in this file. → Bias, Variance, Overfitting.

### Bias

**Definition.** The error introduced by approximating a complicated real-world relationship with a much simpler model. As methods get more flexible, bias generally decreases.

**Intuition.** Force a straight line through a curvy truth and you're systematically wrong in a way more data won't fix — that's bias. Simpler, more restrictive models carry more of it.

**Notes.** Low bias is one of the two things low test error needs; the other is low Variance. High bias = underfitting. Trades off against Variance.

### Variance

**Definition.** The amount by which `f̂` would change if it were estimated on a different training set. More flexible methods generally have higher variance.

**Intuition.** Refit the model on a fresh sample from the same population — does the fit barely move, or swing wildly? Wild swings = high variance. Flexible methods bend to each sample's noise, so they're less stable.

**Notes.** *Emphasized:* small changes in the training data producing large changes in `f̂` is the hallmark of high variance. High variance = overfitting. Trades off against Bias.

### Classification error rate

**Definition.** In classification, fit quality is measured by the error rate — the fraction of misclassified observations. The *test error rate* on test observations `(x_0, y_0)` is `Ave(I(y_0 ≠ ŷ_0))`, the average of the indicator that the predicted label `ŷ_0` differs from the true label `y_0`; the *training error rate* is the same quantity computed on the training data.

**Intuition.** The classification analogue of MSE — instead of averaging squared numeric misses, you count how often you named the wrong class.

**Notes.** `I(·)` is the indicator function (1 if the condition holds, else 0). As with MSE, training error consistently falls as flexibility rises while test error is U-shaped, so a low training error rate can mislead. A good classifier has a small *test* error rate. The theoretical floor on that rate is the Bayes error rate.

### Bayes classifier

**Definition.** The classifier that assigns each observation with predictor vector `x_0` to the most probable class given those predictors — the class `j` maximizing the conditional probability `Pr(Y = j | X = x_0)`. In a two-class problem, predict class 1 if `Pr(Y = 1 | X = x_0) > 0.5`, else class 2.

**Intuition.** If you actually knew the true class probabilities at every point, your best bet is always the most likely class. That's the Bayes classifier — the gold standard every real classifier tries to approximate.

**Notes.** `Pr(Y = j | X = x_0)` is a conditional probability. The Bayes classifier produces the lowest possible test error rate (→ Bayes error rate) but is a theoretical ideal: in practice we don't know the true conditional distribution and must estimate it (e.g. KNN estimates it from nearby points). It defines the Bayes decision boundary.

### Bayes decision boundary

**Definition.** The set of points where the Bayes classifier is indifferent between classes — in the two-class case, where `Pr(Y = 1 | X = x_0) = 0.5`. It partitions the predictor space into regions assigned to each class.

**Intuition.** The dividing line the perfect classifier would draw. On one side one class wins; on the other, the other.

**Notes.** Real classifiers try to approximate this boundary. A too-flexible fit (e.g. KNN with `K = 1`) produces a jagged boundary that chases noise instead of tracking the true Bayes boundary.

### Bayes error rate

**Definition.** The lowest possible test error rate, achieved by the Bayes classifier. Overall it equals `1 − E[max_j Pr(Y = j | X)]`, the expectation taken over all values of `X`; at a single point `x_0` it is `1 − max_j Pr(Y = j | X = x_0)`.

**Intuition.** Even the perfect classifier is wrong sometimes, because classes overlap — at some `x`, more than one class carries real probability. The Bayes error rate is that unavoidable minimum.

**Notes.** The classification analogue of `Var(ε)` / irreducible error — a floor no classifier beats.

### K-nearest neighbors (KNN)

**Definition.** A non-parametric classifier. Given a positive integer `K` and a test point `x_0`, KNN identifies the `K` training points closest to `x_0` (call them `N_0`), estimates each class's conditional probability as the fraction of `N_0` in that class — `Pr(Y = j | X = x_0) ≈ (1/K) Σ_{i ∈ N_0} I(y_i = j)` — and assigns `x_0` to the class with the largest estimated probability.

**Intuition.** Ask the `K` nearest neighbors to vote; go with the majority. KNN approximates the Bayes classifier by estimating the true conditional probabilities locally from whoever's nearby.

**Notes.** *Emphasized:* the choice of `K` has a drastic effect, and it's `1/K` that acts as the flexibility knob. `K = 1` is maximally flexible — a jagged, low-bias/high-variance boundary that finds noise patterns absent from the true Bayes boundary; large `K` (e.g. `K = 100`) is smoother, less flexible, higher-bias/lower-variance. Choosing `K` well *is* the bias–variance trade-off in action. KNN can also do regression (average the neighbors' responses) — *(beyond ISL ch. 2)*. → Non-parametric methods, Bayes classifier.

---

## Glossary

- **Bayes classifier** — assigns each point to the most probable class given its predictors; the ideal, lowest-error classifier. → Assessing Model Accuracy.
- **Bayes decision boundary** — where the Bayes classifier is indifferent between classes (two-class: `Pr = 0.5`). → Assessing Model Accuracy.
- **Bayes error rate** — the lowest possible test error rate; `1 − E[max_j Pr(Y = j | X)]`. Classification analogue of irreducible error. → Assessing Model Accuracy.
- **Bias** — error from approximating a complex truth with a simpler model; falls as flexibility rises. → Assessing Model Accuracy.
- **Bias–variance trade-off** — expected test MSE `= Var(f̂) + Bias² + Var(ε)`; low test error needs both low bias and low variance. → Assessing Model Accuracy.
- **Classification** — predicting a qualitative (label) response. → Regression vs classification.
- **Classification error rate** — fraction of misclassified points; test rate `= Ave(I(y_0 ≠ ŷ_0))`. → Assessing Model Accuracy.
- **Flexibility vs interpretability** — the spectrum from restrictive/readable to flexible/opaque models. → Estimating f.
- **Inference** — modeling to understand the `X`–`Y` relationship, not just predict. → Prediction vs inference.
- **Irreducible error** — `Var(ε)`, the noise floor no model removes. → Reducible vs irreducible error.
- **K-nearest neighbors (KNN)** — non-parametric method; majority vote of the `K` closest training points; `1/K` sets flexibility. → Assessing Model Accuracy.
- **Mean squared error (MSE)** — `(1/n) Σ (y_i − f̂(x_i))²`; standard regression fit measure. → Assessing Model Accuracy.
- **Non-parametric methods** — no assumed form for `f`; flexible but data-hungry. → Estimating f.
- **Overfitting** — following training noise too closely; low training MSE, high test MSE. → Estimating f.
- **Parametric methods** — assume a functional form for `f`, then estimate its parameters. → Estimating f.
- **Prediction** — modeling to get accurate outputs for new inputs; `f̂` may be a black box. → Prediction vs inference.
- **Qualitative (categorical) variables** — values in one of `K` classes. → Quantitative vs qualitative variables.
- **Quantitative variables** — numerical values. → Quantitative vs qualitative variables.
- **Reducible vs irreducible error** — error you can attack (`[f − f̂]²`) vs the noise floor (`Var(ε)`). → Statistical Learning — Foundations.
- **Regression** — predicting a quantitative (numeric) response. → Regression vs classification.
- **Semi-supervised learning** — some observations labeled, some not; use both. → Statistical Learning — Foundations.
- **Statistical learning** — approaches for estimating `f` in `Y = f(X) + ε`. → Statistical Learning — Foundations.
- **Supervised learning** — fitting from labeled `(x_i, y_i)` pairs. → Statistical Learning — Foundations.
- **Test MSE** — MSE on unseen data; the quantity to minimize; U-shaped in flexibility. → Assessing Model Accuracy.
- **Training MSE** — MSE on the fitting data; decreases monotonically in flexibility. → Assessing Model Accuracy.
- **Unsupervised learning** — predictors but no response; find structure. → Statistical Learning — Foundations.
- **Variance** — how much `f̂` shifts across training sets; rises with flexibility. → Assessing Model Accuracy.