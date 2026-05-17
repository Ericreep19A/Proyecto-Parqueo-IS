import { useEffect, useState } from 'react';
import { marbetesApi } from '../api/client';
import StatusBadge from '../components/StatusBadge';
import { BadgeCheck, AlertCircle, Loader2, Sparkles, Calendar, Hash } from 'lucide-react';

export default function MiMarbete() {
  const [marbete, setMarbete] = useState(null);
  const [loading, setL]       = useState(true);
  const [err, setErr]         = useState('');
  const [genBusy, setGen]     = useState(false);

  const cargar = async () => {
    setL(true); setErr('');
    try { setMarbete(await marbetesApi.miMarbete()); }
    catch (e) { setErr(e.response?.data?.mensaje || 'No se pudo cargar el marbete'); }
    finally { setL(false); }
  };

  useEffect(() => { cargar(); }, []);

  const generar = async () => {
    setGen(true); setErr('');
    try { setMarbete(await marbetesApi.generar()); }
    catch (e) { setErr(e.response?.data?.mensaje || 'No se pudo generar el marbete'); }
    finally { setGen(false); }
  };

  return (
    <div className="animate-fade-up max-w-3xl">
      <div className="mb-8">
        <div className="text-xs uppercase tracking-widest text-ink-500 mb-2">Marbete digital</div>
        <h1 className="font-display text-4xl">Tu permiso de estacionamiento.</h1>
      </div>

      {loading && (
        <div className="card p-10 flex items-center justify-center text-ink-500">
          <Loader2 className="animate-spin mr-2" size={18}/> Cargando...
        </div>
      )}

      {!loading && !marbete && (
        <div className="card p-10 text-center">
          <div className="w-14 h-14 rounded-full bg-ink-100 flex items-center justify-center mx-auto mb-4">
            <AlertCircle className="text-ink-500"/>
          </div>
          <div className="font-display text-2xl">No tienes un marbete activo</div>
          <p className="text-ink-600 text-sm mt-2 max-w-md mx-auto">
            {err || 'Necesitas tener un pago de estacionamiento vigente para generar tu marbete.'}
          </p>
          <button onClick={generar} disabled={genBusy} className="btn-primary mt-6">
            {genBusy ? <><Loader2 size={14} className="animate-spin"/>Generando</> : <><Sparkles size={14}/>Intentar generar</>}
          </button>
        </div>
      )}

      {!loading && marbete && (
        <div className="relative">
          {/* Marbete tipo permiso */}
          <div className="relative bg-gradient-to-br from-ink-950 to-ink-800 text-ink-50 rounded-3xl overflow-hidden shadow-lifted">
            <div className="absolute inset-0 bg-mesh-dark opacity-40"/>
            <div className="absolute inset-0 bg-noise opacity-[0.04]"/>

            {/* sello */}
            <div className="absolute -top-12 -right-12 w-64 h-64 rounded-full ring-2 ring-gold-400/20 flex items-center justify-center rotate-12">
              <div className="w-56 h-56 rounded-full ring-1 ring-gold-400/15 flex items-center justify-center">
                <div className="font-display italic text-gold-400/40 text-2xl rotate-12">UMG · 2026</div>
              </div>
            </div>

            <div className="relative p-8 md:p-10">
              <div className="flex items-start justify-between mb-8 flex-wrap gap-4">
                <div>
                  <div className="text-[10px] uppercase tracking-widest text-gold-400">Marbete digital</div>
                  <div className="font-display text-3xl mt-1">Permiso oficial</div>
                  <div className="text-xs text-ink-400 mt-1">Universidad Mariano Gálvez · Semestre {marbete.semestre}</div>
                </div>
                <StatusBadge variant={marbete.vigente ? 'ok' : 'err'}>
                  {marbete.vigente ? 'Vigente' : 'No vigente'}
                </StatusBadge>
              </div>

              <div className="grid sm:grid-cols-2 gap-6 mt-10">
                <div>
                  <div className="text-[10px] uppercase tracking-widest text-ink-400 flex items-center gap-1.5">
                    <Hash size={11}/> Código único
                  </div>
                  <div className="font-mono text-xl mt-1 tracking-wider">{marbete.codigoUnico}</div>
                </div>
                <div>
                  <div className="text-[10px] uppercase tracking-widest text-ink-400 flex items-center gap-1.5">
                    <Calendar size={11}/> Vigencia
                  </div>
                  <div className="mt-1">
                    <span className="text-ink-200 text-sm">{fmt(marbete.fechaVigenciaInicio)}</span>
                    <span className="text-ink-500 mx-2">→</span>
                    <span className="text-ink-200 text-sm">{fmt(marbete.fechaVigenciaFin)}</span>
                  </div>
                </div>
                <div>
                  <div className="text-[10px] uppercase tracking-widest text-ink-400">Emitido</div>
                  <div className="text-sm text-ink-200 mt-1">{fmt(marbete.fechaEmision, true)}</div>
                </div>
                <div>
                  <div className="text-[10px] uppercase tracking-widest text-ink-400">Estado</div>
                  <div className="text-sm text-ink-200 mt-1">{marbete.estado}</div>
                </div>
              </div>

              <div className="mt-10 pt-6 border-t border-white/10 flex items-center gap-3">
                <BadgeCheck className="text-gold-400"/>
                <p className="text-xs text-ink-400 leading-relaxed">
                  Este marbete es personal e intransferible. Permite el ingreso a cualquier zona de estacionamiento autorizada
                  durante el período de vigencia.
                </p>
              </div>
            </div>
          </div>
        </div>
      )}

      {err && marbete && (
        <div className="mt-4 rounded-xl bg-rose-500/5 ring-1 ring-rose-500/20 text-rose-700 text-sm px-4 py-3">{err}</div>
      )}
    </div>
  );
}

function fmt(s, withTime = false) {
  if (!s) return '—';
  const d = new Date(s);
  const date = d.toLocaleDateString('es-GT', { day: '2-digit', month: 'short', year: 'numeric' });
  if (!withTime) return date;
  return `${date} · ${d.toLocaleTimeString('es-GT', { hour: '2-digit', minute: '2-digit' })}`;
}
