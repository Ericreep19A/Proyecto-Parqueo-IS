import React from 'react'
import { useNavigate } from 'react-router-dom'
import { QRCodeSVG } from 'qrcode.react'
import './Dashboard.css'

function Dashboard() {
  const navigate = useNavigate()
  const estudiante = {
    nombre: 'Henry Sicajau',
    correo: 'henry@umg.edu.gt',
    solvente: true,
    marbete: 'Vigente hasta: Junio 2026',
    espacios: ['A-12', 'B-04', 'C-07']
  }

  return (
    <div className="dash-container">

      <div className="dash-header">
        <h1 className="dash-titulo">Sistema de Parqueo</h1>
        <p className="dash-correo">{estudiante.correo}</p>
      </div>

      <div className="dash-grid">

        <div className="dash-card">
          <h2 className="card-label">Estudiante</h2>
          <p className="card-valor">{estudiante.nombre}</p>
        </div>

        <div className={`dash-card ${estudiante.solvente ? 'solvente' : 'no-solvente'}`}>
          <h2 className="card-label">Solvencia</h2>
          <p className="card-valor">{estudiante.solvente ? '✓ Solvente' : '✗ No Solvente'}</p>
        </div>

        <div className="dash-card">
          <h2 className="card-label">Marbete</h2>
          <p className="card-valor">{estudiante.marbete}</p>
        </div>

      </div>

      <div className="dash-qr">
        <h2 className="card-label">Tu código QR de acceso</h2>
        <QRCodeSVG value={estudiante.correo} size={180} />
        <p className="qr-hint">Muestra este código al guardia para ingresar</p>
      </div>

      <div className="dash-espacios" style={{paddingBottom: '20px'}}>
        <h2 className="card-label">Espacios sugeridos</h2>
        <div className="espacios-grid">
          {estudiante.espacios.map((espacio) => (
            <div className="espacio-item" key={espacio}>
              {espacio}
            </div>
          ))}
        </div>
        <button className="btn-parqueo" onClick={() => navigate('/parqueo')}>
          Ver mapa del parqueo
        </button>

        <button className="btn-pago" onClick={() => navigate('/pago')}>
          Ver estado de pago
        </button>
      </div>

    </div>
  )
}

export default Dashboard