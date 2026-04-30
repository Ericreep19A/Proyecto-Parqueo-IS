import React from 'react'
import { useNavigate } from 'react-router-dom'
import './Parqueo.css'

const espacios = [
  {id:'A-01', estado:'ocupado'}, {id:'A-02', estado:'libre'}, {id:'A-03', estado:'libre'},
  {id:'A-04', estado:'ocupado'}, {id:'A-05', estado:'sugerido'}, {id:'A-06', estado:'libre'},
  {id:'B-01', estado:'libre'}, {id:'B-02', estado:'ocupado'}, {id:'B-03', estado:'sugerido'},
  {id:'B-04', estado:'libre'}, {id:'B-05', estado:'ocupado'}, {id:'B-06', estado:'libre'},
  {id:'C-01', estado:'ocupado'}, {id:'C-02', estado:'libre'}, {id:'C-03', estado:'libre'},
  {id:'C-04', estado:'sugerido'}, {id:'C-05', estado:'ocupado'}, {id:'C-06', estado:'libre'},
]

function Parqueo() {
  const navigate = useNavigate()

  return (
    <div className="parqueo-container">
      <div className="parqueo-header">
        <h1 className="parqueo-titulo">Espacios Disponibles</h1>
        <p className="parqueo-subtitulo">Selecciona un espacio sugerido para ti</p>
      </div>

      <div className="parqueo-leyenda">
        <div className="leyenda-item">
          <div className="leyenda-color" style={{background:'#2d6a4f'}}></div>
          <span>Libre</span>
        </div>
        <div className="leyenda-item">
          <div className="leyenda-color" style={{background:'#444'}}></div>
          <span>Ocupado</span>
        </div>
        <div className="leyenda-item">
          <div className="leyenda-color" style={{background:'#e63946'}}></div>
          <span>Sugerido para ti</span>
        </div>
      </div>

      <div className="parqueo-grid">
        {espacios.map((e) => (
          <div key={e.id} className={`espacio ${e.estado}`}>
            {e.id}
          </div>
        ))}
      </div>

      <button className="btn-volver" onClick={() => navigate('/dashboard')}>
        Volver al Dashboard
      </button>
    </div>
  )
}

export default Parqueo