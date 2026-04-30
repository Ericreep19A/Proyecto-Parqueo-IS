import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './Login.css'

function Login() {
  const [correo, setCorreo] = useState('')
  const navigate = useNavigate()

  function handleSubmit() {
    if (correo === '') {
      alert('Por favor ingresa tu correo')
      return
    }
    navigate('/dashboard')
  }

  return (
    <div className="login-container">
      <div className="login-card">
        <h1 className="login-titulo">Sistema de Parqueo</h1>
        <p className="login-subtitulo">Ingresa tu correo institucional</p>

        <input
          className="login-input"
          type="email"
          placeholder="ejemplo@umg.edu.gt"
          value={correo}
          onChange={(e) => setCorreo(e.target.value)}
        />

        <button className="login-boton" onClick={handleSubmit}>
          Iniciar Sesión
        </button>
      </div>
    </div>
  )
}

export default Login