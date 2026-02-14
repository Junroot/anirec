import type { LucideIcon } from 'lucide-react';
import clsx from 'clsx';

interface StatSummaryCardProps {
  icon: LucideIcon;
  label: string;
  value: string | number;
  className?: string;
}

export function StatSummaryCard({ icon: Icon, label, value, className }: StatSummaryCardProps) {
  return (
    <div className={clsx('bg-surface rounded-xl border border-surface-lighter p-5', className)}>
      <div className="flex items-center gap-3">
        <div className="p-2.5 rounded-lg bg-primary/10">
          <Icon size={20} className="text-primary" />
        </div>
        <div>
          <p className="text-sm text-text-secondary">{label}</p>
          <p className="text-2xl font-bold text-text-primary">{value}</p>
        </div>
      </div>
    </div>
  );
}
