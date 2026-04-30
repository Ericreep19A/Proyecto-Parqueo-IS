import React from 'react'
import { useNavigate } from 'react-router-dom'
import './Pago.css'

function Pago() {
  const navigate = useNavigate()

  const pago = {
    nombre: 'Henry Sicajau',
    correo: 'henry@umg.edu.gt',
    monto: 'Q 150.00',
    periodo: 'Enero - Junio 2026',
    estado: 'Pagado',
    fecha: '15 de enero 2026',
    referencia: 'PAR-2026-00123'
  }

  return (
    <div className="pago-container">
      <div className="pago-header">
        <h1 className="pago-titulo">Estado de Pago</h1>
        <p className="pago-subtitulo">Estacionamiento semestral</p>
      </div>

      <div className="pago-card">
        <div className="pago-estado pagado">
          ✓ Pago al día
        </div>

        <div className="pago-detalle">
          <div className="detalle-fila">
            <span className="detalle-label">Estudiante</span>
            <span className="detalle-valor">{pago.nombre}</span>
          </div>
          <div className="detalle-fila">
            <span className="detalle-label">Correo</span>
            <span className="detalle-valor">{pago.correo}</span>
          </div>
          <div className="detalle-fila">
            <span className="detalle-label">Periodo</span>
            <span className="detalle-valor">{pago.periodo}</span>
          </div>
          <div className="detalle-fila">
            <span className="detalle-label">Monto pagado</span>
            <span className="detalle-valor">{pago.monto}</span>
          </div>
          <div className="detalle-fila">
            <span className="detalle-label">Fecha de pago</span>
            <span className="detalle-valor">{pago.fecha}</span>
          </div>
          <div className="detalle-fila">
            <span className="detalle-label">Referencia</span>
            <span className="detalle-valor">{pago.referencia}</span>
          </div>
        </div>
      </div>

      <button className="btn-volver" onClick={() => navigate('/dashboard')}>
        Volver al Dashboard
      </button>
    </div>
  )
}

export default Pago