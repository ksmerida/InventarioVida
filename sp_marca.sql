CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_marca`(
    IN i_opcion CHAR(2),
    IN i_id_marca INT, 
    IN i_nombre_marca VARCHAR(100),
    IN i_descripcion VARCHAR(255),
    IN i_estado BOOLEAN
)
BEGIN
    -- INSERTAR ROL
    IF i_opcion = 'IR' THEN
        INSERT INTO marca (nombre_marca, descripcion, estado)
        VALUES (i_nombre_marca, i_descripcion, IFNULL(i_estado, 1));
    
    -- ACTUALIZAR ROL
    ELSEIF i_opcion = 'UR' THEN
        UPDATE marca
        SET nombre_marca = i_nombre_marca,
            descripcion = i_descripcion,
            estado = i_estado
        WHERE id_marca = i_id_marca;
    
    -- ELIMINAR ROL (ELIMINADO LÓGICO)
    ELSEIF i_opcion = 'DR' THEN
        UPDATE marca
        SET estado = 0
        WHERE id_marca = i_id_marca;
    
    -- CONSULTAR ROL
    ELSEIF i_opcion = 'SR' THEN
        IF i_id_marca IS NOT NULL THEN
            SELECT r.id_marca, r.nombre_marca, r.descripcion, r.estado
            FROM marca r
            WHERE r.id_marca = i_id_marca
            AND r.estado = 1;
        ELSE
            SELECT r.id_marca, r.nombre_marca, r.descripcion, r.estado
            FROM marca r
            WHERE r.estado = 1;
        END IF;
    END IF;
END