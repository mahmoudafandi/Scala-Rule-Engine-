
Create database if not exists Rule ;

Use Rule 

;
CREATE TABLE RuleEngine (
    timestamp     DATETIME      NOT NULL,
    productName   VARCHAR(80)   NOT NULL,
    expiryDate    DATE          NOT NULL,
    quantity      INT           NOT NULL,
    unitPrice     DECIMAL(10,2) NOT NULL,
    channel       VARCHAR(30)   NOT NULL,
    paymentMethod VARCHAR(20)   NOT NULL,
    discount      DECIMAL(10,2) NOT NULL,
    finalPrice    DECIMAL(10,2) NOT NULL
);