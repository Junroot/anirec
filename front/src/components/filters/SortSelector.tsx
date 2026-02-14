import { ArrowUpDown } from 'lucide-react';
import { SORT_OPTIONS } from '@/data/constants';

interface SortSelectorProps {
  value: string;
  onChange: (value: string) => void;
}

export function SortSelector({ value, onChange }: SortSelectorProps) {
  return (
    <div className="relative flex items-center">
      <ArrowUpDown size={14} className="absolute left-3 text-text-muted pointer-events-none" />
      <select
        value={value}
        onChange={e => onChange(e.target.value)}
        className="pl-8 pr-3 py-2.5 bg-surface-light border border-surface-lighter rounded-lg text-text-primary text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary transition-colors appearance-none cursor-pointer"
      >
        {SORT_OPTIONS.map(opt => (
          <option key={opt.value} value={opt.value}>{opt.label}</option>
        ))}
      </select>
    </div>
  );
}
