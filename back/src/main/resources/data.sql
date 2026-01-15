-- ======================================================================================
-- 1. CLEANUP & SCHEMA REBUILD
-- ======================================================================================
SET
FOREIGN_KEY_CHECKS = 0;
SET
SQL_SAFE_UPDATES = 0;

-- Drop all tables to ensure a clean slate
DROP TABLE IF EXISTS `pc_case_coolers`;
DROP TABLE IF EXISTS `pcs`;
DROP TABLE IF EXISTS `component_placement`;
DROP TABLE IF EXISTS `component_links`;
DROP TABLE IF EXISTS `component_price`;
DROP TABLE IF EXISTS `cpu`;
DROP TABLE IF EXISTS `gpu`;
DROP TABLE IF EXISTS `motherboard`;
DROP TABLE IF EXISTS `ram_kit`;
DROP TABLE IF EXISTS `storage`;
DROP TABLE IF EXISTS `psu`;
DROP TABLE IF EXISTS `case`;
DROP TABLE IF EXISTS `cpu_cooler`;
DROP TABLE IF EXISTS `case_cooler`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `refresh_token`;

-- ======================================================================================
-- 2. CREATE TABLES
-- ======================================================================================

CREATE TABLE `user`
(
    `id`        bigint       NOT NULL AUTO_INCREMENT,
    `email`     varchar(255) NOT NULL,
    `google_id` varchar(255) DEFAULT NULL,
    `password`  varchar(255) DEFAULT NULL,
    `role`      enum('ADMIN','USER') DEFAULT NULL,
    `username`  varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_email` (`email`),
    UNIQUE KEY `UK_username` (`username`)
) ENGINE=InnoDB;

CREATE TABLE `cpu`
(
    `id`              bigint NOT NULL AUTO_INCREMENT,
    `cpu_brand`       varchar(255) DEFAULT NULL,
    `cpu_model`       varchar(255) DEFAULT NULL,
    `cpu_socket_type` varchar(255) DEFAULT NULL,
    `cpu_cores`       int          DEFAULT NULL,
    `cpu_threads`     int          DEFAULT NULL,
    `cpu_base_clock_ghz` double DEFAULT NULL,
    `cpu_boost_clock_ghz` double DEFAULT NULL,
    `power_draw`      int          DEFAULT NULL,
    `picture_url`     varchar(255) DEFAULT NULL,
    `width` double DEFAULT NULL,
    `length` double DEFAULT NULL,
    `height` double DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `gpu`
(
    `id`            bigint NOT NULL AUTO_INCREMENT,
    `gpu_brand`     varchar(255) DEFAULT NULL,
    `gpu_model`     varchar(255) DEFAULT NULL,
    `gpu_memory_gb` int          DEFAULT NULL,
    `power_draw`    int          DEFAULT NULL,
    `picture_url`   varchar(255) DEFAULT NULL,
    `width` double DEFAULT NULL,
    `length` double DEFAULT NULL,
    `height` double DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `motherboard`
(
    `id`                bigint NOT NULL AUTO_INCREMENT,
    `motherboard_brand` varchar(255) DEFAULT NULL,
    `motherboard_model` varchar(255) DEFAULT NULL,
    `chipset`           varchar(255) DEFAULT NULL,
    `socket_type`       varchar(255) DEFAULT NULL,
    `memory_type`       varchar(255) DEFAULT NULL,
    `power_draw`        int          DEFAULT NULL,
    `picture_url`       varchar(255) DEFAULT NULL,
    `width` double DEFAULT NULL,
    `length` double DEFAULT NULL,
    `height` double DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `ram_kit`
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `brand`            varchar(255) DEFAULT NULL,
    `model`            varchar(255) DEFAULT NULL,
    `ram_type`         varchar(255) DEFAULT NULL,
    `ram_size_gb`      int          DEFAULT NULL,
    `ram_speed_mhz`    int          DEFAULT NULL,
    `ram_sticks_count` int          DEFAULT NULL,
    `power_draw`       int          DEFAULT NULL,
    `picture_url`      varchar(255) DEFAULT NULL,
    `width` double DEFAULT NULL,
    `length` double DEFAULT NULL,
    `height` double DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `storage`
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `brand`            varchar(255) DEFAULT NULL,
    `model`            varchar(255) DEFAULT NULL,
    `storage_type`     varchar(255) DEFAULT NULL,
    `storage_total_gb` int          DEFAULT NULL,
    `storage_count`    int          DEFAULT NULL,
    `power_draw`       int          DEFAULT NULL,
    `picture_url`      varchar(255) DEFAULT NULL,
    `width` double DEFAULT NULL,
    `length` double DEFAULT NULL,
    `height` double DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `psu`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `psu_model`   varchar(255) DEFAULT NULL,
    `psu_wattage` int          DEFAULT NULL,
    `power_draw`  int          DEFAULT NULL,
    `picture_url` varchar(255) DEFAULT NULL,
    `width` double DEFAULT NULL,
    `length` double DEFAULT NULL,
    `height` double DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `case`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `case_model`  varchar(255) DEFAULT NULL,
    `case_color`  varchar(255) DEFAULT NULL,
    `rgb_setup`   varchar(255) DEFAULT NULL,
    `power_draw`  int          DEFAULT NULL,
    `picture_url` varchar(255) DEFAULT NULL,
    `width` double DEFAULT NULL,
    `length` double DEFAULT NULL,
    `height` double DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `case_cooler`
(
    `id`            bigint NOT NULL AUTO_INCREMENT,
    `brand`         varchar(255) DEFAULT NULL,
    `model`         varchar(255) DEFAULT NULL,
    `fan_size`      int          DEFAULT NULL,
    `cooling_color` tinyblob,
    `power_draw`    int          DEFAULT NULL,
    `picture_url`   varchar(255) DEFAULT NULL,
    `width` double DEFAULT NULL,
    `length` double DEFAULT NULL,
    `height` double DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `cpu_cooler`
(
    `id`                         bigint NOT NULL AUTO_INCREMENT,
    `brand`                      varchar(255) DEFAULT NULL,
    `model`                      varchar(255) DEFAULT NULL,
    `cooling_type`               tinyint      DEFAULT NULL,
    `cpu_socket`                 varchar(255) DEFAULT NULL,
    `fan_count`                  int          DEFAULT NULL,
    `cooling_color`              tinyblob,
    `case_cooler_slots_required` int          DEFAULT NULL,
    `pccase_type`                varchar(255) DEFAULT NULL,
    `maxtdp`                     bigint       DEFAULT NULL,
    `power_draw`                 int          DEFAULT NULL,
    `picture_url`                varchar(255) DEFAULT NULL,
    `width` double DEFAULT NULL,
    `length` double DEFAULT NULL,
    `height` double DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `component_placement`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT,
    `case_id`        bigint DEFAULT NULL,
    `component_type` varchar(255) NOT NULL,
    `width` double DEFAULT NULL,
    `length` double DEFAULT NULL,
    `height` double DEFAULT NULL,
    `x` double DEFAULT NULL,
    `y` double DEFAULT NULL,
    `z` double DEFAULT NULL,
    `rotation` double DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY              `FK_placement_case` (`case_id`),
    CONSTRAINT `FK_placement_case` FOREIGN KEY (`case_id`) REFERENCES `case` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `component_links`
(
    `id`             bigint NOT NULL AUTO_INCREMENT,
    `component_type` varchar(255) DEFAULT NULL,
    `component_id`   bigint       DEFAULT NULL,
    `url`            varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `component_price`
(
    `id`             bigint NOT NULL AUTO_INCREMENT,
    `price_value` double DEFAULT NULL,
    `time`           date         DEFAULT NULL,
    `location`       varchar(255) DEFAULT NULL,
    `component_type` varchar(255) DEFAULT NULL,
    `component_id`   bigint       DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_price` (`component_id`,`component_type`,`time`)
) ENGINE=InnoDB;

CREATE TABLE `pcs`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT,
    `name`           varchar(255) NOT NULL,
    `description`    varchar(255) DEFAULT NULL,
    `purpose`        varchar(255) DEFAULT NULL,
    `owner_id`       bigint       NOT NULL,
    `case_id`        bigint       DEFAULT NULL,
    `cpu_id`         bigint       DEFAULT NULL,
    `gpu_id`         bigint       DEFAULT NULL,
    `motherboard_id` bigint       DEFAULT NULL,
    `ramkit_id`      bigint       DEFAULT NULL,
    `storage_id`     bigint       DEFAULT NULL,
    `psu_id`         bigint       DEFAULT NULL,
    `cpu_cooler_id`  bigint       DEFAULT NULL,
    `benchmark_score` double DEFAULT NULL,
    `temperature_idlec` double DEFAULT NULL,
    `temperature_loadc` double DEFAULT NULL,
    `noise_level_db` double DEFAULT NULL,
    `estimated_value_usd` double DEFAULT NULL,
    `total_power_draww` double DEFAULT NULL,
    `location`       varchar(255) DEFAULT NULL,
    `visibility`     enum('HIDDEN','PRIVATE','PUBLIC') DEFAULT NULL,
    `created_at`     datetime(6) DEFAULT NULL,
    `updated_at`     datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY              `FK_pc_owner` (`owner_id`),
    CONSTRAINT `FK_pc_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`),
    CONSTRAINT `FK_pc_motherboard` FOREIGN KEY (`motherboard_id`) REFERENCES `motherboard` (`id`)
) ENGINE=InnoDB;

-- ======================================================================================
-- NEW TABLE: pc_tags (Required for the @ElementCollection in PC entity)
-- ======================================================================================
CREATE TABLE `pc_tags` (
                           `pc_id` bigint NOT NULL,
                           `tags` varchar(255) DEFAULT NULL,
                           KEY `FK_pc_tags_pc` (`pc_id`),
                           CONSTRAINT `FK_pc_tags_pc` FOREIGN KEY (`pc_id`) REFERENCES `pcs` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `pc_case_coolers`
(
    `pc_id`          bigint NOT NULL,
    `case_cooler_id` bigint NOT NULL,
    KEY              `FK_join_pc` (`pc_id`),
    KEY              `FK_join_cooler` (`case_cooler_id`),
    CONSTRAINT `FK_join_cooler` FOREIGN KEY (`case_cooler_id`) REFERENCES `case_cooler` (`id`),
    CONSTRAINT `FK_join_pc` FOREIGN KEY (`pc_id`) REFERENCES `pcs` (`id`)
) ENGINE=InnoDB;

SET
FOREIGN_KEY_CHECKS = 1;
SET
SQL_SAFE_UPDATES = 1;

-- ======================================================================================
-- 3. INSERT COMPONENTS
-- ======================================================================================

-- A. CPU
INSERT INTO cpu (cpu_brand, cpu_model, cpu_socket_type, cpu_cores, cpu_threads, cpu_base_clock_ghz, cpu_boost_clock_ghz,
                 power_draw, picture_url, width, length, height)
VALUES ('Intel', 'Core i9-13900K', 'LGA1700', 24, 32, 3.0, 5.8, 253, 'https://example.com/cpu/i9-13900k.png', 35.0,
        35.0, 4.0),
       ('Intel', 'Core i7-13700K', 'LGA1700', 16, 24, 3.4, 5.4, 253, 'https://example.com/cpu/i7-13700k.png', 35.0,
        35.0, 4.0),
       ('Intel', 'Core i5-13600K', 'LGA1700', 14, 20, 3.5, 5.1, 181, 'https://example.com/cpu/i5-13600k.png', 35.0,
        35.0, 4.0),
       ('AMD', 'Ryzen 9 7950X', 'AM5', 16, 32, 4.5, 5.7, 170, 'https://example.com/cpu/7950x.png', 40.0, 40.0, 4.0),
       ('AMD', 'Ryzen 7 7800X3D', 'AM5', 8, 16, 4.2, 5.0, 120, 'https://example.com/cpu/7800x3d.png', 40.0, 40.0, 4.0),
       ('AMD', 'Ryzen 5 7600X', 'AM5', 6, 12, 4.7, 5.3, 105, 'https://example.com/cpu/7600x.png', 40.0, 40.0, 4.0),
       ('AMD', 'Ryzen 9 5950X', 'AM4', 16, 32, 3.4, 4.9, 105, 'https://example.com/cpu/5950x.png', 40.0, 40.0, 4.0),
       ('Intel', 'Core i9-12900K', 'LGA1700', 16, 24, 3.2, 5.2, 241, 'https://example.com/cpu/i9-12900k.png', 35.0,
        35.0, 4.0),
       ('Intel', 'Core i5-12400F', 'LGA1700', 6, 12, 2.5, 4.4, 65, 'https://example.com/cpu/i5-12400f.png', 35.0, 35.0,
        4.0),
       ('AMD', 'Ryzen 5 5600X', 'AM4', 6, 12, 3.7, 4.6, 65, 'https://example.com/cpu/5600x.png', 40.0, 40.0, 4.0);

-- B. GPU
INSERT INTO gpu (gpu_brand, gpu_model, gpu_memory_gb, power_draw, picture_url, width, length, height)
VALUES ('NVIDIA', 'GeForce RTX 4090', 24, 450, 'https://example.com/gpu/4090.png', 140.0, 340.0, 60.0),
       ('NVIDIA', 'GeForce RTX 4080', 16, 320, 'https://example.com/gpu/4080.png', 140.0, 330.0, 55.0),
       ('NVIDIA', 'GeForce RTX 4070 Ti', 12, 285, 'https://example.com/gpu/4070.png', 130.0, 300.0, 50.0),
       ('AMD', 'Radeon RX 7900 XTX', 24, 355, 'https://example.com/gpu/7900xtx.png', 135.0, 330.0, 55.0),
       ('AMD', 'Radeon RX 7900 XT', 20, 315, 'https://example.com/gpu/7900xt.png', 135.0, 320.0, 50.0),
       ('NVIDIA', 'GeForce RTX 3080', 10, 320, 'https://example.com/gpu/3080.png', 130.0, 300.0, 50.0),
       ('NVIDIA', 'GeForce RTX 3060', 12, 170, 'https://example.com/gpu/3060.png', 120.0, 250.0, 40.0),
       ('AMD', 'Radeon RX 6700 XT', 12, 230, 'https://example.com/gpu/6700.png', 120.0, 270.0, 45.0),
       ('NVIDIA', 'GeForce RTX 4060', 8, 115, 'https://example.com/gpu/4060.png', 110.0, 240.0, 40.0),
       ('AMD', 'Radeon RX 6600', 8, 132, 'https://example.com/gpu/6600.png', 110.0, 220.0, 40.0);

-- C. Motherboard
INSERT INTO motherboard (motherboard_brand, motherboard_model, chipset, socket_type, memory_type, power_draw,
                         picture_url, width, length, height)
VALUES ('ASUS', 'ROG MAXIMUS Z790 HERO', 'Z790', 'LGA1700', 'DDR5', 60, 'https://example.com/mb/z790.png', 244.0, 305.0,
        30.0),
       ('MSI', 'MAG Z790 TOMAHAWK WIFI', 'Z790', 'LGA1700', 'DDR5', 50, 'https://example.com/mb/tomahawk.png', 244.0,
        305.0, 30.0),
       ('Gigabyte', 'B760 AORUS ELITE', 'B760', 'LGA1700', 'DDR4', 45, 'https://example.com/mb/b760.png', 244.0, 305.0,
        30.0),
       ('ASUS', 'ROG CROSSHAIR X670E HERO', 'X670E', 'AM5', 'DDR5', 70, 'https://example.com/mb/x670e.png', 244.0,
        305.0, 30.0),
       ('MSI', 'MPG B650 EDGE WIFI', 'B650', 'AM5', 'DDR5', 50, 'https://example.com/mb/b650.png', 244.0, 305.0, 30.0),
       ('ASRock', 'X670E Taichi', 'X670E', 'AM5', 'DDR5', 65, 'https://example.com/mb/taichi.png', 260.0, 305.0, 30.0),
       ('ASUS', 'ROG STRIX B550-F', 'B550', 'AM4', 'DDR4', 45, 'https://example.com/mb/b550.png', 244.0, 305.0, 30.0),
       ('MSI', 'MEG X570 UNIFY', 'X570', 'AM4', 'DDR4', 55, 'https://example.com/mb/x570.png', 244.0, 305.0, 30.0),
       ('Gigabyte', 'Z690 GAMING X', 'Z690', 'LGA1700', 'DDR5', 50, 'https://example.com/mb/z690.png', 244.0, 305.0,
        30.0),
       ('ASUS', 'TUF GAMING B550-PLUS', 'B550', 'AM4', 'DDR4', 40, 'https://example.com/mb/tuf.png', 244.0, 305.0,
        30.0);

-- D. RAM Kit (UPDATED with Brand & Model columns)
INSERT INTO ram_kit (brand, model, ram_type, ram_size_gb, ram_speed_mhz, ram_sticks_count, power_draw, picture_url,
                     width, length, height)
VALUES ('Corsair', 'Vengeance', 'DDR5', 32, 6000, 2, 10, 'https://example.com/ram/corsair32.png', 8.0, 133.0, 35.0),
       ('G.Skill', 'Trident Z5', 'DDR5', 64, 6400, 2, 12, 'https://example.com/ram/gskill64.png', 8.0, 133.0, 42.0),
       ('Kingston', 'Fury Beast', 'DDR5', 16, 5600, 2, 8, 'https://example.com/ram/kingston16.png', 8.0, 133.0, 34.0),
       ('Corsair', 'Vengeance LPX', 'DDR4', 32, 3600, 2, 8, 'https://example.com/ram/vengeance.png', 7.0, 133.0, 32.0),
       ('G.Skill', 'Ripjaws V', 'DDR4', 16, 3200, 2, 6, 'https://example.com/ram/ripjaws.png', 7.0, 133.0, 42.0),
       ('Crucial', 'Pro', 'DDR5', 32, 5600, 2, 10, 'https://example.com/ram/crucial.png', 8.0, 133.0, 32.0),
       ('TeamGroup', 'T-Force Delta', 'DDR5', 32, 6000, 2, 11, 'https://example.com/ram/tforce.png', 8.0, 133.0, 46.0),
       ('Corsair', 'Dominator Platinum', 'DDR5', 64, 6200, 2, 14, 'https://example.com/ram/dominator.png', 8.0, 133.0,
        55.0),
       ('Kingston', 'Fury Renegade', 'DDR4', 32, 3600, 2, 9, 'https://example.com/ram/renegade.png', 8.0, 133.0, 42.0),
       ('XPG', 'Lancer', 'DDR5', 32, 6000, 2, 10, 'https://example.com/ram/adata.png', 8.0, 133.0, 40.0);

-- E. Storage (UPDATED with Brand & Model columns)
INSERT INTO storage (brand, model, storage_type, storage_total_gb, storage_count, power_draw, picture_url, width,
                     length, height)
VALUES ('Samsung', '990 Pro', 'NVMe', 2000, 1, 5, 'https://example.com/storage/990pro.png', 22.0, 80.0, 2.0),
       ('WD', 'Black SN850X', 'NVMe', 1000, 1, 5, 'https://example.com/storage/sn850x.png', 22.0, 80.0, 2.0),
       ('Crucial', 'P3 Plus', 'NVMe', 1000, 1, 4, 'https://example.com/storage/p3plus.png', 22.0, 80.0, 2.0),
       ('Samsung', '970 Evo Plus', 'NVMe', 500, 1, 4, 'https://example.com/storage/970evo.png', 22.0, 80.0, 2.0),
       ('Seagate', 'Barracuda', 'HDD', 4000, 1, 10, 'https://example.com/storage/barracuda.png', 101.0, 147.0, 26.0),
       ('WD', 'Blue', 'HDD', 2000, 1, 8, 'https://example.com/storage/wdblue.png', 101.0, 147.0, 26.0),
       ('Samsung', '870 Evo', 'SSD', 1000, 1, 3, 'https://example.com/storage/870evo.png', 70.0, 100.0, 7.0),
       ('Kingston', 'NV2', 'NVMe', 1000, 1, 4, 'https://example.com/storage/nv2.png', 22.0, 80.0, 2.0),
       ('Sabrent', 'Rocket 4', 'NVMe', 2000, 1, 6, 'https://example.com/storage/rocket4.png', 22.0, 80.0, 2.0),
       ('Crucial', 'MX500', 'SSD', 2000, 1, 3, 'https://example.com/storage/mx500.png', 70.0, 100.0, 7.0);

-- F. PSU
INSERT INTO psu (psu_model, psu_wattage, power_draw, picture_url, width, length, height)
VALUES ('Corsair RM1000x', 1000, 0, 'https://example.com/psu/rm1000.png', 150.0, 180.0, 86.0),
       ('Corsair RM850x', 850, 0, 'https://example.com/psu/rm850.png', 150.0, 160.0, 86.0),
       ('Seasonic Vertex', 1000, 0, 'https://example.com/psu/vertex.png', 150.0, 160.0, 86.0),
       ('EVGA SuperNOVA', 750, 0, 'https://example.com/psu/evga.png', 150.0, 150.0, 86.0),
       ('MSI MPG A1000G', 1000, 0, 'https://example.com/psu/msi.png', 150.0, 150.0, 86.0),
       ('Be Quiet! Dark Power 13', 850, 0, 'https://example.com/psu/dp13.png', 150.0, 175.0, 86.0),
       ('Thermaltake GF3', 1000, 0, 'https://example.com/psu/tt.png', 150.0, 160.0, 86.0),
       ('Corsair RM750e', 750, 0, 'https://example.com/psu/rm750.png', 150.0, 140.0, 86.0),
       ('Cooler Master V850', 850, 0, 'https://example.com/psu/cm.png', 150.0, 160.0, 86.0),
       ('Seasonic Focus', 750, 0, 'https://example.com/psu/focus.png', 150.0, 140.0, 86.0);

-- G. CPU Cooler (UPDATED with Brand & Model columns)
INSERT INTO cpu_cooler (brand, model, cooling_type, cpu_socket, fan_count, case_cooler_slots_required, pccase_type,
                        maxtdp, power_draw, picture_url, width, length, height)
VALUES ('NZXT', 'Kraken Elite 360', 0, 'LGA1700', 3, 3, 'ATX', 300, 15, 'https://example.com/cooler/kraken360.png',
        120.0, 394.0, 52.0),
       ('Corsair', 'iCUE H150i Elite', 0, 'LGA1700', 3, 3, 'ATX', 280, 15, 'https://example.com/cooler/h150i.png',
        120.0, 397.0, 52.0),
       ('Arctic', 'Liquid Freezer III', 0, 'AM5', 3, 3, 'ATX', 320, 15, 'https://example.com/cooler/lf3-360.png', 120.0,
        398.0, 65.0),
       ('DeepCool', 'LS720', 0, 'AM5', 3, 3, 'ATX', 300, 15, 'https://example.com/cooler/ls720.png', 120.0, 402.0,
        52.0),
       ('Noctua', 'NH-D15', 1, 'AM5', 2, 0, 'ATX', 220, 5, 'https://example.com/cooler/nhd15.png', 150.0, 161.0, 165.0),
       ('Be Quiet!', 'Dark Rock Pro 4', 1, 'LGA1700', 1, 0, 'ATX', 250, 5, 'https://example.com/cooler/drp4.png', 136.0,
        146.0, 163.0),
       ('DeepCool', 'AK620', 1, 'AM4', 2, 0, 'ATX', 260, 5, 'https://example.com/cooler/ak620.png', 129.0, 138.0,
        160.0),
       ('Cooler Master', 'Hyper 212', 1, 'LGA1700', 1, 0, 'ATX', 180, 4, 'https://example.com/cooler/hyper212.png',
        120.0, 80.0, 158.0),
       ('NZXT', 'Kraken 240', 0, 'AM4', 2, 2, 'ATX', 240, 10, 'https://example.com/cooler/nzxt240.png', 120.0, 275.0,
        52.0),
       ('Arctic', 'Liquid Freezer II 240', 0, 'LGA1700', 2, 2, 'ATX', 250, 10,
        'https://example.com/cooler/arctic240.png', 120.0, 277.0, 65.0);

-- H. Case Cooler (UPDATED: 3x 120mm, 3x 100mm, 3x 140mm)
INSERT INTO case_cooler (brand, model, fan_size, power_draw, picture_url, width, length, height, cooling_color)
VALUES ('Corsair', 'LL120 RGB', 120, 3, 'https://example.com/fan/ll120.png', 120.0, 120.0, 25.0, NULL),
       ('Noctua', 'NF-A12x25', 120, 2, 'https://example.com/fan/nfa12.png', 120.0, 120.0, 25.0, NULL),
       ('Lian Li', 'Uni Fan SL120', 120, 4, 'https://example.com/fan/sl120.png', 120.0, 120.0, 25.0, NULL),
       ('Scythe', 'Kaze Jyu Slim', 100, 2, 'https://example.com/fan/kaze100.png', 100.0, 100.0, 12.0, NULL),
       ('Akasa', 'Slim Fan 100', 100, 2, 'https://example.com/fan/akasa100.png', 100.0, 100.0, 15.0, NULL),
       ('Generic', 'Industrial 100mm', 100, 5, 'https://example.com/fan/gen100.png', 100.0, 100.0, 25.0, NULL),
       ('Corsair', 'QL140 RGB', 140, 4, 'https://example.com/fan/ql140.png', 140.0, 140.0, 25.0, NULL),
       ('Be Quiet!', 'Silent Wings 4', 140, 3, 'https://example.com/fan/sw4-140.png', 140.0, 140.0, 25.0, NULL),
       ('Arctic', 'P14 PWM PST', 140, 2, 'https://example.com/fan/p14.png', 140.0, 140.0, 27.0, NULL);

-- I. Case
INSERT INTO `case` (case_model, case_color, rgb_setup, power_draw, picture_url, width, length, height)
VALUES ('Lian Li O11 Dynamic EVO', 'White', 'None', 0, 'https://example.com/case/o11evo.png', 285.0, 465.0, 459.0),
       ('NZXT H9 Flow', 'Black', 'None', 0, 'https://example.com/case/h9flow.png', 290.0, 466.0, 495.0),
       ('Corsair 4000D Airflow', 'Black', 'None', 0, 'https://example.com/case/4000d.png', 230.0, 453.0, 466.0),
       ('Fractal Design North', 'Wood/White', 'None', 0, 'https://example.com/case/north.png', 215.0, 447.0, 469.0),
       ('Hyte Y60', 'Red', 'None', 0, 'https://example.com/case/y60.png', 285.0, 456.0, 462.0),
       ('Phanteks NV7', 'Black', 'ARGB', 10, 'https://example.com/case/nv7.png', 253.0, 532.0, 586.0),
       ('Be Quiet! Pure Base 500DX', 'White', 'ARGB', 5, 'https://example.com/case/500dx.png', 232.0, 450.0, 463.0),
       ('Cooler Master MasterBox TD500', 'Black', 'ARGB', 5, 'https://example.com/case/td500.png', 217.0, 493.0, 469.0),
       ('Lian Li Lancool 216', 'Black', 'ARGB', 5, 'https://example.com/case/lancool216.png', 235.0, 480.0, 491.0),
       ('Fractal Design Torrent', 'Black', 'None', 0, 'https://example.com/case/torrent.png', 242.0, 544.0, 530.0);

-- ======================================================================================
-- 3. INSERT COMPONENT PLACEMENTS
-- ======================================================================================

INSERT INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height)
SELECT id,
       'MotherBoard',
       0,
       0,
       0,
       0,
       305.0,
       244.0,
       50.0
FROM `case`;

INSERT INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height)
SELECT id,
       'PSU',
       0,
       0,
       0,
       0,
       150.0,
       200.0,
       86.0
FROM `case`;

-- ⚠️ FIXED: GPU Placement inserted for every case
INSERT INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height)
SELECT id,
       'GPU',
       0,
       0,
       0,
       0,
       160.0,
       450.0,
       90.0
FROM `case`;

INSERT INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height)
SELECT id,
       'RAMKit',
       0,
       0,
       0,
       0,
       50.0,
       150.0,
       60.0
FROM `case`;

INSERT INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height)
SELECT id,
       'Storage',
       0,
       0,
       0,
       0,
       100.0,
       150.0,
       25.0
FROM `case`;

INSERT INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height)
SELECT id,
       'CPUCooler',
       0,
       0,
       0,
       0,
       200.0,
       200.0,
       190.0
FROM `case`;

-- 6 Case Fans
INSERT INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height)
SELECT id,
       'CaseCooler',
       -1,
       0,
       0,
       0,
       120.0,
       120.0,
       30.0
FROM `case`;
INSERT INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height)
SELECT id,
       'CaseCooler',
       -1,
       0,
       1,
       0,
       120.0,
       120.0,
       30.0
FROM `case`;
INSERT INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height)
SELECT id,
       'CaseCooler',
       -1,
       0,
       2,
       0,
       120.0,
       120.0,
       30.0
FROM `case`;
INSERT INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height)
SELECT id,
       'CaseCooler',
       0,
       -1,
       0,
       0,
       120.0,
       120.0,
       30.0
FROM `case`;
INSERT INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height)
SELECT id,
       'CaseCooler',
       0,
       -1,
       1,
       0,
       120.0,
       120.0,
       30.0
FROM `case`;
INSERT INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height)
SELECT id,
       'CaseCooler',
       0,
       -1,
       2,
       0,
       120.0,
       120.0,
       30.0
FROM `case`;

-- ======================================================================================
-- 4. INSERT PRICES & LINKS
-- ======================================================================================

INSERT INTO component_links (component_type, component_id, url)
SELECT 'CPU', id, CONCAT('https://shop.com/cpu/', id)
FROM cpu;
INSERT INTO component_price (component_type, component_id, price_value, time, location)
SELECT 'CPU', id, 200 + (id * 10), '2023-01-01', 'US'
FROM cpu;

INSERT INTO component_links (component_type, component_id, url)
SELECT 'GPU', id, CONCAT('https://shop.com/gpu/', id)
FROM gpu;
INSERT INTO component_price (component_type, component_id, price_value, time, location)
SELECT 'GPU', id, 400 + (id * 20), '2023-01-01', 'US'
FROM gpu;

INSERT INTO component_links (component_type, component_id, url)
SELECT 'MotherBoard', id, CONCAT('https://shop.com/mb/', id)
FROM motherboard;
INSERT INTO component_price (component_type, component_id, price_value, time, location)
SELECT 'MotherBoard', id, 150 + (id * 5), '2023-01-01', 'US'
FROM motherboard;

INSERT INTO component_links (component_type, component_id, url)
SELECT 'RAMKit', id, CONCAT('https://shop.com/ram/', id)
FROM ram_kit;
INSERT INTO component_price (component_type, component_id, price_value, time, location)
SELECT 'RAMKit', id, 80 + (id * 2), '2023-01-01', 'US'
FROM ram_kit;

INSERT INTO component_links (component_type, component_id, url)
SELECT 'Storage', id, CONCAT('https://shop.com/storage/', id)
FROM storage;
INSERT INTO component_price (component_type, component_id, price_value, time, location)
SELECT 'Storage', id, 60 + (id * 5), '2023-01-01', 'US'
FROM storage;

INSERT INTO component_links (component_type, component_id, url)
SELECT 'PSU', id, CONCAT('https://shop.com/psu/', id)
FROM psu;
INSERT INTO component_price (component_type, component_id, price_value, time, location)
SELECT 'PSU', id, 100 + (id * 5), '2023-01-01', 'US'
FROM psu;

INSERT INTO component_links (component_type, component_id, url)
SELECT 'CPUCooler', id, CONCAT('https://shop.com/cpucooler/', id)
FROM cpu_cooler;
INSERT INTO component_price (component_type, component_id, price_value, time, location)
SELECT 'CPUCooler', id, 50 + (id * 5), '2023-01-01', 'US'
FROM cpu_cooler;

INSERT INTO component_links (component_type, component_id, url)
SELECT 'CaseCooler', id, CONCAT('https://shop.com/casecooler/', id)
FROM case_cooler;
INSERT INTO component_price (component_type, component_id, price_value, time, location)
SELECT 'CaseCooler', id, 15 + id, '2023-01-01', 'US'
FROM case_cooler;

INSERT INTO component_links (component_type, component_id, url)
SELECT 'Case', id, CONCAT('https://shop.com/case/', id)
FROM `case`;
INSERT INTO component_price (component_type, component_id, price_value, time, location)
SELECT 'Case', id, 90 + (id * 10), '2023-01-01', 'US'
FROM `case`;