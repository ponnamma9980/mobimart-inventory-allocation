# MobiMart Inventory Allocation

## Smart Inventory Replenishment and Budget-Constrained Allocation Dashboard

MobiMart Inventory Allocation is a Java and Spring Boot-based inventory optimization system designed to support data-driven inventory replenishment decisions for a retail mobile-phone chain.

The system analyzes historical sales, current inventory, store characteristics, product categories, product lifecycle information, and budget constraints to determine where inventory should be replenished and how much should be allocated.

In addition to inventory allocation, the system provides End-of-Life (EOL) risk analysis, transfer recommendations, and a performance comparison between the proposed allocation strategy and a naive baseline approach.

The project includes a web-based dashboard that presents recommendations and business metrics in an easy-to-understand format.

---

# 1. Project Overview

Retail businesses need to maintain sufficient inventory to satisfy customer demand while avoiding excessive stock and unnecessary capital investment.

A simple approach of distributing inventory based only on previous sales can result in:

- Stockouts at high-demand stores
- Excess inventory at low-demand stores
- Uneven inventory distribution
- Excessive capital tied up in inventory
- Increased markdown exposure
- Poor handling of End-of-Life products

MobiMart addresses these problems by combining demand analysis, inventory availability, store-level factors, product category, lifecycle information, and budget constraints.

The system generates explainable inventory recommendations for each store and phone combination.

---

# 2. Objectives

The main objectives of the project are:

1. Estimate demand using historical sales data.
2. Determine inventory requirements for each store and phone model.
3. Identify products with inventory shortages.
4. Calculate a priority score for replenishment opportunities.
5. Allocate inventory while respecting the overall inventory budget.
6. Identify inventory exposed to End-of-Life risk.
7. Compare transfer costs with potential markdown costs.
8. Provide actionable transfer recommendations.
9. Compare the proposed allocation strategy with a naive baseline.
10. Present important results through a web dashboard.

---

# 3. Key Features

## 3.1 Smart Inventory Allocation

The allocation engine analyzes each store + phone model combination.

For every opportunity, the system evaluates:

- Average weekly sales
- Current inventory
- Target inventory
- Replenishment requirement
- Store attractiveness
- Product category
- Stockout impact
- Product lifecycle
- Priority score

The system then generates a recommended number of units and the corresponding inventory investment.

## 3.2 Demand-Based Inventory Target

The system estimates average weekly demand using historical sales data.

The inventory target is designed around approximately two weeks of expected demand.

This helps maintain sufficient stock while avoiding unnecessary inventory accumulation.

## 3.3 Priority-Based Allocation

Each replenishment opportunity receives a priority score based on multiple factors.

The allocation considers:

| Factor | Purpose |
|---|---|
| Demand | Identifies products with stronger sales |
| Store attractiveness | Considers store-level demand potential |
| Stockout impact | Gives importance to products where stockouts matter |
| Product category | Differentiates budget, mid-range, premium and flagship products |
| Product lifecycle | Reduces priority for products approaching replacement |
| Replenishment need | Considers the size of the inventory gap |

This produces a more targeted allocation than a simple proportional-sales approach.

## 3.4 Budget-Constrained Allocation

The system operates under a chain-wide inventory budget of:

**₹4 Crore**

The allocation engine ensures that recommendations remain within the available budget.

The system also applies allocation limits at the store and opportunity level to prevent excessive concentration of inventory.

The dashboard displays budget utilization through a visual progress bar.

---

# 4. End-of-Life Risk Analysis

Mobile-phone products have a limited lifecycle. When a product approaches its End-of-Life stage, holding excess inventory can create significant markdown exposure.

MobiMart identifies inventory items that are at risk and evaluates possible actions.

For each EOL-risk item, the system considers:

- Current stock
- Inventory value
- Risk level
- Potential markdown cost
- Transfer cost
- Recommended action
- Decision cost

The system compares the financial impact of retaining inventory with the cost of transferring it to another location.

When transferring inventory is financially preferable, the system recommends:

**TRANSFER**

This helps reduce potential markdown exposure and improve inventory utilization.

---

# 5. Transfer Savings

The dashboard calculates potential savings generated through transfer decisions.

The basic decision principle is:

```text
Potential Markdown Cost
          vs
     Transfer Cost
```

If the transfer cost is lower than the potential markdown exposure, transferring inventory can reduce the expected loss.

The dashboard summarizes estimated transfer savings across identified EOL-risk items.

---

# 6. Performance Comparison

The project includes a comparison between the proposed system and a naive baseline.

### Proposed System

The MobiMart inventory optimization strategy considers:

- Demand
- Inventory gaps
- Store attractiveness
- Stockout impact
- Product lifecycle
- Budget constraints

### Naive Baseline

The baseline represents a simpler allocation approach based primarily on previous-month sales.

The two approaches are compared using:

| Metric | Preferred Direction |
|---|---|
| Stockout Rate | Lower is better |
| Weeks of Cover | Lower is better |
| Dead Stock | Lower is better |
| Markdown Loss | Lower is better |
| Capital Turns | Higher is better |

The dashboard automatically identifies the winner for each metric.

---

# 7. Dashboard

The project provides a browser-based inventory optimization dashboard.

The dashboard contains:

### Allocation Summary

- Total recommendations
- Allocation value
- Recommended units
- Average priority

### Budget

- Current recommended investment
- Maximum chain-wide inventory budget
- Budget utilization

### Inventory Risk Summary

- Current inventory capital
- At-risk capital
- EOL risk items
- Transfer savings

### End-of-Life Risk Analysis

Displays detailed EOL-risk inventory information.

### Allocation Recommendations

Displays:

- Store
- City
- Phone
- Category
- Weekly sales
- Current inventory
- Target inventory
- Replenishment need
- Recommended units
- Allocation value
- Priority

### Performance Comparison

Displays the performance of the proposed system against the naive baseline.

### Recommendation Logic

Explains how the system converts historical sales into final inventory recommendations.

---

# 8. Dashboard Filtering

The allocation dashboard supports interactive filtering.

Users can filter recommendations by:

- City
- Category
- Store

The search field can be used to search by:

- Phone
- Store
- City
- Category

The dashboard updates the allocation summary and recommendation table according to the selected filters.

---

# 9. Recommendation Workflow

The overall recommendation workflow is:

```text
Historical Sales
       |
       v
Average Weekly Demand
       |
       v
Two-Week Inventory Target
       |
       v
Current Inventory
       |
       v
Inventory Gap
       |
       v
Store & Product Analysis
       |
       v
Priority Score
       |
       v
Budget-Constrained Allocation
       |
       v
Final Recommendation
```

---

# 10. Allocation Engine

The main allocation logic is implemented in the `AllocationEngine`.

The engine performs the following major operations.

### Step 1 — Build Opportunities

The engine evaluates every relevant store and phone combination.

Products without meaningful historical demand are excluded from replenishment recommendations.

### Step 2 — Calculate Demand

Historical sales records are used to calculate average weekly sales.

### Step 3 — Calculate Target Inventory

The system targets approximately two weeks of demand.

### Step 4 — Calculate Replenishment Need

The system compares target inventory against current inventory.

If:

```text
Current Inventory < Target Inventory
```

a replenishment opportunity is created.

### Step 5 — Calculate Priority

The priority score incorporates:

- Weekly demand
- Store score
- Stockout score
- Lifecycle score
- Replenishment requirement

### Step 6 — Sort Opportunities

Opportunities are sorted by priority score.

### Step 7 — Apply Budget Constraints

The engine allocates inventory while respecting the:

**₹4 Crore chain-wide budget**

### Step 8 — Generate Recommendations

The final output contains the recommended quantity, investment value, priority score, and an explanation for the recommendation.

---

# 11. Store Analysis

Store characteristics are incorporated into the allocation decision.

The store score considers factors such as:

- Income index
- Footfall index
- Store size
- Location type

Premium, mall, and high-attractiveness locations receive additional weighting where applicable.

This prevents the system from treating every store as having identical demand potential.

---

# 12. Product Category Analysis

The system considers phone categories when calculating stockout impact.

Supported categories include examples such as:

- Budget
- Mid-range
- Premium
- Flagship

Different category weights are applied to reflect different stockout impacts.

---

# 13. Product Lifecycle

Product lifecycle information is considered during allocation.

When a phone model has successor information, its fresh-stock priority is reduced.

This helps prevent excessive investment in products that may soon be replaced by newer models.

---

# 14. Explainable Recommendations

Each allocation recommendation contains a reason explaining why the inventory was recommended.

The explanation can include:

- Current stock
- Target stock
- Demand level
- Expected two-week sales value
- Recommended investment

This makes the allocation output easier for business users to understand instead of presenting only a numerical recommendation.

---

# 15. Technology Stack

## Backend

- Java
- Spring Boot
- Spring Data JPA
- REST APIs

## Database

- H2 Database

H2 is used as the project's database.

**MySQL is not required.**

## Frontend

- HTML5
- CSS3
- JavaScript

## Development Environment

- IntelliJ IDEA
- Maven
- JDK

---

# 16. Architecture

The project follows a layered architecture.

```text
                  +---------------------+
                  |    Web Dashboard    |
                  | HTML/CSS/JavaScript |
                  +----------+----------+
                             |
                             | REST API
                             v
                  +---------------------+
                  |     Controllers     |
                  +----------+----------+
                             |
                             v
                  +---------------------+
                  |       Engines       |
                  +----------+----------+
                             |
                             v
                  +---------------------+
                  |     Repositories    |
                  |   Spring Data JPA   |
                  +----------+----------+
                             |
                             v
                  +---------------------+
                  |     H2 Database     |
                  +---------------------+
```

---

# 17. Project Structure

```text
MobiMart
|
+-- src
|   +-- main
|       +-- java
|       |   +-- com
|       |       +-- mobimart
|       |           |
|       |           +-- controller
|       |           |   +-- AllocationController.java
|       |           |   +-- BaselineController.java
|       |           |   +-- ComparisonController.java
|       |           |   +-- EOLController.java
|       |           |   +-- RiskController.java
|       |           |   +-- PerformanceController.java
|       |           |
|       |           +-- engine
|       |           |   +-- AllocationEngine.java
|       |           |   +-- BaselineEngine.java
|       |           |   +-- ...
|       |           |
|       |           +-- model
|       |           |   +-- Store.java
|       |           |   +-- PhoneModel.java
|       |           |   +-- Inventory.java
|       |           |   +-- SalesHistory.java
|       |           |
|       |           +-- repository
|       |               +-- StoreRepository.java
|       |               +-- PhoneModelRepository.java
|       |               +-- InventoryRepository.java
|       |               +-- SalesHistoryRepository.java
|       |
|       +-- resources
|           +-- application.properties
|           +-- static
|               +-- index.html
|
+-- pom.xml
+-- README.md
```

> The exact list of files may vary depending on the final project implementation.

---

# 18. Data Model

The core application data is represented using the following entities.

## Store

Contains store-level information such as:

- Store ID
- Store name
- City
- Location type
- Store size
- Income index
- Footfall index

## PhoneModel

Contains product information such as:

- Phone ID
- Phone name
- Category
- Price
- Successor lifecycle information

## Inventory

Represents the current quantity of a phone model available at a store.

## SalesHistory

Contains historical sales information used to estimate demand.

---

# 19. REST API

The application exposes REST endpoints for the dashboard and analysis modules.

## Allocation API

```http
GET /api/allocation
```

Returns inventory allocation recommendations.

Example response structure:

```json
[
  {
    "store": "MobiMart Indiranagar",
    "city": "Bangalore",
    "phone": "Samsung Mobi 31",
    "category": "Flagship",
    "averageWeeklySales": 10.96,
    "currentInventory": 15,
    "targetInventory": 22,
    "replenishmentNeed": 7,
    "recommendedUnits": 7,
    "pricePerUnit": 64999,
    "allocationValue": 454993,
    "priorityScore": 392770.89,
    "reason": "Replenishment recommendation..."
  }
]
```

## Baseline API

```http
GET /api/baseline
```

Returns inventory recommendations generated using the baseline strategy.

## EOL Risk API

```http
GET /api/eol-risk
```

Returns inventory items identified as being exposed to End-of-Life risk.

## Comparison API

```http
GET /api/comparison
```

Returns performance metrics comparing the proposed system and the naive baseline.

## Performance API

```http
GET /api/performance
```

Returns performance-related inventory optimization metrics.

---

# 20. Running the Project

## Prerequisites

Install:

- JDK
- IntelliJ IDEA
- Maven

No MySQL server is required because the project uses H2 Database.

## Step 1 — Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

Navigate into the project directory:

```bash
cd MobiMart
```

## Step 2 — Open the Project

Open the cloned project in IntelliJ IDEA.

Allow IntelliJ IDEA to import the Maven dependencies.

## Step 3 — Check Database Configuration

The database configuration is located at:

```text
src/main/resources/application.properties
```

The project uses H2 Database.

No separate database installation or server startup is required.

## Step 4 — Run the Spring Boot Application

Run the main Spring Boot application from IntelliJ IDEA.

Alternatively, use Maven:

```bash
mvn spring-boot:run
```

## Step 5 — Open the Dashboard

Once Spring Boot starts successfully, open:

```text
http://localhost:8080
```

The MobiMart Inventory Allocation dashboard should load in the browser.

---

# 21. H2 Database

The project uses H2 as the application database.

This makes the project easy to run because the user does not need to install or configure an external database server.

The application uses Spring Data JPA repositories to interact with the database.

Main repositories include:

```text
StoreRepository
PhoneModelRepository
InventoryRepository
SalesHistoryRepository
```

---

# 22. Example Dashboard Results

A successful application run produces an inventory optimization dashboard containing metrics such as:

```text
Recommendations
Allocation Value
Recommended Units
Average Priority
Chain-wide Inventory Budget
Current Inventory Capital
At-Risk Capital
EOL Risk Items
Transfer Savings
```

The dashboard also provides detailed allocation and EOL-risk tables.

---

# 23. Example Business Interpretation

If a store has:

```text
Current Inventory < Two-Week Target
```

and the phone has strong historical demand, the system identifies it as a replenishment opportunity.

The priority is then influenced by the store's characteristics, product category, stockout impact, lifecycle information, and inventory gap.

The final recommendation is constrained by the available chain-wide budget.

This provides a more structured decision process than simply distributing inventory equally across stores.

---

# 24. Why This Approach?

A simple sales-only allocation strategy can overlook important business factors.

MobiMart improves the decision process by considering:

```text
Demand
  +
Inventory Gap
  +
Store Characteristics
  +
Stockout Impact
  +
Product Lifecycle
  +
Budget Constraints
```

This creates a more practical inventory allocation strategy that balances demand and financial exposure.

---

# 25. Benefits

The system helps retailers:

- Reduce potential stockouts
- Improve inventory availability
- Avoid unnecessary inventory investment
- Prioritize high-value opportunities
- Respect inventory budgets
- Identify EOL-risk inventory
- Reduce potential markdown exposure
- Make transfer decisions using cost comparison
- Understand why an allocation was recommended
- Compare an optimization strategy with a simpler baseline

---

# 26. Limitations

The current implementation is designed as an inventory decision-support system and uses historical data available to the application.

Potential future improvements include:

- More advanced demand forecasting
- Seasonality detection
- Promotional-event effects
- Lead-time-aware replenishment
- Supplier constraints
- Real-time inventory updates
- Automated inter-store transfer optimization
- Machine-learning-based demand prediction
- Multi-period inventory optimization
- User authentication and role-based access

---

# 27. Future Enhancements

Possible future versions could include:

### Advanced Forecasting

Use time-series or machine-learning models to forecast future demand.

### Real-Time Inventory

Integrate with live store inventory systems.

### Automated Transfer Optimization

Automatically determine the best source and destination stores for inventory transfers.

### Supplier Lead Times

Include supplier lead time when calculating replenishment requirements.

### Seasonal Demand

Account for festivals, holidays, launches, and promotional campaigns.

### Interactive Analytics

Add charts for:

- Store demand
- Product performance
- Inventory distribution
- EOL exposure
- Budget utilization
- Historical trends

---

# 28. Conclusion

MobiMart Inventory Allocation is a complete inventory optimization and decision-support application built using Java, Spring Boot, Spring Data JPA, H2 Database, HTML, CSS, and JavaScript.

The system combines demand analysis, inventory gap detection, priority scoring, budget-constrained allocation, End-of-Life risk analysis, transfer decisions, and baseline comparison.

The accompanying dashboard provides an accessible way for business users to understand inventory recommendations, financial exposure, and system performance.

The project demonstrates how software engineering and analytical decision-making can be combined to address a practical retail inventory management problem.

---

# 29. Author

**MobiMart Inventory Allocation Project**

Developed as an inventory optimization and decision-support application using Java and Spring Boot.
