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
