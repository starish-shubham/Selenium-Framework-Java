selenium-e2e-framework/
│
├── drivers/✅                      ✅  # local browser drivers (offline-critical)
│   ├── chromedriver.exe✅
│   ├── geckodriver.exe✅
│   └── geckodriver.exe✅
│
├── src/
│   ├── main/java/com/yourcompany/
│   │   ├── core/ ✅                 # merged: driver + base + config
│   │   │   ├── DriverFactory.java ✅
│   │   │   ├── BaseTest.java ✅
│   │   │   ├── BasePage.java ✅
│   │   │   └── ConfigReader.java✅
│   │   │
│   │   ├── pages/                  # one file per screen
│   │   │   ├── LoginPage.java
│   │   │   └── DashboardPage.java
│   │   │
│   │   └── utils/                  # everything else reusable
│   │       ├── WaitUtils.java
│   │       ├── ScreenshotUtils.java
│   │       └── DataUtils.java      # merged Excel+Json readers
│   │
│   └── test/java/com/yourcompany/tests/
│       ├── LoginTest.java
│       └── CheckoutTest.java
│
├── config/                         # merged, top-level, easy to find
│   ├── config-qa.properties
│   └── config-prod.properties
│
├── testdata/✅         # merged, top-level
│   ├── login_data.json
│   └── users.xlsx
│
├── reports/                        # all output lives here (gitignored)
├── logs/
│
├── pom.xml ✅
├── testng.xml ✅
├── .gitignore ✅
└── README.md ✅