# SkiTracker — Alpine Telemetry & Metabolic Engine

The core activity recognition and metabolic expenditure algorithms behind **SkiApp**: automatic classification of GPS telemetry into downhill skiing, chairlift/gondola ascents, and stationary intervals, paired with speed-adjusted MET calorie estimation.

> **Architecture note:** This repository publishes only the internal algorithmic modules and their unit test specifications for transparency and auditability. It does not contain the Android application shell, foreground GPS services, Room persistence layer, or Gradle build system.

| File | What it computes |
|---|---|
| `src/ActivityRecognizer.kt` | Hysteresis-stabilized classifier distinguishing `SKIING` ($5\text{–}30\text{ m/s}$, $\Delta z/\Delta t < -0.5\text{ m/s}$), `LIFT` ($2\text{–}8\text{ m/s}$, $\Delta z/\Delta t > +0.5\text{ m/s}$), and `STATIONARY` ($< 1.5\text{ m/s}$) over a sliding window of GPS samples |
| `src/CalorieCalculator.kt` | Metabolic Equivalent of Task (MET) expenditure model ($\text{kcal/min} = \frac{\text{MET} \times m_{\text{kg}} \times 3.5}{200}$) dynamically scaled by activity state and downhill speed ($4.3\text{–}8.0\text{ MET}$) |
| `src/TelemetryModels.kt` | Pure data structures (`LocationPoint`, `ActivityType`) |
| `tests/ActivityRecognizerTest.kt` | Unit test specification for state transitions, hysteresis anti-flapping, and vertical rate thresholds |
| `tests/CalorieCalculatorTest.kt` | Unit test specification for intensity tiers and body-mass scaling |

## Algorithmic Model

### 1. Activity Classification with Hysteresis (`ActivityRecognizer`)
Given a sliding window of recent `LocationPoint` samples $(t_i, \phi_i, \lambda_i, z_i, v_i)$:
- Mean horizontal speed $\bar{v} = \frac{1}{N}\sum_{i=1}^N v_i$ and vertical velocity $\dot{z} = \frac{z_N - z_1}{(t_N - t_1)/1000}$.
- Candidate state:
  - **Downhill Skiing (`SKIING`):** $5.0 \le \bar{v} \le 30.0\text{ m/s}$ and $\dot{z} < -0.5\text{ m/s}$.
  - **Ski Lift (`LIFT`):** $2.0 \le \bar{v} \le 8.0\text{ m/s}$ and $\dot{z} > +0.5\text{ m/s}$.
  - **Stationary (`STATIONARY`):** $\bar{v} < 1.5\text{ m/s}$ or ambiguous vertical/speed signature.
- **Hysteresis filter:** requires $K = 3$ consecutive consistent candidate evaluations before transitioning `currentActivity`, preventing false state flips at lift terminals or brief slope stops.

### 2. Metabolic Expenditure (`CalorieCalculator`)
$$\text{Calories/min} = \frac{\text{MET}(a, v) \times \text{weight}_{\text{kg}} \times 3.5}{200}$$
where $\text{MET}(\text{SKIING}, v) \in \{4.3, 5.3, 8.0\}$ for light ($<8\text{ m/s}$), moderate ($8\text{–}15\text{ m/s}$), and vigorous ($\ge 15\text{ m/s}$) skiing, $\text{MET}(\text{LIFT}) = 1.5$, and $\text{MET}(\text{STATIONARY}) = 1.3$.

## Licence

© 2026 SkiTracker contributors. Source code is licensed under the GNU Affero General
Public License, version 3 ([`LICENSE`](LICENSE)).

A commercial or closed-source license is available from the author upon request.

## Contributing

See [`CONTRIBUTING.md`](CONTRIBUTING.md) and the contribution agreement in [`FLA.md`](FLA.md).
