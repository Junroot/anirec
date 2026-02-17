import { useState, useRef, useEffect } from 'react';
import { Search, X, Loader2 } from 'lucide-react';
import type { FilterOption } from '@/api/animeApi';

const DEBOUNCE_MS = 300;
const MIN_QUERY_LENGTH = 2;

interface SearchAutocompleteProps {
  value: string;
  onChange: (value: string) => void;
  placeholder: string;
  searchFn: (query: string, signal?: AbortSignal) => Promise<FilterOption[]>;
}

export function SearchAutocomplete({ value, onChange, placeholder, searchFn }: SearchAutocompleteProps) {
  const [query, setQuery] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [isOpen, setIsOpen] = useState(false);
  const [results, setResults] = useState<FilterOption[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);
  const abortControllerRef = useRef<AbortController | null>(null);

  // Debounced search
  useEffect(() => {
    if (query.length < MIN_QUERY_LENGTH) {
      setResults([]);
      return;
    }

    const timer = setTimeout(() => {
      abortControllerRef.current?.abort();
      const controller = new AbortController();
      abortControllerRef.current = controller;

      setIsLoading(true);

      searchFn(query, controller.signal)
        .then((data) => {
          setResults(data);
        })
        .catch((err: unknown) => {
          if (err instanceof DOMException && err.name === 'AbortError') return;
          setResults([]);
        })
        .finally(() => {
          if (!controller.signal.aborted) {
            setIsLoading(false);
          }
        });
    }, DEBOUNCE_MS);

    return () => {
      clearTimeout(timer);
    };
  }, [query, searchFn]);

  // Cleanup abort controller on unmount
  useEffect(() => {
    return () => {
      abortControllerRef.current?.abort();
    };
  }, []);

  // Reset display when value is cleared externally (e.g., reset filters)
  useEffect(() => {
    if (!value) {
      setDisplayName('');
      setQuery('');
    }
  }, [value]);

  // Close dropdown on outside click
  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSelect = (id: number, name: string) => {
    setDisplayName(name);
    setQuery('');
    setIsOpen(false);
    onChange(String(id));
  };

  const handleClear = () => {
    setDisplayName('');
    setQuery('');
    setIsOpen(false);
    onChange('');
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setQuery(e.target.value);
    setIsOpen(true);
    if (!e.target.value && !displayName) {
      onChange('');
    }
  };

  const handleFocus = () => {
    if (query.length >= MIN_QUERY_LENGTH) {
      setIsOpen(true);
    }
  };

  return (
    <div ref={containerRef} className="relative">
      {displayName ? (
        <div className="flex items-center gap-1.5 px-3 py-2.5 bg-surface-container-high border border-outline-variant rounded-lg text-on-surface text-sm">
          <span className="truncate max-w-[120px]">{displayName}</span>
          <button
            type="button"
            onClick={handleClear}
            className="text-outline hover:text-on-surface transition-colors shrink-0"
            aria-label={`Clear ${placeholder.toLowerCase()}`}
          >
            <X size={14} />
          </button>
        </div>
      ) : (
        <div className="relative">
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-outline pointer-events-none" />
          <input
            type="text"
            value={query}
            onChange={handleInputChange}
            onFocus={handleFocus}
            placeholder={placeholder}
            className="w-full pl-8 pr-3 py-2.5 bg-surface-container-high border border-outline-variant rounded-lg text-on-surface text-sm placeholder:text-outline focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary transition-colors"
            aria-label={`Search ${placeholder.toLowerCase()}`}
          />
          {isLoading && (
            <Loader2 size={14} className="absolute right-3 top-1/2 -translate-y-1/2 text-outline animate-spin" />
          )}
        </div>
      )}

      {isOpen && results.length > 0 && (
        <ul className="absolute z-50 mt-1 w-full max-h-60 overflow-auto bg-surface-container-high border border-outline-variant rounded-lg shadow-lg">
          {results.map((item) => (
            <li key={item.id}>
              <button
                type="button"
                onClick={() => handleSelect(item.id, item.name)}
                className="w-full px-3 py-2 text-left text-sm text-on-surface hover:bg-surface-container-highest transition-colors"
              >
                {item.name}
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
