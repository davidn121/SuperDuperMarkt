INSERT INTO food_types (name, minimum_quality, maximum_quality, minimum_shelf_life_in_days, maximum_shelf_life_in_days, daily_quality_change, has_update_daily_price)
VALUES ('Kaese', 30, null, 50, 100, -1, 1);

INSERT INTO food_types (name, minimum_quality, maximum_quality, minimum_shelf_life_in_days, maximum_shelf_life_in_days, daily_quality_change, has_update_daily_price)
VALUES ('Wein', 0, 50, null, null, 0.1, 0);



INSERT INTO products (label, quality, initial_price, expiration_date, FoodType)
SELECT 'GoudaDB' as label, 35 as quality, 4 as initial_price, ADDDATE(NOW(), 55) as expiration_date, ft.id
FROM food_types as ft WHERE ft.name = 'Kaese';

INSERT INTO products (label, quality, initial_price, expiration_date, FoodType)
SELECT 'FetaDB' as label, 31 as quality, 2.5 as initial_price, ADDDATE(NOW(), 80) as expiration_date, ft.id
FROM food_types as ft WHERE ft.name = 'Kaese';

INSERT INTO products (label, quality, initial_price, expiration_date, FoodType)
SELECT 'BrieDB' as label, 25 as quality, 3 as initial_price, ADDDATE(NOW(), 80) as expiration_date, ft.id
FROM food_types as ft WHERE ft.name = 'Kaese';

INSERT INTO products (label, quality, initial_price, expiration_date, FoodType)
SELECT 'CamembertDB' as label, 35 as quality, 5 as initial_price, ADDDATE(NOW(), 105) as expiration_date, ft.id
FROM food_types as ft WHERE ft.name = 'Kaese';

INSERT INTO products (label, quality, initial_price, expiration_date, FoodType)
SELECT 'EmmentalerDB' as label, 35 as quality, 5 as initial_price, ADDDATE(NOW(), 20) as expiration_date, ft.id
FROM food_types as ft WHERE ft.name = 'Kaese';

INSERT INTO products (label, quality, initial_price, expiration_date, FoodType)
SELECT 'RieslingDB' as label, 22 as quality, 6 as initial_price, null as expiration_date, ft.id
FROM food_types as ft WHERE ft.name = 'Wein';

INSERT INTO products (label, quality, initial_price, expiration_date, FoodType)
SELECT 'MerlotDB' as label, 49 as quality, 6.5 as initial_price, null as expiration_date, ft.id
FROM food_types as ft WHERE ft.name = 'Wein';

INSERT INTO products (label, quality, initial_price, expiration_date, FoodType)
SELECT 'ChampagneDB' as label, -3 as quality, 4 as initial_price, null as expiration_date, ft.id
FROM food_types as ft WHERE ft.name = 'Wein';

INSERT INTO products (label, quality, initial_price, expiration_date, FoodType)
SELECT 'MalbecDB' as label, 55 as quality, 5 as initial_price, null as expiration_date, ft.id
FROM food_types as ft WHERE ft.name = 'Wein';

INSERT INTO products (label, quality, initial_price, expiration_date, FoodType)
SELECT 'RotlingDB' as label, 15 as quality, 3 as initial_price, ADDDATE(NOW(), 20) as expiration_date, ft.id
FROM food_types as ft WHERE ft.name = 'Wein';