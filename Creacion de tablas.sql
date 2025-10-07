CREATE DATABASE IF NOT EXISTS inventario;
USE inventario;

-- TABLA MARCA
CREATE TABLE marca (
    id_marca INT AUTO_INCREMENT PRIMARY KEY,
    nombre_marca VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
) ENGINE=InnoDB;

-- TABLA PRODUCTO
CREATE TABLE producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    id_marca INT,
    nombre_producto VARCHAR(150) NOT NULL,
    descripcion VARCHAR(255),
    codigo_barras VARCHAR(100) UNIQUE,
    color VARCHAR(50),
    presentacion VARCHAR(100), -- Ej: "600ml", "caja 12"
    unidad_medida VARCHAR(50), -- Ej: "pieza", "litro"
    stock INT DEFAULT 0,
    estado BOOLEAN DEFAULT 1,
    FOREIGN KEY (id_marca) REFERENCES marca(id_marca)
) ENGINE=InnoDB;

-- TABLA SEDE
CREATE TABLE sede (
    id_sede INT AUTO_INCREMENT PRIMARY KEY,
    nombre_sede VARCHAR(100) NOT NULL,
    direccion VARCHAR(255),
    estado BOOLEAN DEFAULT 1
) ENGINE=InnoDB;

-- TABLA BODEGA
CREATE TABLE bodega (
    id_bodega INT AUTO_INCREMENT PRIMARY KEY,
    id_sede INT,
    nombre_bodega VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    estado BOOLEAN DEFAULT 1,
    FOREIGN KEY (id_sede) REFERENCES sede(id_sede)
) ENGINE=InnoDB;

-- TABLA ROL
CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
) ENGINE=InnoDB;

-- TABLA USUARIO
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_rol INT,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE,
    pass VARCHAR(255) NOT NULL,
    estado BOOLEAN DEFAULT 1,
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
) ENGINE=InnoDB;

-- TABLA MOVIMIENTO
CREATE TABLE movimiento (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    tipo_movimiento VARCHAR(50) NOT NULL, -- Entrada, Salida, Transferencia
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_bodega_origen INT,
    id_bodega_destino INT,
    id_usuario_responsable INT,
    id_usuario_receptor INT,
    observaciones VARCHAR(255),
    FOREIGN KEY (id_bodega_origen) REFERENCES bodega(id_bodega),
    FOREIGN KEY (id_bodega_destino) REFERENCES bodega(id_bodega),
    FOREIGN KEY (id_usuario_responsable) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_usuario_receptor) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- TABLA DETALLE_MOVIMIENTO
CREATE TABLE detalle_movimiento (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_movimiento INT,
    id_producto INT,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_movimiento) REFERENCES movimiento(id_movimiento),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
) ENGINE=InnoDB;

-- TABLA STOCK_BODEGA
CREATE TABLE stock_bodega (
    id_stock INT AUTO_INCREMENT PRIMARY KEY,
    id_bodega INT,
    id_producto INT,
    cantidad INT DEFAULT 0,
    FOREIGN KEY (id_bodega) REFERENCES bodega(id_bodega),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
) ENGINE=InnoDB;
