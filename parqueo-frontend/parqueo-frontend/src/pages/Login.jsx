import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { GraduationCap, LockKeyhole, Mail, Loader2, ArrowRight, ParkingCircle } from 'lucide-react';

export default function Login() {
  const [correo, setCorreo]     = useState('');
  const [password, setPassword] = useState('');
  const [error, setError]       = useState('');
  const [loading, setLoading]   = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const onSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(correo, password);
      const dest = location.state?.from?.pathname || '/dashboard';
      navigate(dest, { replace: true });
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Credenciales inválidas');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen grid md:grid-cols-2 bg-ink-50">
      {/* ----- IZQUIERDA: ART ----- */}
      <div className="relative hidden md:flex bg-ink-950 text-ink-100 overflow-hidden">
        {/* Fondo con mesh + grano */}
        <div className="absolute inset-0 bg-mesh-dark" />
        <div className="absolute inset-0 bg-noise opacity-[0.06]" />

        {/* Líneas decorativas estilo blueprint */}
        <svg className="absolute inset-0 w-full h-full opacity-20" preserveAspectRatio="none">
          <defs>
            <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
              <path d="M 40 0 L 0 0 0 40" fill="none" stroke="rgba(234,179,8,0.2)" strokeWidth="0.5"/>
            </pattern>
          </defs>
          <rect width="100%" height="100%" fill="url(#grid)"/>
        </svg>

        {/* Círculo grande dorado */}
        <div className="absolute -right-32 -top-32 w-[480px] h-[480px] rounded-full bg-gradient-to-br from-gold-500/20 to-transparent blur-3xl" />

        {/* Contenido */}
        <div className="relative z-10 flex flex-col justify-between p-12 w-full">
          <div className="flex items-center gap-3">
            <div className="w-11 h-11 rounded-xl bg-gold-400/10 ring-1 ring-gold-400/30 flex items-center justify-center">
              <GraduationCap size={22} className="text-gold-400" />
            </div>
            <div>
              <div className="font-display text-xl leading-none">Parqueo<span className="italic text-gold-400"> UMG</span></div>
              <div className="text-[11px] uppercase tracking-widest text-ink-400 mt-0.5">Universidad Mariano Gálvez</div>
            </div>
          </div>

          {/* Bloque central */}
          <div className="max-w-md animate-fade-up">
            <div className="inline-flex items-center gap-2 text-xs uppercase tracking-widest text-gold-400 mb-4">
              <span className="w-8 h-px bg-gold-400" /> Sistema MVP
            </div>
            <h1 className="font-display text-5xl leading-[1.05] text-balance">
              El acceso al campus,
              <span className="italic text-gold-400"> simplificado.</span>
            </h1>
            <p className="mt-6 text-ink-300 leading-relaxed">
              Marbete digital, código QR personal y validación instantánea.
              Sin filas, sin papelitos, sin sorpresas.
            </p>

            {/* Features */}
            <div className="mt-10 space-y-3">
              {[
                'Tu solvencia, en tiempo real',
                'QR único renovable cada sesión',
                'Sugerencia inteligente de espacio',
              ].map((t) => (
                <div key={t} className="flex items-center gap-3 text-sm">
                  <span className="w-1.5 h-1.5 rounded-full bg-gold-400" />
                  <span className="text-ink-200">{t}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Footer left */}
          <div className="text-xs text-ink-500 flex items-center gap-2">
            <ParkingCircle size={14} />
            <span>Caso 3 · Proyecto académico · 2026</span>
          </div>
        </div>
      </div>

      {/* ----- DERECHA: FORM ----- */}
      <div className="flex items-center justify-center px-6 py-12">
        <div className="w-full max-w-md animate-fade-in">
          <div className="md:hidden flex items-center gap-3 mb-10">
            <div className="w-10 h-10 rounded-xl bg-ink-900 flex items-center justify-center">
              <GraduationCap size={20} className="text-gold-400" />
            </div>
            <div className="font-display text-xl">Parqueo <span className="italic text-gold-500">UMG</span></div>
          </div>

          <div className="mb-8">
            <div className="text-xs uppercase tracking-widest text-ink-500 mb-2">Iniciar sesión</div>
            <h2 className="font-display text-4xl leading-tight">Bienvenido de vuelta.</h2>
            <p className="mt-2 text-ink-600 text-sm">Ingresa con tu correo institucional para continuar.</p>
          </div>

          <form onSubmit={onSubmit} className="space-y-5">
            <div>
              <label className="label">Correo institucional</label>
              <div className="relative">
                <Mail size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-400" />
                <input
                  type="email" required autoComplete="email"
                  value={correo} onChange={(e) => setCorreo(e.target.value)}
                  placeholder="nombre.apellido@miumg.edu.gt"
                  className="input pl-10"
                />
              </div>
            </div>

            <div>
              <label className="label">Contraseña</label>
              <div className="relative">
                <LockKeyhole size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-400" />
                <input
                  type="password" required autoComplete="current-password"
                  value={password} onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••••••"
                  className="input pl-10"
                />
              </div>
            </div>

            {error && (
              <div className="rounded-xl bg-rose-500/5 ring-1 ring-rose-500/20 text-rose-700 text-sm px-4 py-3">
                {error}
              </div>
            )}

            <button type="submit" disabled={loading} className="btn-primary w-full py-3.5">
              {loading ? (<><Loader2 size={16} className="animate-spin" /> Validando...</>)
                       : (<>Ingresar <ArrowRight size={16} /></>)}
            </button>
          </form>

          {/* Demo credentials */}
          <div className="mt-10 rounded-2xl bg-ink-100/70 ring-1 ring-ink-200 p-5">
            <div className="text-[11px] uppercase tracking-widest text-ink-500 mb-3">Credenciales de prueba</div>
            <div className="space-y-2 text-xs font-mono">
              <div className="flex justify-between gap-4">
                <span className="text-ink-700">brandon.jom@miumg.edu.gt</span>
                <span className="text-emerald-700">autorizado</span>
              </div>
              <div className="flex justify-between gap-4">
                <span className="text-ink-700">henry.sicajau@miumg.edu.gt</span>
                <span className="text-rose-700">denegado</span>
              </div>
              <div className="flex justify-between gap-4">
                <span className="text-ink-700">seguridad@miumg.edu.gt</span>
                <span className="text-gold-700">seguridad</span>
              </div>
              <div className="pt-2 mt-2 border-t border-ink-200 text-ink-500">
                Contraseña común: <span className="text-ink-900">Password123.</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
