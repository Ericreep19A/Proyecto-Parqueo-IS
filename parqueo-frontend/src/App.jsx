import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';

import Login         from './pages/Login.jsx';
import Dashboard     from './pages/Dashboard.jsx';
import MiQR          from './pages/MiQR.jsx';
import MisVehiculos  from './pages/MisVehiculos.jsx';
import MiMarbete     from './pages/MiMarbete.jsx';
import ValidarAcceso from './pages/ValidarAcceso.jsx';
import Espacios      from './pages/Espacios.jsx';

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route path="/"          element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<Dashboard />} />

        {/* Solo estudiantes */}
        <Route path="/mi-qr"        element={<ProtectedRoute roles={['ESTUDIANTE']}><MiQR /></ProtectedRoute>} />
        <Route path="/mi-marbete"   element={<ProtectedRoute roles={['ESTUDIANTE']}><MiMarbete /></ProtectedRoute>} />
        <Route path="/mis-vehiculos" element={<ProtectedRoute roles={['ESTUDIANTE']}><MisVehiculos /></ProtectedRoute>} />

        {/* Solo seguridad/admin */}
        <Route path="/validar"   element={<ProtectedRoute roles={['SEGURIDAD','ADMIN']}><ValidarAcceso /></ProtectedRoute>} />

        {/* Cualquier autenticado */}
        <Route path="/espacios"  element={<Espacios />} />
      </Route>

      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
