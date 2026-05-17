import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard, QrCode, Car, BadgeCheck, ShieldCheck,
  MapPinned, LogOut, GraduationCap,
} from 'lucide-react';

function Nav({ to, icon: Icon, children }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm transition-colors ` +
        (isActive
          ? 'bg-gold-400/10 text-gold-300 ring-1 ring-inset ring-gold-400/20'
          : 'text-ink-300 hover:bg-white/5 hover:text-white')
      }
    >
      <Icon size={18} strokeWidth={2} />
      <span>{children}</span>
    </NavLink>
  );
}

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  const initiales = user?.nombreCompleto
    ?.split(' ')
    .slice(0, 2)
    .map((s) => s[0])
    .join('')
    .toUpperCase() ?? 'U';

  const esEstudiante = user?.rol === 'ESTUDIANTE';
  const esSeguridad  = user?.rol === 'SEGURIDAD' || user?.rol === 'ADMIN';

  return (
    <div className="min-h-screen flex bg-ink-50">
      {/* SIDEBAR */}
      <aside className="hidden md:flex w-64 shrink-0 flex-col bg-ink-950 text-ink-100 relative overflow-hidden">
        <div className="absolute inset-0 bg-mesh-dark opacity-60 pointer-events-none" />
        <div className="relative flex flex-col h-full p-5">
          {/* Brand */}
          <div className="flex items-center gap-3 px-2 mb-8">
            <div className="w-9 h-9 rounded-lg bg-gold-400/10 ring-1 ring-gold-400/30 flex items-center justify-center">
              <GraduationCap size={18} className="text-gold-400" />
            </div>
            <div>
              <div className="font-display text-lg leading-none">Parqueo<span className="italic text-gold-400"> UMG</span></div>
              <div className="text-[10px] uppercase tracking-widest text-ink-400">Control de acceso</div>
            </div>
          </div>

          {/* Nav */}
          <nav className="space-y-1 flex-1">
            <div className="text-[10px] uppercase tracking-widest text-ink-500 px-3 mb-2">Principal</div>
            <Nav to="/dashboard" icon={LayoutDashboard}>Resumen</Nav>

            {esEstudiante && (
              <>
                <Nav to="/mi-qr"        icon={QrCode}>Mi código QR</Nav>
                <Nav to="/mi-marbete"   icon={BadgeCheck}>Mi marbete</Nav>
                <Nav to="/mis-vehiculos" icon={Car}>Mis vehículos</Nav>
              </>
            )}

            {esSeguridad && (
              <Nav to="/validar" icon={ShieldCheck}>Validar acceso</Nav>
            )}

            <div className="text-[10px] uppercase tracking-widest text-ink-500 px-3 mb-2 mt-6">Información</div>
            <Nav to="/espacios" icon={MapPinned}>Espacios y afluencia</Nav>
          </nav>

          {/* User card */}
          <div className="mt-4 rounded-xl bg-white/5 ring-1 ring-white/10 p-3">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-full bg-gold-400 text-ink-900 flex items-center justify-center font-semibold text-sm">
                {initiales}
              </div>
              <div className="flex-1 min-w-0">
                <div className="text-sm font-medium truncate">{user?.nombreCompleto}</div>
                <div className="text-[10px] uppercase tracking-widest text-ink-400">{user?.rol}</div>
              </div>
            </div>
            <button onClick={handleLogout}
              className="mt-3 w-full flex items-center justify-center gap-2 rounded-lg bg-white/5 hover:bg-white/10 text-ink-100 text-xs py-2 transition-colors">
              <LogOut size={14} /> Cerrar sesión
            </button>
          </div>
        </div>
      </aside>

      {/* MAIN */}
      <main className="flex-1 min-w-0 flex flex-col">
        {/* Mobile bar */}
        <div className="md:hidden flex items-center justify-between bg-ink-950 text-ink-100 px-4 py-3">
          <div className="flex items-center gap-2">
            <GraduationCap size={18} className="text-gold-400" />
            <span className="font-display">Parqueo UMG</span>
          </div>
          <button onClick={handleLogout} className="text-ink-300 hover:text-white">
            <LogOut size={18} />
          </button>
        </div>

        <div className="flex-1 overflow-auto">
          <div className="max-w-6xl mx-auto px-5 sm:px-8 py-8 md:py-12">
            <Outlet />
          </div>
        </div>
      </main>
    </div>
  );
}
