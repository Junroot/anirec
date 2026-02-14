import { Star } from 'lucide-react';
import { useState } from 'react';
import clsx from 'clsx';

interface RatingInputProps {
  value: number;
  onChange: (value: number) => void;
  max?: number;
}

export function RatingInput({ value, onChange, max = 10 }: RatingInputProps) {
  const [hover, setHover] = useState(0);

  return (
    <div className="flex items-center gap-1">
      {Array.from({ length: max }, (_, i) => i + 1).map(star => (
        <button
          key={star}
          type="button"
          onClick={() => onChange(star)}
          onMouseEnter={() => setHover(star)}
          onMouseLeave={() => setHover(0)}
          className="p-0.5 transition-transform hover:scale-110"
        >
          <Star
            size={20}
            className={clsx(
              'transition-colors',
              (hover || value) >= star
                ? 'text-warning fill-warning'
                : 'text-surface-lighter'
            )}
          />
        </button>
      ))}
      <span className="ml-2 text-sm font-medium text-text-secondary">
        {hover || value || 0}/10
      </span>
    </div>
  );
}
