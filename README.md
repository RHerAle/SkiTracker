# SkiTracker: Alpine Telemetry and Metabolic Engine

SkiTracker provides activity recognition and metabolic expenditure algorithms for ski tracking. The engine classifies GPS telemetry into downhill skiing, chairlift or gondola ascents, and stationary stops. It pairs classification with speed-adjusted MET calorie calculations. Pure Kotlin implementation with zero platform dependencies.

| File | Role |
|---|---|
| `src/ActivityRecognizer.kt` | Hysteresis classifier separating `SKIING` (5 to 30 m/s, vertical speed < -0.5 m/s), `LIFT` (2 to 8 m/s, vertical speed > 0.5 m/s), and `STATIONARY` (< 1.5 m/s) over sliding GPS windows |
| `src/CalorieCalculator.kt` | Metabolic expenditure model ($\text{kcal/min} = \frac{\text{MET} \times m_{\text{kg}} \times 3.5}{200}$) scaled by activity state and descent speed (4.3 to 8.0 MET) |
| `src/TelemetryModels.kt` | Telemetry types (`LocationPoint`, `ActivityType`) |
| `tests/ActivityRecognizerTest.kt` | Tests for state transitions, vertical speed thresholds, and hysteresis anti-flapping |
| `tests/CalorieCalculatorTest.kt` | Tests for MET intensity tiers and body mass scaling |

## Algorithmic Model

### 1. Activity Classification with Hysteresis (`ActivityRecognizer`)
Given a sliding window of recent `LocationPoint` samples $(t_i, \phi_i, \lambda_i, z_i, v_i)$:
- Mean horizontal speed $\bar{v} = \frac{1}{N}\sum_{i=1}^N v_i$ and vertical velocity $\dot{z} = \frac{z_N - z_1}{(t_N - t_1)/1000}$.
- Candidate state:
  - **Downhill Skiing (`SKIING`):** $5.0 \le \bar{v} \le 30.0\text{ m/s}$ and $\dot{z} < -0.5\text{ m/s}$.
  - **Ski Lift (`LIFT`):** $2.0 \le \bar{v} \le 8.0\text{ m/s}$ and $\dot{z} > +0.5\text{ m/s}$.
  - **Stationary (`STATIONARY`):** $\bar{v} < 1.5\text{ m/s}$ or ambiguous vertical/speed signature.
- **Hysteresis filter:** requires 3 consecutive consistent candidate evaluations before transitioning `currentActivity`. This prevents false state flips at lift terminals or brief slope stops.

### 2. Metabolic Expenditure (`CalorieCalculator`)
$$\text{Calories/min} = \frac{\text{MET}(a, v) \times \text{weight}_{\text{kg}} \times 3.5}{200}$$
where $\text{MET}(\text{SKIING}, v) \in \{4.3, 5.3, 8.0\}$ for light (< 8 m/s), moderate (8 to 15 m/s), and vigorous ($\ge$ 15 m/s) skiing, $\text{MET}(\text{LIFT}) = 1.5$, and $\text{MET}(\text{STATIONARY}) = 1.3$.

## License

© 2026 SkiTracker contributors. Source code is licensed under the GNU Affero General Public License, version 3 ([`LICENSE`](LICENSE)).

Commercial licenses remain available from the author upon request.

## Contributing

Read [`CONTRIBUTING.md`](CONTRIBUTING.md) and [`FLA.md`](FLA.md).
