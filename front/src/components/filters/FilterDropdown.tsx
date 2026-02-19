import { ChevronDown } from 'lucide-react';
import clsx from 'clsx';

interface FilterDropdownProps {
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: { value: string; label: string }[];
}

export function FilterDropdown({ label, value, onChange, options }: FilterDropdownProps) {
  const isActive = !!value;

  return (
    <div className="relative">
      <select
        value={value}
        onChange={e => onChange(e.target.value)}
        className={clsx(
          'w-full pl-3 pr-8 py-2.5 bg-surface-container-high border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary transition-colors appearance-none cursor-pointer',
          isActive
            ? 'border-primary bg-primary/10 text-primary'
            : 'border-outline-variant text-on-surface',
        )}
        aria-label={label}
      >
        <option value="">{label}</option>
        {options.map(opt => (
          <option key={opt.value} value={opt.value}>{opt.label}</option>
        ))}
      </select>
      <ChevronDown
        size={14}
        className={clsx(
          'absolute right-2.5 top-1/2 -translate-y-1/2 pointer-events-none',
          isActive ? 'text-primary' : 'text-outline',
        )}
      />
    </div>
  );
}
