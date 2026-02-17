import { RotateCcw } from 'lucide-react';
import { SearchInput } from './SearchInput';
import { FilterDropdown } from './FilterDropdown';
import { SortSelector } from './SortSelector';
import { Button } from '@/components/ui/Button';
import { GENRES, FORMATS } from '@/data/constants';
import type { FilterState } from '@/hooks/useFilterState';

interface FilterBarProps {
  filters: FilterState;
  onFilterChange: (key: keyof FilterState, value: string) => void;
  onReset: () => void;
  resultCount: number;
}

export function FilterBar({ filters, onFilterChange, onReset, resultCount }: FilterBarProps) {
  const hasActiveFilters = filters.search || filters.genre || filters.format;

  return (
    <div className="space-y-4">
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="flex-1">
          <SearchInput value={filters.search} onChange={v => onFilterChange('search', v)} />
        </div>
        <SortSelector value={filters.sort} onChange={v => onFilterChange('sort', v)} />
      </div>
      <div className="flex flex-wrap gap-2 items-center">
        <FilterDropdown
          label="Genre"
          value={filters.genre}
          onChange={v => onFilterChange('genre', v)}
          options={GENRES}
        />
        <FilterDropdown
          label="Format"
          value={filters.format}
          onChange={v => onFilterChange('format', v)}
          options={FORMATS}
        />
        {hasActiveFilters && (
          <Button variant="ghost" size="sm" onClick={onReset} className="text-on-surface-variant">
            <RotateCcw size={14} /> Reset
          </Button>
        )}
        <span className="text-sm text-on-surface-variant ml-auto">{resultCount} results</span>
      </div>
    </div>
  );
}
