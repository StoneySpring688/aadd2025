-- CUIDADO: Esto borra TODOS los datos [REINICIAR BD]
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE productos;
TRUNCATE TABLE categorias;
TRUNCATE TABLE usuarios;

SET FOREIGN_KEY_CHECKS = 1;

-- Ver todos los productos con su categoría y vendedor
SELECT 
    p.id,
    p.titulo,
    p.precio,
    p.estado,
    c.nombre AS categoria,
    u.nombre AS vendedor
FROM 
    productos p
    JOIN categorias c ON p.categoria_id = c.id
    JOIN usuarios u ON p.vendedor_id = u.id;

-- Ver categorías raíz
SELECT * FROM categorias WHERE categoria_padre_id IS NULL;

-- Ver subcategorías de una categoría específica
SELECT * FROM categorias WHERE categoria_padre_id = '8';

-- Contar productos por categoría
SELECT 
    c.nombre,
    COUNT(p.id) AS total_productos
FROM 
    categorias c
    LEFT JOIN productos p ON c.id = p.categoria_id
GROUP BY 
    c.id, c.nombre
ORDER BY 
    total_productos DESC;

-- Ver productos más visualizados del mes actual
SELECT 
    p.titulo,
    p.visualizaciones,
    p.fecha_publicacion,
    c.nombre AS categoria
FROM 
    productos p
    JOIN categorias c ON p.categoria_id = c.id
WHERE 
    MONTH(p.fecha_publicacion) = MONTH(CURRENT_DATE())
    AND YEAR(p.fecha_publicacion) = YEAR(CURRENT_DATE())
ORDER BY 
    p.visualizaciones DESC;