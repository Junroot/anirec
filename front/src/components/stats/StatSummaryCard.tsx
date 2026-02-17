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
    <div className={clsx('bg-surface-container rounded-xl border border-outline-variant p-5', className)}>
      <div className="flex items-center gap-3">
        <div className="p-2.5 rounded-lg bg-primary-container">
          <Icon size={20} className="text-primary" />
        </div>
        <div>
          <p className="text-sm text-on-surface-variant">{label}</p>
          <p className="text-2xl font-bold text-on-surface">{value}</p>
        </div>
      </div>
    </div>
  );
}
