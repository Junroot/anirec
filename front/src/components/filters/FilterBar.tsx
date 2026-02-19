import { useState } from 'react';
import { RotateCcw, X } from 'lucide-react';
import { FilterDropdown } from './FilterDropdown';
import { SearchAutocomplete } from './SearchAutocomplete';
import { SortSelector } from './SortSelector';
import { Button } from '@/components/ui/Button';
import { searchGenres, searchProducers } from '@/api/animeApi';
import { FORMATS, SEASONS, STATUSES, YEARS } from '@/data/constants';
import type { FilterState } from '@/hooks/useFilterState';

interface FilterBarProps {
  filters: FilterState;
  onFilterChange: (key: keyof FilterState, value: string) => void;
  onReset: () => void;
  resultCount: number;
}

const FILTER_LABELS: Record<string, string> = {
  genre: 'Genre',
  format: 'Format',
  status: 'Status',
  producer: 'Producer',
  year: 'Year',
  season: 'Season',
};

const DROPDOWN_OPTIONS: Record<string, { value: string; label: string }[]> = {
  format: FORMATS,
  status: STATUSES,
  year: YEARS,
  season: SEASONS,
};

function getFilterDisplayValue(key: string, value: string, autocompleteNames: Record<string, string>): string {
  if (key === 'genre' || key === 'producer') {
    return autocompleteNames[key] || value;
  }
  const options = DROPDOWN_OPTIONS[key];
  if (options) {
    const opt = options.find(o => o.value === value);
    return opt?.label ?? value;
  }
  return value;
}

export function FilterBar({ filters, onFilterChange, onReset, resultCount }: FilterBarProps) {
  const [autocompleteNames, setAutocompleteNames] = useState<Record<string, string>>({});

  const handleDisplayNameChange = (key: string) => (name: string) => {
    setAutocompleteNames(prev => ({ ...prev, [key]: name }));
  };

  const activeFilterKeys = (Object.keys(FILTER_LABELS) as (keyof FilterState)[]).filter(
    key => !!filters[key],
  );
  const hasActiveFilters = activeFilterKeys.length > 0;

  return (
    <div className="space-y-3">
      {/* Row 1: Content attributes */}
      <div>
        <span className="text-xs font-medium text-outline uppercase tracking-wider mb-1.5 block">Content</span>
        <div className="flex flex-wrap gap-2">
          <SearchAutocomplete
            value={filters.genre}
            onChange={v => onFilterChange('genre', v)}
            placeholder="Genre"
            searchFn={searchGenres}
            onDisplayNameChange={handleDisplayNameChange('genre')}
          />
          <FilterDropdown
            label="Format"
            value={filters.format}
            onChange={v => onFilterChange('format', v)}
            options={FORMATS}
          />
          <FilterDropdown
            label="Status"
            value={filters.status}
            onChange={v => onFilterChange('status', v)}
            options={STATUSES}
          />
          <SearchAutocomplete
            value={filters.producer}
            onChange={v => onFilterChange('producer', v)}
            placeholder="Producer"
            searchFn={searchProducers}
            onDisplayNameChange={handleDisplayNameChange('producer')}
          />
        </div>
      </div>

      {/* Row 2: Airing period + Sort */}
      <div>
        <span className="text-xs font-medium text-outline uppercase tracking-wider mb-1.5 block">Airing Period</span>
        <div className="flex flex-wrap gap-2 items-center">
          <FilterDropdown
            label="Year"
            value={filters.year}
            onChange={v => onFilterChange('year', v)}
            options={YEARS}
          />
          <FilterDropdown
            label="Season"
            value={filters.season}
            onChange={v => onFilterChange('season', v)}
            options={SEASONS}
          />
          <div className="ml-auto">
            <SortSelector value={filters.sort} onChange={v => onFilterChange('sort', v)} />
          </div>
        </div>
      </div>

      {/* Row 3: Active filter chips + result count */}
      <div className="flex flex-wrap gap-2 items-center">
        {activeFilterKeys.map(key => (
          <span
            key={key}
            className="inline-flex items-center gap-1 px-2.5 py-1 bg-primary/10 border border-primary/30 rounded-full text-xs text-primary"
          >
            {FILTER_LABELS[key]}: {getFilterDisplayValue(key, filters[key], autocompleteNames)}
            <button
              type="button"
              onClick={() => onFilterChange(key, '')}
              className="hover:text-on-surface transition-colors"
              aria-label={`Remove ${FILTER_LABELS[key]} filter`}
            >
              <X size={12} />
            </button>
          </span>
        ))}
        {hasActiveFilters && (
          <Button variant="ghost" size="sm" onClick={onReset} className="text-outline">
            <RotateCcw size={14} /> Reset
          </Button>
        )}
        <span className="text-sm text-outline ml-auto">{resultCount} results</span>
      </div>
    </div>
  );
}
