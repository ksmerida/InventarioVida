DELIMITER $$
CREATE PROCEDURE `sp_inventario_vida` (
	IN i_opcion char(1),
    IN i_id_usuario int,
    IN i_id_rol int, 
    IN i_nombres varchar(100),
    IN i_apellidos varchar(100),
    IN i_correo varchar(150),
    IN i_pass varchar(255),
    IN i_estado boolean
)
BEGIN
-- INSERTAR USUARIO
	if i_opcion = 'IU' then
		insert into usuario (id_rol, nombres, apellidos, correo, pass, estado)
        values (i_id_rol, i_nombres, i_apellidos, i_correo, i_pass, IFNULL(i_estado, 1));
        
-- ACTUALIZAR USUARIO
	elseif i_opcion = 'UU' then
        update usuario
        set id_rol = i_id_rol,
            nombres = i_nombres,
            apellidos = i_apellidos,
            correo = i_correo,
            pass = i_pass,
            estado = i_estado
        where id_usuario = i_id_usuario;
        
-- ELIMINAR USUARIO (ELIMINADO LOGICO)
	elseif i_opcion = 'DU' then
        update usuario
        set estado = 0
        where id_usuario = i_id_usuario;
        
-- CONSULTAR USUARIO
	elseif i_opcion = 'SU' then
        if i_id_usuario is not null then
            select u.id_usuario, r.nombre_rol, u.nombres, u.apellidos, u.correo, u.estado from usuario u, r rol where id_usuario = i_id_usuario;
        else
            select * from usuario;
        end if;
    end if;sp_inventario_vida
end $$

DELIMITER ;

-- Procedimiento Almacenado Sede
DELIMITER //
CREATE PROCEDURE sp_sede(
    IN p_accion VARCHAR(2),    -- 'IU' insertar, 'UU' actualizar, 'DU' eliminar lógico, 'SU' seleccionar
    IN p_id_sede INT,
    IN p_nombre_sede VARCHAR(100),
    IN p_direccion VARCHAR(255),
    IN p_estado TINYINT
)
BEGIN
    -- Insertar sede
    IF p_accion = 'IU' THEN
        INSERT INTO sede (nombre_sede, direccion, estado)
        VALUES (p_nombre_sede, p_direccion, 1);

    -- Actualizar sede
    ELSEIF p_accion = 'UU' THEN
        UPDATE sede
        SET nombre_sede = p_nombre_sede,
            direccion = p_direccion
        WHERE id_sede = p_id_sede;

    -- Eliminar lógico
    ELSEIF p_accion = 'DU' THEN
        UPDATE sede
        SET estado = 0
        WHERE id_sede = p_id_sede;

    -- Mostrar todas las sedes activas
    ELSEIF p_accion = 'SU' THEN
        SELECT id_sede, nombre_sede, direccion
        FROM sede
        WHERE estado = 1;
    END IF;
END //
DELIMITER ;

-- Procedimiento Almacenado Producto
DELIMITER //
CREATE PROCEDURE sp_producto(
    IN p_accion VARCHAR(2),      -- 'IP' insertar, 'UP' actualizar, 'DP' eliminar lógico, 'SP' seleccionar
    IN p_id_producto INT,
    IN p_id_marca INT,
    IN p_nombre_producto VARCHAR(150),
    IN p_descripcion VARCHAR(255),
    IN p_codigo_barras VARCHAR(100),
    IN p_color VARCHAR(50),
    IN p_presentacion VARCHAR(100),
    IN p_unidad_medida VARCHAR(50),
    IN p_stock INT,
    IN p_estado TINYINT
)
BEGIN
    -- Insertar producto
    IF p_accion = 'IP' THEN
        INSERT INTO producto (
            id_marca, nombre_producto, descripcion, codigo_barras, color,
            presentacion, unidad_medida, stock, estado
        ) VALUES (
            p_id_marca, p_nombre_producto, p_descripcion, p_codigo_barras, p_color,
            p_presentacion, p_unidad_medida, p_stock, 1
        );

    -- Actualizar producto
    ELSEIF p_accion = 'UP' THEN
        UPDATE producto
        SET id_marca = p_id_marca,
            nombre_producto = p_nombre_producto,
            descripcion = p_descripcion,
            codigo_barras = p_codigo_barras,
            color = p_color,
            presentacion = p_presentacion,
            unidad_medida = p_unidad_medida,
            stock = p_stock
        WHERE id_producto = p_id_producto;

    -- Eliminación lógica
    ELSEIF p_accion = 'DP' THEN
        UPDATE producto
        SET estado = 0
        WHERE id_producto = p_id_producto;

    -- Mostrar productos activos
    ELSEIF p_accion = 'SP' THEN
        SELECT p.id_producto, m.nombre_marca, p.nombre_producto, p.descripcion,
               p.codigo_barras, p.color, p.presentacion, p.unidad_medida, p.stock
        FROM producto p
        LEFT JOIN marca m ON m.id_marca = p.id_marca
        WHERE p.estado = 1;
    END IF;
END //
DELIMITER ;


