# Parqueo UMG · Frontend

Frontend React + Vite + Tailwind para el sistema de control de acceso vehicular.
Se conecta con el backend Spring Boot (`localhost:8081/api`).

## 1. Requisitos

- **Node.js 18+** ([descarga aquí](https://nodejs.org))
- El backend corriendo en `http://localhost:8081`
- VS Code (opcional)

## 2. Instalación rápida

```bash
cd parqueo-frontend
npm install
npm run dev
```

La app abre automáticamente en `http://localhost:5173`.

## 3. Configuración

El archivo `.env` ya viene configurado para conectarse al backend en `localhost:8081`. Si cambias el puerto del backend:

```env
VITE_API_URL=http://localhost:OTRO_PUERTO/api
```

## 4. Credenciales de prueba

**Contraseña común:** `Password123.`

| Correo                            | Rol         | Comportamiento esperado            |
|-----------------------------------|-------------|------------------------------------|
| brandon.jom@miumg.edu.gt          | ESTUDIANTE  | Autorizado · todos sus checks ok   |
| erica.hidalgo@miumg.edu.gt        | ESTUDIANTE  | Autorizado                         |
| isaura.caceres@miumg.edu.gt       | ESTUDIANTE  | Autorizada                         |
| henry.sicajau@miumg.edu.gt        | ESTUDIANTE  | **Denegado** · sin pago vigente    |
| seguridad@miumg.edu.gt            | SEGURIDAD   | Acceso al panel de validación      |
| admin@miumg.edu.gt                | ADMIN       | Acceso completo                    |

## 5. Páginas implementadas

| Ruta            | Acceso        | Cobertura |
|-----------------|---------------|-----------|
| `/login`        | público       | RNF03     |
| `/dashboard`    | autenticado   | RF10      |
| `/mi-qr`        | estudiante    | RF06      |
| `/mi-marbete`   | estudiante    | RF04, RF05 |
| `/mis-vehiculos`| estudiante    | —         |
| `/validar`      | seguridad/admin | RF01-RF08 |
| `/espacios`     | autenticado   | RF08, RF09 |

## 6. Flujo de prueba completo

1. Inicia sesión como `brandon.jom@miumg.edu.gt`.
2. En `Dashboard` deberías ver "Autorizado" con todos los requisitos en verde.
3. Ve a **Mi código QR** → se genera tu pase con cronómetro de expiración.
4. **Copia el `qrToken`** desde la consola del navegador (Network) o reusa el de Swagger.
5. Cierra sesión, entra como `seguridad@miumg.edu.gt`.
6. Ve a **Validar acceso** → pega el `qrToken` y un `vehiculoId` (1 = Toyota Yaris de Brandon).
7. Debes ver el resultado **AUTORIZADO** con animación verde y espacio sugerido `A-01`.

Para probar la denegación: repite el flujo con `henry.sicajau@miumg.edu.gt`. Verás que ni siquiera puede generar QR (porque no tiene pago vigente).

## 7. Estructura

```
src/
├── api/client.js            # Axios + todos los endpoints
├── context/AuthContext.jsx  # Sesión y JWT
├── components/
│   ├── Layout.jsx           # Sidebar + topbar
│   ├── ProtectedRoute.jsx
│   └── StatusBadge.jsx
├── pages/
│   ├── Login.jsx
│   ├── Dashboard.jsx
│   ├── MiQR.jsx
│   ├── MisVehiculos.jsx
│   ├── MiMarbete.jsx
│   ├── ValidarAcceso.jsx
│   └── Espacios.jsx
├── App.jsx
├── main.jsx
└── index.css
```

## 8. Diseño

- **Paleta**: navy profundo (`ink-950`) + dorado académico (`gold-400`)
- **Tipografía**: *Instrument Serif* (display) + *Plus Jakarta Sans* (body)
- **Iconografía**: Lucide React
- **Estilo**: institucional / digital wallet con tarjetas tipo "boarding pass" para el QR y "permiso oficial" para el marbete

## 9. Comandos útiles

```bash
npm run dev       # desarrollo
npm run build     # produccion
npm run preview   # previsualizar build
```
