import { useEffect, useState } from 'react';
import { espaciosApi } from '../api/client';
import { Car, Bike, TrendingUp, Loader2 } from 'lucide-react';

export default function Espacios() {
  const [carros, setCarros]   = useState([]);
  const [motos, setMotos]     = useState([]);
  const [horas, setHoras]     = useState([]);
  const [loading, setL]       = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const [c, m, h] = await Promise.all([
          espaciosApi.disponibles('CARRO'),
          espaciosApi.disponibles('MOTO'),
          espaciosApi.horasAfluencia(),
        ]);
        setCarros(c); setMotos(m); setHoras(h);
      } finally { setL(false); }
    })();
  }, []);

  if (loading) return <div className="flex items-center gap-2 text-ink-500"><Loader2 className="animate-spin" size={16}/>Cargando...</div>;

  const maxCantidad = Math.max(1, ...horas.map((h) => Number(h.cantidadEstudiantes)));

  return (
    <div className="animate-fade-up space-y-10">
      <div>
        <div className="text-xs uppercase tracking-widest text-ink-500 mb-2">Espacios y afluencia</div>
        <h1 className="font-display text-4xl">El campus, en tiempo real.</h1>
      </div>

      {/* KPI cards */}
      <div className="grid sm:grid-cols-2 gap-4">
        <KpiCard icon={Car}  label="Espacios para carros" value={carros.length} sub="activos" zona="A · B · C"/>
        <KpiCard icon={Bike} label="Espacios para motos"  value={motos.length}  sub="activos" zona="MOTOS"/>
      </div>

      {/* Grid de espacios */}
      <div>
        <div className="text-xs uppercase tracking-widest text-ink-500 mb-3">Mapa simplificado</div>
        <div className="card p-6">
          <Zona titulo="Carros" items={carros}/>
          <div className="divider"/>
          <Zona titulo="Motos"  items={motos}/>
        </div>
      </div>

      {/* Horas pico */}
      <div>
        <div className="flex items-center gap-2 text-xs uppercase tracking-widest text-ink-500 mb-3">
          <TrendingUp size={14}/> Horas de mayor afluencia (RF09)
        </div>
        <div className="card p-6">
          {horas.length === 0 ? (
            <div className="text-ink-500 text-sm">No hay datos de horarios registrados aún.</div>
          ) : (
            <div className="space-y-3">
              {horas.map((h) => {
                const cantidad = Number(h.cantidadEstudiantes);
                const hora = String(h.hora).padStart(2, '0');
                const pct = (cantidad / maxCantidad) * 100;
                return (
                  <div key={hora} className="grid grid-cols-[80px,1fr,60px] items-center gap-4">
                    <div className="font-mono text-sm text-ink-700">{hora}:00 h</div>
                    <div className="h-9 bg-ink-100 rounded-lg overflow-hidden">
                      <div
                        className="h-full bg-gradient-to-r from-ink-900 to-ink-700 transition-all duration-700"
                        style={{ width: `${pct}%` }}
                      />
                    </div>
                    <div className="text-right text-sm font-semibold text-ink-900">{cantidad}</div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
        <p className="text-xs text-ink-500 mt-3">
          Basado en los horarios de salida de las inscripciones activas del semestre vigente.
        </p>
      </div>
    </div>
  );
}

function KpiCard({ icon: Icon, label, value, sub, zona }) {
  return (
    <div className="card p-6 flex items-center gap-5">
      <div className="w-12 h-12 rounded-xl bg-ink-900 text-gold-400 flex items-center justify-center">
        <Icon size={20}/>
      </div>
      <div className="flex-1">
        <div className="text-xs uppercase tracking-widest text-ink-500">{label}</div>
        <div className="flex items-baseline gap-2 mt-1">
          <div className="font-display text-4xl">{value}</div>
          <div className="text-xs text-ink-500">{sub}</div>
        </div>
        <div className="text-xs text-ink-400 mt-1 font-mono">{zona}</div>
      </div>
    </div>
  );
}

function Zona({ titulo, items }) {
  return (
    <div>
      <div className="text-xs uppercase tracking-widest text-ink-500 mb-3">{titulo}</div>
      <div className="grid grid-cols-4 sm:grid-cols-6 md:grid-cols-9 gap-2">
        {items.map((e) => (
          <div key={e.id}
            className="aspect-square rounded-lg bg-ink-100 ring-1 ring-inset ring-ink-200 hover:ring-gold-400 hover:bg-gold-400/5 transition-colors flex flex-col items-center justify-center text-center cursor-default">
            <div className="font-mono text-xs font-semibold text-ink-900">{e.codigo}</div>
            <div className="text-[9px] uppercase tracking-wider text-ink-500 mt-0.5">{e.zona}</div>
          </div>
        ))}
      </div>
    </div>
  );
}
