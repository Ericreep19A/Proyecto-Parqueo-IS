import axios from 'axios';

// El backend corre en localhost:8081 con context-path /api
const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081/api';

export const api = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

// Interceptor: agrega Bearer token automáticamente
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('parqueo_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Interceptor de respuesta: si 401, fuerza logout
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('parqueo_token');
      localStorage.removeItem('parqueo_user');
      // No redirigimos aquí para no romper hooks de React; AuthContext detecta.
    }
    return Promise.reject(err);
  }
);

/* =============== Auth =============== */
export const authApi = {
  login: (correo, password) =>
    api.post('/auth/login', { correo, password }).then((r) => r.data),
};

/* =============== Estudiantes =============== */
export const estudiantesApi = {
  miPerfil:   () => api.get('/estudiantes/perfil').then((r) => r.data),
  miSolvencia: () => api.get('/estudiantes/mi-solvencia').then((r) => r.data),
  solvenciaPorId: (id) => api.get(`/estudiantes/${id}/solvencia`).then((r) => r.data),
};

/* =============== Vehiculos =============== */
export const vehiculosApi = {
  misVehiculos: () => api.get('/vehiculos/mis-vehiculos').then((r) => r.data),
  registrar:    (data) => api.post('/vehiculos', data).then((r) => r.data),
};

/* =============== Marbete =============== */
export const marbetesApi = {
  miMarbete: () => api.get('/marbetes/mi-marbete').then((r) => r.data),
  generar:   () => api.post('/marbetes/generar').then((r) => r.data),
};

/* =============== QR =============== */
export const qrApi = {
  miQr: () => api.get('/qr/mi-qr').then((r) => r.data),
};

/* =============== Accesos =============== */
export const accesosApi = {
  validar: (qrToken, vehiculoId) =>
    api.post('/accesos/validar', { qrToken, vehiculoId }).then((r) => r.data),
};

/* =============== Espacios =============== */
export const espaciosApi = {
  disponibles: (tipo) => api.get(`/espacios/disponibles?tipo=${tipo}`).then((r) => r.data),
  sugerir:     (tipo) => api.get(`/espacios/sugerir?tipo=${tipo}`).then((r) => r.data),
  horasAfluencia: () => api.get('/espacios/horas-afluencia').then((r) => r.data),
};
