import { useEffect, useState } from 'react';
import { vehiculosApi } from '../api/client';
import { Car, Bike, Plus, X, Loader2, AlertCircle, CheckCircle2 } from 'lucide-react';

const TIPOS = ['CARRO', 'MOTO'];

export default function MisVehiculos() {
  const [lista, setLista]   = useState([]);
  const [loading, setL]     = useState(true);
  const [modal, setModal]   = useState(false);
  const [success, setOK]    = useState('');

  const cargar = async () => {
    setL(true);
    try { setLista(await vehiculosApi.misVehiculos()); }
    finally { setL(false); }
  };
  useEffect(() => { cargar(); }, []);

  const onRegistrado = (v) => {
    setOK(`Vehículo ${v.placa} registrado correctamente.`);
    setTimeout(() => setOK(''), 4000);
    setModal(false);
    cargar();
  };

  return (
    <div className="animate-fade-up">
      <div className="flex flex-wrap items-end justify-between gap-4 mb-8">
        <div>
          <div className="text-xs uppercase tracking-widest text-ink-500 mb-2">Mis vehículos</div>
          <h1 className="font-display text-4xl">Tu garaje en la universidad.</h1>
        </div>
        <button onClick={() => setModal(true)} className="btn-primary">
          <Plus size={16} /> Registrar vehículo
        </button>
      </div>

      {success && (
        <div className="mb-6 rounded-xl bg-emerald-500/8 ring-1 ring-emerald-500/20 text-emerald-700 text-sm px-4 py-3 flex items-center gap-2">
          <CheckCircle2 size={16}/> {success}
        </div>
      )}

      {loading ? (
        <div className="card p-10 flex items-center justify-center text-ink-500">
          <Loader2 className="animate-spin mr-2" size={18} /> Cargando vehículos...
        </div>
      ) : lista.length === 0 ? (
        <EmptyState onAdd={() => setModal(true)} />
      ) : (
        <div className="grid sm:grid-cols-2 gap-4">
          {lista.map((v) => <VehiculoCard key={v.id} v={v}/>)}
        </div>
      )}

      {modal && <ModalRegistrar onClose={() => setModal(false)} onSaved={onRegistrado}/>}
    </div>
  );
}

function VehiculoCard({ v }) {
  const Icon = v.tipo === 'MOTO' ? Bike : Car;
  return (
    <div className="card p-6 relative overflow-hidden group hover:shadow-lifted transition-shadow">
      <div className="absolute -right-8 -bottom-8 opacity-[0.04] group-hover:opacity-[0.07] transition-opacity">
        <Icon size={180} strokeWidth={1} />
      </div>
      <div className="relative">
        <div className="flex items-center gap-3 mb-4">
          <div className="w-10 h-10 rounded-lg bg-ink-900 text-gold-400 flex items-center justify-center">
            <Icon size={18}/>
          </div>
          <div className="text-xs uppercase tracking-widest text-ink-500">{v.tipo}</div>
        </div>
        <div className="font-mono text-2xl font-semibold tracking-wider">{v.placa}</div>
        <div className="mt-3 text-sm text-ink-600">
          {[v.marca, v.modelo].filter(Boolean).join(' ')}
          {v.anio && <span className="text-ink-400"> · {v.anio}</span>}
        </div>
        {v.color && (
          <div className="mt-3 inline-flex items-center gap-2 text-xs text-ink-500">
            <span className="w-3 h-3 rounded-full ring-1 ring-ink-300"
              style={{ background: stringToColor(v.color) }} />
            {v.color}
          </div>
        )}
      </div>
    </div>
  );
}

function EmptyState({ onAdd }) {
  return (
    <div className="card p-12 text-center">
      <div className="w-14 h-14 rounded-full bg-ink-100 flex items-center justify-center mx-auto mb-4">
        <Car className="text-ink-400"/>
      </div>
      <div className="font-display text-2xl">Aún no registras vehículos</div>
      <p className="text-ink-600 text-sm mt-2 max-w-sm mx-auto">
        Para usar el sistema de acceso necesitas registrar al menos un vehículo activo.
      </p>
      <button onClick={onAdd} className="btn-primary mt-6"><Plus size={16}/> Registrar el primero</button>
    </div>
  );
}

function ModalRegistrar({ onClose, onSaved }) {
  const [form, setForm] = useState({ placa: '', tipo: 'CARRO', marca: '', modelo: '', color: '', anio: '' });
  const [err, setErr]   = useState('');
  const [busy, setBusy] = useState(false);

  const upd = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();
    setErr(''); setBusy(true);
    try {
      const data = { ...form, anio: form.anio ? Number(form.anio) : null };
      const saved = await vehiculosApi.registrar(data);
      onSaved(saved);
    } catch (e2) {
      const msg = e2.response?.data?.mensaje || e2.response?.data?.errores
        ? Object.values(e2.response.data.errores).join(', ')
        : 'No se pudo registrar el vehículo';
      setErr(msg);
    } finally { setBusy(false); }
  };

  return (
    <div className="fixed inset-0 z-50 bg-ink-950/60 backdrop-blur-sm flex items-center justify-center px-4 animate-fade-in" onClick={onClose}>
      <div className="bg-white rounded-2xl shadow-lifted w-full max-w-lg" onClick={(e) => e.stopPropagation()}>
        <div className="flex items-center justify-between px-6 py-4 border-b border-ink-100">
          <div>
            <div className="text-xs uppercase tracking-widest text-ink-500">Nuevo vehículo</div>
            <div className="font-display text-xl">Datos del vehículo</div>
          </div>
          <button onClick={onClose} className="text-ink-400 hover:text-ink-900"><X size={20}/></button>
        </div>
        <form onSubmit={onSubmit} className="p-6 space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Placa *</label>
              <input required value={form.placa} onChange={upd('placa')} className="input font-mono uppercase" placeholder="P-123ABC"/>
            </div>
            <div>
              <label className="label">Tipo *</label>
              <select required value={form.tipo} onChange={upd('tipo')} className="input">
                {TIPOS.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div>
              <label className="label">Marca</label>
              <input value={form.marca} onChange={upd('marca')} className="input" placeholder="Toyota"/>
            </div>
            <div>
              <label className="label">Modelo</label>
              <input value={form.modelo} onChange={upd('modelo')} className="input" placeholder="Yaris"/>
            </div>
            <div>
              <label className="label">Color</label>
              <input value={form.color} onChange={upd('color')} className="input" placeholder="Blanco"/>
            </div>
            <div>
              <label className="label">Año</label>
              <input type="number" min="1980" max="2030" value={form.anio} onChange={upd('anio')} className="input" placeholder="2020"/>
            </div>
          </div>

          {err && (
            <div className="rounded-xl bg-rose-500/5 ring-1 ring-rose-500/20 text-rose-700 text-sm px-4 py-3 flex items-start gap-2">
              <AlertCircle size={16} className="shrink-0 mt-0.5"/> {err}
            </div>
          )}

          <div className="flex gap-3 pt-2">
            <button type="button" onClick={onClose} className="btn-ghost flex-1">Cancelar</button>
            <button type="submit" disabled={busy} className="btn-primary flex-1">
              {busy ? <><Loader2 size={14} className="animate-spin"/>Guardando</> : 'Registrar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

function stringToColor(s) {
  const map = {
    blanco: '#F8FAFC', negro: '#0F172A', gris: '#94A3B8',
    rojo: '#EF4444', azul: '#3B82F6', verde: '#10B981',
    amarillo: '#EAB308', naranja: '#F97316', cafe: '#92400E',
  };
  return map[s?.toLowerCase()] || '#94A3B8';
}
