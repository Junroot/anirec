import { RotateCcw } from 'lucide-react';
import { FilterDropdown } from './FilterDropdown';
import { SearchAutocomplete } from './SearchAutocomplete';
import { SortSelector } from './SortSelector';
import { Button } from '@/components/ui/Button';
import { searchGenres, searchProducers } from '@/api/animeApi';
import { FORMATS, STATUSES } from '@/data/constants';
import type { FilterState } from '@/hooks/useFilterState';

interface FilterBarProps {
  filters: FilterState;
  onFilterChange: (key: keyof FilterState, value: string) => void;
  onReset: () => void;
  resultCount: number;
}

export function FilterBar({ filters, onFilterChange, onReset, resultCount }: FilterBarProps) {
  const hasActiveFilters = filters.genre || filters.format || filters.status || filters.producer;

  return (
    <div className="space-y-4">
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="flex-1 flex flex-wrap gap-2">
          <SearchAutocomplete
            value={filters.genre}
            onChange={v => onFilterChange('genre', v)}
            placeholder="Genre"
            searchFn={searchGenres}
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
          />
        </div>
        <SortSelector value={filters.sort} onChange={v => onFilterChange('sort', v)} />
      </div>
      <div className="flex flex-wrap gap-2 items-center">
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
