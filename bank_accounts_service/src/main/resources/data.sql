-- Asegúrate de que las contraseñas insertadas aquí estén HASHEADAS con BCrypt
-- Puedes generar hashes online o usando el PasswordEncoder en una clase de utilidad
-- Ejemplo: 'password' hasheado con BCrypt -> $2a$10$DOMpFU/Inv08PaSJkIz.e.g.XoTDfW/fLcp8GpHp/0.jWRWHGjcZa

-- Insertar un usuario regular
INSERT INTO usuarios (full_name, email, phone_number, address, balance, password, roles, creation_date, update_date) VALUES
    ('Usuario Ejemplo', 'usuario@ejemplo.com', '123456789', 'Calle Falsa 123', 1000.50, '$2a$10$DOMpFU/Inv08PaSJkIz.e.g.XoTDfW/fLcp8GpHp/0.jWRWHGjcZa', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insertar un usuario administrador
INSERT INTO usuarios (nombre_completo, correo_electronico, numero_telefono, direccion, saldo, password, roles, fecha_creacion, fecha_actualizacion) VALUES
    ('Admin Ejemplo', 'admin@ejemplo.com', '987654321', 'Avenida Siempre Viva 742', 500.00, '$2a$10$DOMpFU/Inv08PaSJkIz.e.g.XoTDfW/fLcp8GpHp/0.jWRWHGjcZa', 'ROLE_ADMIN,ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);