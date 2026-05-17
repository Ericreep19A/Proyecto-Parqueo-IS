import { useEffect, useState } from 'react';
import { qrApi, estudiantesApi } from '../api/client';
import { RefreshCcw, Clock, Loader2, AlertCircle, Sparkles } from 'lucide-react';

export default function MiQR() {
  const [qr, setQr]         = useState(null);
  const [perfil, setPerfil] = useState(null);
  const [loading, setL]     = useState(true);
  const [error, setError]   = useState('');
  const [timeLeft, setTL]   = useState(0);

  const cargar = async () => {
    setL(true); setError('');
    try {
      const [q, p] = await Promise.all([qrApi.miQr(), perfil || estudiantesApi.miPerfil()]);
      setQr(q);
      if (!perfil) setPerfil(p);
      // Calcula segundos restantes hasta expiracion
      const expira = new Date(q.expiraEn);
      setTL(Math.max(0, Math.floor((expira - new Date()) / 1000)));
    } catch (e) {
      setError(e.response?.data?.mensaje || 'No se pudo generar el código QR. Verifica que tengas un marbete vigente.');
    } finally { setL(false); }
  };

  useEffect(() => { cargar(); }, []); // eslint-disable-line

  // tick del cronometro
  useEffect(() => {
    if (timeLeft <= 0) return;
    const t = setInterval(() => setTL((s) => Math.max(0, s - 1)), 1000);
    return () => clearInterval(t);
  }, [timeLeft]);

  const mm = String(Math.floor(timeLeft / 60)).padStart(2, '0');
  const ss = String(timeLeft % 60).padStart(2, '0');
  const expirado = timeLeft <= 0 && qr;

  return (
    <div className="animate-fade-up max-w-4xl mx-auto">
      <div className="mb-8">
        <div className="text-xs uppercase tracking-widest text-ink-500 mb-2">Mi código QR</div>
        <h1 className="font-display text-4xl">Tu pase de ingreso.</h1>
        <p className="text-ink-600 mt-2 text-sm">El QR se renueva cada sesión. Muéstralo al guardia al entrar.</p>
      </div>

      {error && (
        <div className="card p-6 flex items-start gap-3 border-l-4 border-rose-500">
          <AlertCircle className="text-rose-600 shrink-0" size={20} />
          <div>
            <div className="font-medium text-ink-900">No fue posible generar el QR</div>
            <div className="text-sm text-ink-600 mt-1">{error}</div>
          </div>
        </div>
      )}

      {loading && (
        <div className="card p-10 flex items-center justify-center text-ink-500">
          <Loader2 className="animate-spin mr-2" size={18} /> Generando código...
        </div>
      )}

      {!loading && qr && perfil && (
        <div className="grid lg:grid-cols-[1fr,360px] gap-6">
          {/* ====== BOARDING PASS ====== */}
          <div className="relative">
            <div className="relative bg-ink-950 text-ink-50 rounded-3xl overflow-hidden shadow-lifted">
              {/* fondo */}
              <div className="absolute inset-0 bg-mesh-dark opacity-70" />
              <div className="absolute inset-0 bg-noise opacity-[0.05]" />

              {/* dotted line con notches */}
              <div className="absolute left-0 right-0 top-[58%] flex items-center pointer-events-none">
                <div className="w-5 h-5 -ml-2.5 rounded-full bg-ink-50" />
                <div className="flex-1 border-t border-dashed border-white/20" />
                <div className="w-5 h-5 -mr-2.5 rounded-full bg-ink-50" />
              </div>

              <div className="relative p-8 md:p-10">
                {/* header */}
                <div className="flex items-start justify-between">
                  <div>
                    <div className="text-[10px] uppercase tracking-widest text-gold-400">Pase de ingreso</div>
                    <div className="font-display text-3xl mt-1">{perfil.nombreCompleto}</div>
                  </div>
                  <div className="text-right">
                    <div className="text-[10px] uppercase tracking-widest text-ink-400">Carné</div>
                    <div className="font-mono text-lg mt-1">{perfil.carne}</div>
                  </div>
                </div>

                {/* meta */}
                <div className="grid grid-cols-3 gap-6 mt-8">
                  <Meta label="Carrera" value={perfil.carrera} />
                  <Meta label="Ingreso" value={String(perfil.fechaIngreso).slice(0, 4)} />
                  <Meta label="Estado" value={
                    <span className="inline-flex items-center gap-1.5 text-emerald-400">
                      <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"/> Activo
                    </span>
                  } />
                </div>

                {/* QR area */}
                <div className="mt-10 flex items-center gap-8 flex-wrap">
                  <div className="relative">
                    {/* glow */}
                    <div className="absolute -inset-3 bg-gold-400/30 blur-2xl rounded-full" />
                    <div className={`relative bg-white p-3 rounded-2xl ${expirado ? 'grayscale opacity-50' : ''}`}>
                      <img
                        src={`data:image/png;base64,${qr.qrImagenBase64}`}
                        alt="Código QR"
                        className="w-44 h-44 block"
                      />
                    </div>
                  </div>

                  <div className="flex-1 min-w-[200px]">
                    <div className="text-[10px] uppercase tracking-widest text-ink-400">Vigencia</div>
                    <div className="flex items-center gap-2 font-display text-4xl mt-1 tabular-nums">
                      <Clock size={20} className={expirado ? 'text-rose-400' : 'text-gold-400'} />
                      <span className={expirado ? 'text-rose-400' : ''}>{mm}:{ss}</span>
                    </div>
                    <div className="text-xs text-ink-400 mt-2">
                      {expirado ? 'Este QR ya expiró. Genera uno nuevo.' : 'Tiempo restante para usarse'}
                    </div>

                    <button
                      onClick={cargar}
                      className="mt-5 inline-flex items-center gap-2 rounded-xl bg-gold-400 hover:bg-gold-300 text-ink-900 text-sm font-semibold px-4 py-2.5 transition-colors">
                      <RefreshCcw size={14} /> {expirado ? 'Generar nuevo' : 'Regenerar QR'}
                    </button>
                  </div>
                </div>

                {/* footer */}
                <div className="absolute bottom-4 left-10 right-10 flex items-center justify-between text-[10px] uppercase tracking-widest text-ink-500 pt-6">
                  <span>Parqueo UMG · Caso 3</span>
                  <span className="font-mono">{qr.qrToken.slice(0, 12)}...{qr.qrToken.slice(-6)}</span>
                </div>
              </div>
            </div>
          </div>

          {/* ====== TIPS ====== */}
          <div className="space-y-4">
            <div className="card p-5">
              <div className="flex items-center gap-2 text-xs uppercase tracking-widest text-ink-500 mb-3">
                <Sparkles size={14} className="text-gold-500" /> Cómo usar
              </div>
              <ol className="space-y-3 text-sm text-ink-700">
                <Step n="1">Llega a la garita en tu vehículo.</Step>
                <Step n="2">Muestra el QR al guardia desde la pantalla.</Step>
                <Step n="3">Espera la confirmación visual antes de avanzar.</Step>
              </ol>
            </div>

            <div className="card bg-gold-500/8 ring-1 ring-gold-500/20 p-5">
              <div className="font-medium text-ink-900 text-sm">¿No se ve bien tu QR?</div>
              <div className="text-xs text-ink-700 mt-1">Aumenta el brillo de la pantalla y mantén el código firme frente al lector.</div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

function Meta({ label, value }) {
  return (
    <div>
      <div className="text-[10px] uppercase tracking-widest text-ink-400">{label}</div>
      <div className="text-sm mt-1 text-ink-100">{value}</div>
    </div>
  );
}

function Step({ n, children }) {
  return (
    <li className="flex gap-3">
      <span className="shrink-0 w-6 h-6 rounded-full bg-ink-900 text-gold-400 font-mono text-xs flex items-center justify-center">{n}</span>
      <span>{children}</span>
    </li>
  );
}
