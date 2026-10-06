-- UC-CB-02. Hibernate puede crear el esquema en desarrollo; este script sirve para instalaciones MySQL/MariaDB controladas.
CREATE TABLE IF NOT EXISTS talleres (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(120) NOT NULL, razon_social VARCHAR(180) NOT NULL,
  rfc VARCHAR(13) NOT NULL, telefono VARCHAR(10) NOT NULL, email VARCHAR(180) NOT NULL, fotografia MEDIUMTEXT,
  pais VARCHAR(60) NOT NULL DEFAULT 'México', calle VARCHAR(120) NOT NULL, numero VARCHAR(20) NOT NULL,
  colonia VARCHAR(120) NOT NULL, municipio VARCHAR(120) NOT NULL, estado VARCHAR(120) NOT NULL, codigo_postal CHAR(5) NOT NULL,
  CONSTRAINT uk_taller_rfc UNIQUE (rfc), CONSTRAINT uk_taller_email UNIQUE (email), CONSTRAINT uk_taller_telefono UNIQUE (telefono),
  INDEX idx_taller_ubicacion (estado, municipio, codigo_postal)
);
CREATE TABLE IF NOT EXISTS clientes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, nombre_completo VARCHAR(120) NOT NULL, contacto_alternativo VARCHAR(120) NOT NULL,
  edad INT NOT NULL, fecha_nacimiento DATE NOT NULL, telefono_personal VARCHAR(10) NOT NULL, telefono_trabajo VARCHAR(10),
  email VARCHAR(180) NOT NULL, email_trabajo VARCHAR(180), fotografia MEDIUMTEXT, estatus VARCHAR(12) NOT NULL DEFAULT 'ACTIVO',
  pais VARCHAR(60) NOT NULL DEFAULT 'México', calle VARCHAR(120) NOT NULL, numero VARCHAR(20) NOT NULL,
  colonia VARCHAR(120) NOT NULL, municipio VARCHAR(120) NOT NULL, estado VARCHAR(120) NOT NULL, codigo_postal CHAR(5) NOT NULL,
  CONSTRAINT uk_cliente_email UNIQUE (email), CONSTRAINT uk_cliente_telefono UNIQUE (telefono_personal),
  INDEX idx_cliente_estatus_nombre (estatus, nombre_completo)
);
CREATE TABLE IF NOT EXISTS cliente_taller (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, customer_id BIGINT NOT NULL, workshop_id BIGINT NOT NULL,
  CONSTRAINT uk_cliente_taller UNIQUE (customer_id, workshop_id),
  CONSTRAINT fk_cliente_taller_cliente FOREIGN KEY (customer_id) REFERENCES clientes(id),
  CONSTRAINT fk_cliente_taller_taller FOREIGN KEY (workshop_id) REFERENCES talleres(id),
  INDEX idx_cliente_taller_taller (workshop_id, customer_id)
);
