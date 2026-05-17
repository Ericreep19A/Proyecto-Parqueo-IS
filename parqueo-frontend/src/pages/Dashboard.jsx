import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { estudiantesApi, vehiculosApi } from '../api/client';
import StatusBadge from '../components/StatusBadge';
import {
  ShieldCheck, ShieldAlert, QrCode, Car, BadgeCheck, ArrowUpRight,
  GraduationCap, MapPinned, Loader2, ScanLine,
} from 'lucide-react';

function CheckRow({ ok, label }) {
  return (
    <div className="flex items-center justify-between py-3 border-b border-ink-100 last:border-0">
      <span className="text-sm text-ink-700">{label}</span>
      <StatusBadge variant={ok ? 'ok' : 'err'}>{ok ? 'Cumple' : 'Pendiente'}</StatusBadge>
    </div>
  );
}

export default function Dashboard() {
  const { user } = useAuth();
  const [perfil, setPerfil]     = useState(null);
  const [solvencia, setSolv]    = useState(null);
  const [vehiculos, setVehs]    = useState([]);
  const [loading, setLoading]   = useState(true);
  const [err, setErr]           = useState('');

  const esEstudiante = user?.rol === 'ESTUDIANTE';
  const esSeguridad  = user?.rol === 'SEGURIDAD' || user?.rol === 'ADMIN';

  useEffect(() => {
    if (!esEstudiante) { setLoading(false); return; }
    (async () => {
      try {
        const [p, s, v] = await Promise.allSettled([
          estudiantesApi.miPerfil(),
          estudiantesApi.miSolvencia(),
          vehiculosApi.misVehiculos(),
        ]);
        if (p.status === 'fulfilled') setPerfil(p.value);
        if (s.status === 'fulfilled') setSolv(s.value);
        if (v.status === 'fulfilled') setVehs(v.value);
      } catch (e) { setErr(e.message); }
      finally { setLoading(false); }
    })();
  }, [esEstudiante]);

  if (loading) return <div className="flex items-center gap-2 text-ink-500"><Loader2 className="animate-spin" size={16}/> Cargando...</div>;

  /* ============ VISTA SEGURIDAD ============ */
  if (esSeguridad) {
    return (
      <div className="animate-fade-up">
        <div className="mb-8">
          <div className="text-xs uppercase tracking-widest text-ink-500 mb-2">Panel de seguridad</div>
          <h1 className="font-display text-4xl">Hola, <span className="italic">{user?.nombreCompleto}</span>.</h1>
        </div>
        <Link to="/validar" className="card block p-8 hover:shadow-lifted transition-shadow group">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-5">
              <div className="w-14 h-14 rounded-xl bg-ink-900 flex items-center justify-center">
                <ScanLine className="text-gold-400" />
              </div>
              <div>
                <div className="font-display text-2xl">Validar acceso vehicular</div>
                <p className="text-ink-600 text-sm mt-1">Pega un QR y selecciona el vehículo del estudiante.</p>
              </div>
            </div>
            <ArrowUpRight className="text-ink-400 group-hover:text-ink-900 transition-colors" />
          </div>
        </Link>
      </div>
    );
  }

  /* ============ VISTA ESTUDIANTE ============ */
  const autorizado = solvencia?.autorizadoIngreso;

  return (
    <div className="animate-fade-up space-y-8">
      {/* Encabezado */}
      <div>
        <div className="text-xs uppercase tracking-widest text-ink-500 mb-2">Resumen del estudiante</div>
        <div className="flex flex-wrap items-end justify-between gap-4">
          <h1 className="font-display text-4xl leading-tight">
            Hola, <span className="italic">{perfil?.nombreCompleto?.split(' ')[0] || 'estudiante'}</span>.
          </h1>
          {perfil && (
            <div className="flex items-center gap-2 text-xs text-ink-500 font-mono">
              <GraduationCap size={14} /> Carné {perfil.carne}
            </div>
          )}
        </div>
      </div>

      {/* HERO: Estado de elegibilidad */}
      {solvencia && (
        <div className={`relative overflow-hidden rounded-3xl p-8 md:p-10 ring-1 ring-inset ${
          autorizado
            ? 'bg-gradient-to-br from-emerald-500/8 to-ink-50 ring-emerald-500/20'
            : 'bg-gradient-to-br from-rose-500/8 to-ink-50 ring-rose-500/20'
        }`}>
          {/* halo */}
          <div className={`absolute -right-32 -top-32 w-80 h-80 rounded-full blur-3xl ${
            autorizado ? 'bg-emerald-400/20' : 'bg-rose-400/20'}`} />

          <div className="relative grid md:grid-cols-3 gap-8 md:gap-12 items-start">
            {/* Status */}
            <div className="md:col-span-1">
              <div className="text-xs uppercase tracking-widest text-ink-500 mb-2">Semestre {solvencia.semestre}</div>
              <div className="flex items-center gap-3 mb-3">
                {autorizado ? <ShieldCheck size={32} className="text-emerald-600"/>
                            : <ShieldAlert size={32} className="text-rose-600"/>}
                <div className="font-display text-3xl leading-none">
                  {autorizado ? 'Autorizado' : 'Denegado'}
                </div>
              </div>
              <p className="text-ink-600 text-sm leading-relaxed">{solvencia.mensaje}</p>
            </div>

            {/* Requisitos */}
            <div className="md:col-span-2">
              <div className="text-xs uppercase tracking-widest text-ink-500 mb-3">Verificación de requisitos</div>
              <div className="bg-white rounded-2xl ring-1 ring-ink-200 px-5">
                <CheckRow ok={solvencia.inscritoEnSemestre}        label="Inscripción en semestre vigente" />
                <CheckRow ok={solvencia.solventeAcademico}         label="Solvencia académica" />
                <CheckRow ok={solvencia.solventeFinanciero}        label="Solvencia financiera" />
                <CheckRow ok={solvencia.pagoEstacionamientoVigente} label="Pago de estacionamiento" />
                <CheckRow ok={solvencia.marbeteVigente}            label="Marbete digital activo" />
              </div>
            </div>
          </div>
        </div>
      )}

      {err && <div className="rounded-xl bg-rose-500/5 ring-1 ring-rose-500/20 text-rose-700 text-sm px-4 py-3">{err}</div>}

      {/* Quick access tiles */}
      <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <Tile to="/mi-qr"        icon={QrCode}      title="Mi código QR"       desc="Genera tu QR de ingreso" accent />
        <Tile to="/mi-marbete"   icon={BadgeCheck}  title="Mi marbete"         desc={solvencia?.marbeteVigente ? 'Marbete vigente' : 'Sin marbete activo'} />
        <Tile to="/mis-vehiculos" icon={Car}        title={`${vehiculos.length} vehículo${vehiculos.length===1?'':'s'}`} desc="Gestiona tus vehículos" />
        <Tile to="/espacios"     icon={MapPinned}   title="Espacios"           desc="Disponibilidad y horas pico" />
      </div>
    </div>
  );
}

function Tile({ to, icon: Icon, title, desc, accent }) {
  return (
    <Link to={to}
      className={`group card p-5 hover:shadow-lifted transition-shadow ${accent ? 'bg-ink-900 text-ink-50 ring-0' : ''}`}>
      <div className="flex items-start justify-between">
        <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${
          accent ? 'bg-gold-400/10 ring-1 ring-gold-400/30' : 'bg-ink-100'}`}>
          <Icon size={18} className={accent ? 'text-gold-400' : 'text-ink-700'} />
        </div>
        <ArrowUpRight size={16} className={accent ? 'text-ink-400 group-hover:text-gold-400' : 'text-ink-300 group-hover:text-ink-900'} />
      </div>
      <div className="mt-5">
        <div className={`font-medium ${accent ? 'text-ink-50' : 'text-ink-900'}`}>{title}</div>
        <div className={`text-xs mt-1 ${accent ? 'text-ink-400' : 'text-ink-500'}`}>{desc}</div>
      </div>
    </Link>
  );
}
