import React from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Parqueo from './pages/Parqueo'
import Pago from './pages/Pago'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/parqueo" element={<Parqueo />} />
        <Route path="/pago" element={<Pago />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App