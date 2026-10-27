## 2026-10-25 - Uncaught Exceptions in Telemetry Engine Disrupt Background Recording
**Vulnerability:** Throwing uncaught `IllegalArgumentException` in calorie calculation functions on bad user/telemetry inputs (`NaN`, negative numbers) crashed background telemetry recording loops.
**Learning:** In continuous background data collection pipelines, input validation must fall back or sanitize values defensively rather than throwing runtime exceptions that break execution.
**Prevention:** Avoid throwing uncaught runtime exceptions in hot background processing loops; sanitize inputs to default safe bounds or return safe fallback values.
