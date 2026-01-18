SET FOREIGN_KEY_CHECKS = 0;
SET SQL_SAFE_UPDATES = 0;

INSERT IGNORE INTO `case` (id, case_model, picture_url, power_draw, width, length, height) VALUES
(1, 'Lian Li O11 Dynamic Evo', 'https://m.media-amazon.com/images/I/61b+N6Rj0xL.jpg', 0, 285.0, 465.0, 459.0),
(2, 'NZXT H9 Flow', 'https://m.media-amazon.com/images/I/61+U1f-0eZL.jpg', 0, 290.0, 466.0, 495.0),
(3, 'Corsair 4000D Airflow', 'https://m.media-amazon.com/images/I/71O2RkC+7LL.jpg', 0, 230.0, 453.0, 466.0),
(4, 'Fractal Design North', 'https://m.media-amazon.com/images/I/71X8k7+7LL.jpg', 0, 215.0, 447.0, 469.0),
(5, 'NZXT H5 Flow', 'https://m.media-amazon.com/images/I/61p-k+7LL.jpg', 0, 227.0, 446.0, 464.0),
(6, 'Phanteks NV7', 'https://m.media-amazon.com/images/I/71+j+7LL.jpg', 0, 253.0, 532.0, 586.0),
(7, 'Hyte Y60', 'https://m.media-amazon.com/images/I/71+t+7LL.jpg', 0, 285.0, 456.0, 462.0),
(8, 'Fractal Design Pop Air', 'https://m.media-amazon.com/images/I/71+y+7LL.jpg', 0, 215.0, 473.0, 454.0),
(9, 'Lian Li Lancool 216', 'https://m.media-amazon.com/images/I/71+z+7LL.jpg', 0, 235.0, 480.0, 491.0),
(10, 'Fractal Design Torrent', 'https://m.media-amazon.com/images/I/71+a+7LL.jpg', 0, 242.0, 544.0, 530.0);

DELETE FROM component_placement;

INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'CaseCooler', 0, 0, 0, 0, 120.0, 120.0, 100.0 FROM `case`;
INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'CaseCooler', 0, 0, 1, 0, 120.0, 120.0, 100.0 FROM `case`;
INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'CaseCooler', 0, 0, 2, 0, 120.0, 120.0, 100.0 FROM `case`;
INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'CaseCooler', -1, 0, 0, 0, 120.0, 120.0, 100.0 FROM `case`;
INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'CaseCooler', -1, 0, 1, 0, 120.0, 120.0, 100.0 FROM `case`;
INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'CaseCooler', -1, 0, 2, 0, 120.0, 120.0, 100.0 FROM `case`;

INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'CaseCooler', 0, 1, 0, 0, 120.0, 120.0, 100.0 FROM `case` WHERE id IN (1, 2, 6, 10);
INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'CaseCooler', 0, 1, 1, 0, 120.0, 120.0, 100.0 FROM `case` WHERE id IN (1, 2, 6, 10);
INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'CaseCooler', 0, 1, 2, 0, 120.0, 120.0, 100.0 FROM `case` WHERE id IN (1, 2, 6, 10);

INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'CaseCooler', 1, 0, 0, 0, 120.0, 120.0, 100.0 FROM `case` WHERE id IN (1, 6);
INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'CaseCooler', 1, 0, 1, 0, 120.0, 120.0, 100.0 FROM `case` WHERE id IN (1, 6);
INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'CaseCooler', 1, 0, 2, 0, 120.0, 120.0, 100.0 FROM `case` WHERE id IN (1, 6);

INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'GPU', 0, 0, 0, 0, 140.0, 450.0, 450.0 FROM `case`;

INSERT IGNORE INTO component_placement (case_id, component_type, x, y, z, rotation, width, length, height) SELECT id, 'CPUCooler', 0, 0, 0, 0, 200.0, 200.0, 185.0 FROM `case`;

INSERT IGNORE INTO cpu (id, cpu_brand, cpu_model, cpu_socket_type, cpu_cores, cpu_threads, cpu_base_clock_ghz, cpu_boost_clock_ghz, power_draw, picture_url, width, length, height) VALUES
(1, 'Intel', 'Core i9-13900K', 'LGA1700', 24, 32, 3.0, 5.8, 125, 'https://example.com/cpu/i9-13900k.png', 35, 35, 4),
(2, 'Intel', 'Core i7-13700K', 'LGA1700', 16, 24, 3.4, 5.4, 125, 'https://example.com/cpu/i7-13700k.png', 35, 35, 4),
(3, 'AMD', 'Ryzen 9 7950X', 'AM5', 16, 32, 4.5, 5.7, 170, 'https://example.com/cpu/r9-7950x.png', 40, 40, 4),
(4, 'AMD', 'Ryzen 7 7800X3D', 'AM5', 8, 16, 4.2, 5.0, 120, 'https://example.com/cpu/r7-7800x3d.png', 40, 40, 4),
(5, 'Intel', 'Core i5-13600K', 'LGA1700', 14, 20, 3.5, 5.1, 125, 'https://example.com/cpu/i5-13600k.png', 35, 35, 4),
(6, 'AMD', 'Ryzen 5 7600X', 'AM5', 6, 12, 4.7, 5.3, 105, 'https://example.com/cpu/r5-7600x.png', 40, 40, 4),
(7, 'Intel', 'Core i9-14900K', 'LGA1700', 24, 32, 3.2, 6.0, 125, 'https://example.com/cpu/i9-14900k.png', 35, 35, 4),
(8, 'AMD', 'Ryzen 9 7900X', 'AM5', 12, 24, 4.7, 5.6, 170, 'https://example.com/cpu/r9-7900x.png', 40, 40, 4),
(9, 'AMD', 'Ryzen 7 5800X3D', 'AM4', 8, 16, 3.4, 4.5, 105, 'https://example.com/cpu/r7-5800x3d.png', 40, 40, 4),
(10, 'Intel', 'Core i5-12600K', 'LGA1700', 10, 16, 3.7, 4.9, 125, 'https://example.com/cpu/i5-12600k.png', 35, 35, 4);

INSERT IGNORE INTO motherboard (id, motherboard_brand, motherboard_model, chipset, socket_type, memory_type, picture_url, power_draw, width, length, height) VALUES
(1, 'ASUS', 'ROG Maximus Z790', 'Z790', 'LGA1700', 'DDR5', 'https://example.com/mobo/z790-hero.png', 50, 244, 305, 50),
(2, 'MSI', 'MAG B650 Tomahawk', 'B650', 'AM5', 'DDR5', 'https://example.com/mobo/b650-tomahawk.png', 45, 244, 305, 50),
(3, 'Gigabyte', 'Z790 AORUS Elite', 'Z790', 'LGA1700', 'DDR5', 'https://example.com/mobo/z790-aorus.png', 50, 244, 305, 50),
(4, 'ASRock', 'X670E Taichi', 'X670E', 'AM5', 'DDR5', 'https://example.com/mobo/x670e-taichi.png', 60, 267, 305, 50),
(5, 'ASUS', 'TUF Gaming B550', 'B550', 'AM4', 'DDR4', 'https://example.com/mobo/b550-tuf.png', 40, 244, 305, 50),
(6, 'MSI', 'PRO Z790-A', 'Z790', 'LGA1700', 'DDR5', 'https://example.com/mobo/z790-pro.png', 45, 244, 305, 50),
(7, 'Gigabyte', 'B650 Gaming X', 'B650', 'AM5', 'DDR5', 'https://example.com/mobo/b650-gaming.png', 45, 244, 305, 50),
(8, 'ASRock', 'B760M Steel Legend', 'B760', 'LGA1700', 'DDR5', 'https://example.com/mobo/b760m-steel.png', 40, 244, 244, 50),
(9, 'ASUS', 'ROG Strix B650E-F', 'B650E', 'AM5', 'DDR5', 'https://example.com/mobo/b650e-strix.png', 55, 244, 305, 50),
(10, 'MSI', 'MPG Z790 Edge', 'Z790', 'LGA1700', 'DDR5', 'https://example.com/mobo/z790-edge.png', 50, 244, 305, 50);

INSERT IGNORE INTO cpu_cooler (id, cooling_type, cpu_socket, fan_count, case_cooler_slots_required, pccase_type, maxtdp, power_draw, picture_url, width, length, height) VALUES
(1, 0, 'LGA1700', 3, 3, 'ATX', 300, 15, 'https://example.com/cooler/kraken360.png', 120.0, 394.0, 52.0),
(2, 0, 'LGA1700', 3, 3, 'ATX', 280, 15, 'https://example.com/cooler/h150i.png', 120.0, 397.0, 52.0),
(3, 0, 'AM5', 3, 3, 'ATX', 320, 15, 'https://example.com/cooler/lf3-360.png', 120.0, 398.0, 65.0),
(4, 0, 'AM5', 3, 3, 'ATX', 300, 15, 'https://example.com/cooler/ls720.png', 120.0, 402.0, 52.0),
(5, 1, 'AM5', 2, 0, 'ATX', 220, 5, 'https://example.com/cooler/nhd15.png', 150.0, 161.0, 165.0),
(6, 1, 'LGA1700', 1, 0, 'ATX', 250, 5, 'https://example.com/cooler/drp4.png', 136.0, 146.0, 163.0),
(7, 1, 'AM4', 2, 0, 'ATX', 260, 5, 'https://example.com/cooler/ak620.png', 129.0, 138.0, 160.0),
(8, 1, 'LGA1700', 1, 0, 'ATX', 180, 4, 'https://example.com/cooler/hyper212.png', 120.0, 80.0, 158.0),
(9, 0, 'AM4', 2, 2, 'ATX', 240, 10, 'https://example.com/cooler/nzxt240.png', 120.0, 275.0, 52.0),
(10, 0, 'LGA1700', 2, 2, 'ATX', 250, 10, 'https://example.com/cooler/arctic240.png', 120.0, 277.0, 65.0);

INSERT IGNORE INTO ram_kit (id, ram_type, ram_size_gb, ram_speed_mhz, ram_sticks_count, picture_url, power_draw, width, length, height) VALUES
(1, 'DDR5', 32, 6000, 2, 'https://example.com/ram/vengeance-ddr5.png', 5, 135, 8, 45),
(2, 'DDR5', 32, 6000, 2, 'https://example.com/ram/trident-z5.png', 5, 135, 8, 44),
(3, 'DDR5', 16, 5600, 2, 'https://example.com/ram/fury-beast.png', 4, 133, 7, 35),
(4, 'DDR4', 16, 3200, 2, 'https://example.com/ram/vengeance-ddr4.png', 4, 135, 7, 34),
(5, 'DDR4', 32, 3600, 2, 'https://example.com/ram/ripjaws-v.png', 4, 133, 8, 42),
(6, 'DDR5', 32, 6400, 2, 'https://example.com/ram/delta-rgb.png', 6, 135, 8, 46),
(7, 'DDR5', 64, 5600, 2, 'https://example.com/ram/crucial-pro.png', 5, 133, 7, 32),
(8, 'DDR5', 64, 6200, 2, 'https://example.com/ram/dominator.png', 7, 135, 9, 56),
(9, 'DDR5', 32, 6000, 2, 'https://example.com/ram/xpg-lancer.png', 5, 133, 8, 40),
(10, 'DDR4', 32, 4000, 2, 'https://example.com/ram/royal.png', 5, 133, 8, 44);

INSERT IGNORE INTO gpu (id, gpu_brand, gpu_model, gpu_memory_gb, picture_url, power_draw, width, length, height) VALUES
(1, 'ASUS', 'ROG Strix 4090', 24, 'https://example.com/gpu/strix4090.png', 450, 149.3, 357.6, 70.1),
(2, 'MSI', 'Gaming X Trio 4080', 16, 'https://example.com/gpu/trio4080.png', 320, 140.0, 337.0, 67.0),
(3, 'Gigabyte', 'AERO OC 4070 Ti', 12, 'https://example.com/gpu/aero4070ti.png', 285, 130.0, 300.0, 58.0),
(4, 'Sapphire', 'Nitro+ 7900 XTX', 24, 'https://example.com/gpu/nitro7900xtx.png', 355, 135.0, 320.0, 72.0),
(5, 'PowerColor', 'Red Devil 7800 XT', 16, 'https://example.com/gpu/reddevil7800xt.png', 263, 135.0, 332.0, 52.0),
(6, 'NVIDIA', 'FE 4090', 24, 'https://example.com/gpu/fe4090.png', 450, 137.0, 304.0, 61.0),
(7, 'ASUS', 'TUF Gaming 4070', 12, 'https://example.com/gpu/tuf4070.png', 200, 139.0, 301.0, 63.0),
(8, 'XFX', 'Speedster MERC310', 20, 'https://example.com/gpu/merc7900xt.png', 315, 128.0, 344.0, 55.0),
(9, 'Zotac', 'Trinity 4080', 16, 'https://example.com/gpu/trinity4080.png', 320, 150.0, 356.0, 71.0),
(10, 'MSI', 'Ventus 3X 4060 Ti', 8, 'https://example.com/gpu/ventus4060ti.png', 160, 120.0, 308.0, 42.0);

INSERT IGNORE INTO psu (id, psu_model, psu_wattage, picture_url, power_draw, width, length, height) VALUES
(1, 'Corsair RM1000x', 1000, 'https://example.com/psu/rm1000x.png', 0, 150, 180, 86),
(2, 'Seasonic Vertex GX-1200', 1200, 'https://example.com/psu/vertex1200.png', 0, 150, 170, 86),
(3, 'MSI MPG A850G', 850, 'https://example.com/psu/a850g.png', 0, 150, 150, 86),
(4, 'EVGA SuperNOVA 1000 GT', 1000, 'https://example.com/psu/1000gt.png', 0, 150, 150, 86),
(5, 'Corsair RM750e', 750, 'https://example.com/psu/rm750e.png', 0, 150, 140, 86),
(6, 'Thermaltake GF3 1000W', 1000, 'https://example.com/psu/gf3-1000.png', 0, 150, 160, 86),
(7, 'BeQuiet Dark Power 13', 1000, 'https://example.com/psu/darkpower13.png', 0, 150, 175, 86),
(8, 'ASUS ROG Thor 1000P2', 1000, 'https://example.com/psu/thor1000.png', 0, 150, 190, 86),
(9, 'Corsair SF750', 750, 'https://example.com/psu/sf750.png', 0, 125, 100, 63),
(10, 'NZXT C1200', 1200, 'https://example.com/psu/c1200.png', 0, 150, 150, 86);

INSERT IGNORE INTO storage (id, storage_total_gb, storage_type, storage_count, picture_url, power_draw, width, length, height) VALUES
(1, 2000, 'NVMe', 1, 'https://example.com/ssd/990pro.png', 8, 22, 80, 2),
(2, 2000, 'NVMe', 1, 'https://example.com/ssd/sn850x.png', 8, 22, 80, 2),
(3, 2000, 'NVMe', 1, 'https://example.com/ssd/t700.png', 10, 22, 80, 15),
(4, 2000, 'NVMe', 1, 'https://example.com/ssd/kc3000.png', 8, 22, 80, 3),
(5, 4000, 'NVMe', 1, 'https://example.com/ssd/rocket4.png', 9, 22, 80, 3),
(6, 1000, 'NVMe', 1, 'https://example.com/ssd/980pro.png', 7, 22, 80, 2),
(7, 1000, 'NVMe', 1, 'https://example.com/ssd/sn580.png', 5, 22, 80, 2),
(8, 2000, 'NVMe', 1, 'https://example.com/ssd/p3plus.png', 5, 22, 80, 2),
(9, 2000, 'NVMe', 1, 'https://example.com/ssd/firecuda530.png', 9, 22, 80, 3),
(10, 4000, 'NVMe', 1, 'https://example.com/ssd/mp34.png', 6, 22, 80, 3);

INSERT IGNORE INTO case_cooler (id, fan_size, picture_url, power_draw, width, length, height) VALUES
(1, 120, 'https://example.com/fan/sl120.png', 3, 120, 120, 25),
(2, 120, 'https://example.com/fan/af120.png', 3, 120, 120, 25),
(3, 120, 'https://example.com/fan/nfa12x25.png', 2, 120, 120, 25),
(4, 120, 'https://example.com/fan/silentwings4.png', 2, 120, 120, 25),
(5, 120, 'https://example.com/fan/d30.png', 3, 120, 120, 30),
(6, 120, 'https://example.com/fan/f120.png', 3, 120, 120, 25),
(7, 120, 'https://example.com/fan/p12.png', 2, 120, 120, 25),
(8, 120, 'https://example.com/fan/tllcd.png', 5, 120, 120, 28),
(9, 120, 'https://example.com/fan/ql120.png', 4, 120, 120, 25),
(10, 120, 'https://example.com/fan/tlc12c.png', 2, 120, 120, 25);


INSERT IGNORE INTO component_links (component_type, component_id, url) SELECT 'CPU', id, CONCAT('https://shop.com/cpu/', id) FROM cpu;
INSERT IGNORE INTO component_links (component_type, component_id, url) SELECT 'GPU', id, CONCAT('https://shop.com/gpu/', id) FROM gpu;
INSERT IGNORE INTO component_links (component_type, component_id, url) SELECT 'MotherBoard', id, CONCAT('https://shop.com/mb/', id) FROM motherboard;
INSERT IGNORE INTO component_links (component_type, component_id, url) SELECT 'RAMKit', id, CONCAT('https://shop.com/ram/', id) FROM ram_kit;
INSERT IGNORE INTO component_links (component_type, component_id, url) SELECT 'Storage', id, CONCAT('https://shop.com/storage/', id) FROM storage;
INSERT IGNORE INTO component_links (component_type, component_id, url) SELECT 'PSU', id, CONCAT('https://shop.com/psu/', id) FROM psu;
INSERT IGNORE INTO component_links (component_type, component_id, url) SELECT 'CPUCooler', id, CONCAT('https://shop.com/cpucooler/', id) FROM cpu_cooler;
INSERT IGNORE INTO component_links (component_type, component_id, url) SELECT 'CaseCooler', id, CONCAT('https://shop.com/casecooler/', id) FROM case_cooler;
INSERT IGNORE INTO component_links (component_type, component_id, url) SELECT 'Case', id, CONCAT('https://shop.com/case/', id) FROM `case`;

INSERT IGNORE INTO component_price (component_type, component_id, price_value, time, location) SELECT 'CPU', id, 200 + (id * 10), '2023-01-01', 'US' FROM cpu;
INSERT IGNORE INTO component_price (component_type, component_id, price_value, time, location) SELECT 'GPU', id, 400 + (id * 20), '2023-01-01', 'US' FROM gpu;
INSERT IGNORE INTO component_price (component_type, component_id, price_value, time, location) SELECT 'MotherBoard', id, 150 + (id * 5), '2023-01-01', 'US' FROM motherboard;
INSERT IGNORE INTO component_price (component_type, component_id, price_value, time, location) SELECT 'RAMKit', id, 80 + (id * 2), '2023-01-01', 'US' FROM ram_kit;
INSERT IGNORE INTO component_price (component_type, component_id, price_value, time, location) SELECT 'Storage', id, 60 + (id * 5), '2023-01-01', 'US' FROM storage;
INSERT IGNORE INTO component_price (component_type, component_id, price_value, time, location) SELECT 'PSU', id, 100 + (id * 5), '2023-01-01', 'US' FROM psu;
INSERT IGNORE INTO component_price (component_type, component_id, price_value, time, location) SELECT 'CPUCooler', id, 50 + (id * 5), '2023-01-01', 'US' FROM cpu_cooler;
INSERT IGNORE INTO component_price (component_type, component_id, price_value, time, location) SELECT 'CaseCooler', id, 15 + id, '2023-01-01', 'US' FROM case_cooler;
INSERT IGNORE INTO component_price (component_type, component_id, price_value, time, location) SELECT 'Case', id, 90 + (id * 10), '2023-01-01', 'US' FROM `case`;

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;