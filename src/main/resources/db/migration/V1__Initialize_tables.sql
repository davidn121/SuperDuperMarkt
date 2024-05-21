CREATE TABLE food_types (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    minimum_quality INT(11) DEFAULT NULL,
    maximum_quality INT(11) DEFAULT NULL,
    minimum_shelf_life_in_days INT(11) DEFAULT NULL,
    maximum_shelf_life_in_days INT(11) DEFAULT NULL,
    daily_quality_change DECIMAL(19, 2) NOT NULL,
    has_update_daily_price TINYINT(1) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE products (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    label VARCHAR(255) NOT NULL,
    quality INT(11) NOT NULL,
    initial_price DECIMAL(19, 2) NOT NULL,
    expiration_date DATE DEFAULT NULL,
    FoodType BIGINT(20) NOT NULL,
    PRIMARY KEY (id),
    INDEX FK_products_FoodType (FoodType),
    CONSTRAINT FK_products_FoodType FOREIGN KEY (FoodType) REFERENCES food_types (id) ON UPDATE RESTRICT ON DELETE RESTRICT
);
