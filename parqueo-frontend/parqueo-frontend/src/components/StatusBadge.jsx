import { CheckCircle2, XCircle, AlertCircle, Clock } from 'lucide-react';

const variants = {
  ok:   { cls: 'bg-emerald-500/10 text-emerald-700 ring-emerald-500/20',  Icon: CheckCircle2, label: 'Vigente' },
  err:  { cls: 'bg-rose-500/10 text-rose-700 ring-rose-500/20',           Icon: XCircle,      label: 'No vigente' },
  warn: { cls: 'bg-gold-500/10 text-gold-700 ring-gold-500/20',           Icon: AlertCircle,  label: 'Pendiente' },
  info: { cls: 'bg-ink-100 text-ink-700 ring-ink-200',                    Icon: Clock,        label: 'En proceso' },
};

export default function StatusBadge({ variant = 'ok', children, icon = true }) {
  const v = variants[variant];
  const Icon = v.Icon;
  return (
    <span className={`pill ring-1 ring-inset ${v.cls}`}>
      {icon && <Icon size={14} strokeWidth={2.5} />}
      {children || v.label}
    </span>
  );
}
