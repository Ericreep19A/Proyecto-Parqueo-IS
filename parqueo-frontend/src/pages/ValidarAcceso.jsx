import { useState } from 'react';
import { accesosApi } from '../api/client';
import { ScanLine, CheckCircle2, XCircle, Loader2, MapPin, Car, User, Hash } from 'lucide-react';

export default function ValidarAcceso() {
  const [qrToken, setQr]    = useState('');
  const [vehiculoId, setVeh]= useState('');
  const [busy, setBusy]     = useState(false);
  const [result, setResult] = useState(null);

  const onSubmit = async (e) => {
    e.preventDefault();
    setBusy(true); setResult(null);
    try {
      const r = await accesosApi.validar(qrToken.trim(), Number(vehiculoId));
      setResult(r);
    } catch (e2) {
      setResult({
        resultado: 'DENEGADO',
        mensaje: e2.response?.data?.mensaje || 'Error al validar',
        motivoDenegacion: e2.response?.data?.mensaje,
      });
    } finally { setBusy(false); }
  };

  const autorizado = result?.resultado === 'AUTORIZADO';

  return (
    <div className="animate-fade-up max-w-5xl">
      <div className="mb-8">
        <div className="text-xs uppercase tracking-widest text-ink-500 mb-2">Garita de acceso</div>
        <h1 className="font-display text-4xl">Validar ingreso vehicular.</h1>
        <p className="text-ink-600 text-sm mt-2">Pega el QR del estudiante y selecciona el vehículo correspondiente.</p>
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        {/* ===== FORM ===== */}
        <form onSubmit={onSubmit} className="card p-6 space-y-5">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-lg bg-ink-900 text-gold-400 flex items-center justify-center">
              <ScanLine size={18}/>
            </div>
            <div>
              <div className="font-medium">Validación manual</div>
              <div className="text-xs text-ink-500">Modo escaneo no disponible en MVP</div>
            </div>
          </div>

          <div>
            <label className="label">Token del QR</label>
            <textarea
              required rows={4}
              value={qrToken} onChange={(e) => setQr(e.target.value)}
              placeholder="eyJhbGciOiJIUzI1NiJ9..."
              className="input font-mono text-xs leading-relaxed resize-none"
            />
            <div className="text-[11px] text-ink-500 mt-1">Pega el campo "qrToken" que el estudiante muestra desde su sesión.</div>
          </div>

          <div>
            <label className="label">ID del vehículo</label>
            <input
              type="number" required min="1"
              value={vehiculoId} onChange={(e) => setVeh(e.target.value)}
              placeholder="Ej: 1, 2, 3..."
              className="input"
            />
            <div className="text-[11px] text-ink-500 mt-1">En producción se seleccionaría automáticamente del QR.</div>
          </div>

          <button type="submit" disabled={busy} className="btn-primary w-full">
            {busy ? <><Loader2 size={16} className="animate-spin"/>Validando...</> : <><ScanLine size={16}/>Validar acceso</>}
          </button>
        </form>

        {/* ===== RESULT ===== */}
        <div>
          {!result ? (
            <div className="h-full card p-12 flex flex-col items-center justify-center text-center">
              <div className="relative w-20 h-20 rounded-full bg-ink-100 flex items-center justify-center mb-4">
                <ScanLine className="text-ink-400" size={28}/>
                <div className="absolute inset-0 rounded-full ring-2 ring-ink-200 animate-pulse-ring"/>
              </div>
              <div className="font-display text-2xl text-ink-800">Listo para validar</div>
              <p className="text-ink-500 text-sm mt-2 max-w-xs">
                Los resultados aparecerán aquí. Verifica al estudiante antes de permitir el ingreso.
              </p>
            </div>
          ) : (
            <ResultCard r={result} autorizado={autorizado} onClear={() => { setResult(null); setQr(''); setVeh(''); }} />
          )}
        </div>
      </div>
    </div>
  );
}

function ResultCard({ r, autorizado, onClear }) {
  return (
    <div className={`relative overflow-hidden rounded-2xl ring-1 ring-inset animate-fade-up ${
      autorizado ? 'bg-emerald-500/5 ring-emerald-500/30' : 'bg-rose-500/5 ring-rose-500/30'
    }`}>
      <div className={`absolute -right-20 -top-20 w-60 h-60 rounded-full blur-3xl ${
        autorizado ? 'bg-emerald-400/30' : 'bg-rose-400/30'}`}/>

      <div className="relative p-8">
        <div className="flex items-start gap-4">
          {autorizado
            ? <CheckCircle2 size={56} className="text-emerald-600 shrink-0" strokeWidth={1.5}/>
            : <XCircle size={56} className="text-rose-600 shrink-0" strokeWidth={1.5}/>}
          <div className="flex-1">
            <div className={`text-xs uppercase tracking-widest font-semibold ${autorizado ? 'text-emerald-700' : 'text-rose-700'}`}>
              {r.resultado}
            </div>
            <div className="font-display text-3xl mt-1">{r.mensaje}</div>
            {r.motivoDenegacion && !autorizado && (
              <div className="text-sm text-rose-700 mt-2">Motivo: {r.motivoDenegacion}</div>
            )}
          </div>
        </div>

        {(r.nombreEstudiante || r.placaVehiculo || r.espacioSugerido) && (
          <div className="mt-8 grid grid-cols-2 gap-4">
            {r.nombreEstudiante && <Field icon={User}  label="Estudiante" value={r.nombreEstudiante} sub={r.carne}/>}
            {r.placaVehiculo    && <Field icon={Car}   label="Vehículo"   value={r.placaVehiculo}/>}
            {r.espacioSugerido  && <Field icon={MapPin} label="Espacio sugerido" value={r.espacioSugerido}/>}
            {r.registroId       && <Field icon={Hash}  label="Registro"   value={`#${r.registroId}`}/>}
          </div>
        )}

        <button onClick={onClear} className="mt-8 btn-ghost">Validar otro</button>
      </div>
    </div>
  );
}

function Field({ icon: Icon, label, value, sub }) {
  return (
    <div className="bg-white rounded-xl p-4 ring-1 ring-ink-200">
      <div className="flex items-center gap-1.5 text-[10px] uppercase tracking-widest text-ink-500">
        <Icon size={11}/> {label}
      </div>
      <div className="font-medium text-ink-900 mt-1">{value}</div>
      {sub && <div className="text-xs text-ink-500 mt-0.5 font-mono">{sub}</div>}
    </div>
  );
}
