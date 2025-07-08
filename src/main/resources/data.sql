-- =================================================================
-- INSERTS PARA LA TABLA SUCURSAL
-- =================================================================
-- Se crean 3 sucursales de ejemplo.
INSERT INTO sucursal (id_sucursal, nombre, direccion, telefono, ciudad, region) VALUES
(1, 'Santiago Centro', 'Avenida Libertador #123', '+56911111111', 'Santiago', 'Metropolitana'),
(2, 'Providencia', 'Avenida Providencia #456', '+56922222222', 'Santiago', 'Metropolitana'),
(3, 'Concepcion Centro', 'Calle O Higgins #789', '+56933333333', 'Concepcion', 'Biobio');

-- =================================================================
-- INSERTS PARA LA TABLA PRODUCTO
-- =================================================================
-- Se crean 4 productos de ejemplo. Recuerda que el stock ya no está aquí.
INSERT INTO producto (id_producto, nombre, precio) VALUES
(101, 'Laptop Gamer Pro', 1250000),
(102, 'Mouse Inalambrico RGB', 35000),
(103, 'Teclado Mecanico', 80000),
(104, 'Monitor Curvo 27"', 250000);

-- =================================================================
-- INSERTS PARA LA TABLA PRODUCTO_SUCURSAL
-- =================================================================
-- Aquí se asigna el stock y el precio específico para cada producto en cada sucursal.
-- Este es el paso más importante.

-- Productos para Santiago Centro (id_sucursal = 1)
INSERT INTO producto_sucursal (id_producto, id_sucursal, stock, precio_unitario) VALUES
(101, 1, 15, 1250000), -- Hay 15 Laptops en Santiago Centro
(102, 1, 50, 34990),   -- Hay 50 Mouses en Santiago Centro
(103, 1, 30, 79990);    -- Hay 30 Teclados en Santiago Centro

-- Productos para Providencia (id_sucursal = 2)
INSERT INTO producto_sucursal (id_producto, id_sucursal, stock, precio_unitario) VALUES
(101, 2, 3, 1280000),  -- Hay 3 Laptops en Providencia (STOCK BAJO)
(104, 2, 20, 249990); -- Hay 20 Monitores en Providencia

-- Productos para Concepción Centro (id_sucursal = 3)
INSERT INTO producto_sucursal (id_producto, id_sucursal, stock, precio_unitario) VALUES
(102, 3, 8, 35500),   -- Hay 8 Mouses en Concepción (STOCK BAJO)
(103, 3, 25, 81000),   -- Hay 25 Teclados en Concepción
(104, 3, 7, 255000);  -- Hay 7 Monitores en Concepción (STOCK BAJO)

-- Este producto no tendrá stock en ninguna sucursal para probar el filtro
-- INSERT INTO producto_sucursal (id_producto, id_sucursal, stock, precio_unitario) VALUES (101, 3, 0, 1300000);