import clsx from 'clsx';
import { Eye, CheckCircle, Clock, XCircle, Pause } from 'lucide-react';
import type { WatchStatus } from '@/types/rating';

interface WatchStatusSelectorProps {
  value: WatchStatus | null;
  onChange: (status: WatchStatus) => void;
}

const statuses: { value: WatchStatus; label: string; icon: typeof Eye; color: string }[] = [
  { value: 'Watching', label: 'Watching', icon: Eye, color: 'text-info' },
  { value: 'Completed', label: 'Completed', icon: CheckCircle, color: 'text-success' },
  { value: 'Plan to Watch', label: 'Plan to Watch', icon: Clock, color: 'text-tertiary' },
  { value: 'Dropped', label: 'Dropped', icon: XCircle, color: 'text-error' },
  { value: 'On Hold', label: 'On Hold', icon: Pause, color: 'text-warning' },
];

export function WatchStatusSelector({ value, onChange }: WatchStatusSelectorProps) {
  return (
    <div className="space-y-2">
      <span className="text-sm text-on-surface-variant">Watch Status</span>
      <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
        {statuses.map(status => {
          const Icon = status.icon;
          return (
            <button
              key={status.value}
              type="button"
              onClick={() => onChange(status.value)}
              className={clsx(
                'flex items-center gap-2 px-3 py-2 rounded-lg text-sm border transition-all',
                value === status.value
                  ? 'border-primary bg-primary-container text-on-primary-container'
                  : 'border-outline-variant bg-surface-container-high text-on-surface-variant hover:border-outline-variant hover:bg-surface-container-highest'
              )}
            >
              <Icon size={14} className={value === status.value ? 'text-on-primary-container' : status.color} />
              {status.label}
            </button>
          );
        })}
      </div>
    </div>
  );
}
