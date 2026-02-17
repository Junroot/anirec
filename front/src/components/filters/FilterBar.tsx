import { RotateCcw } from 'lucide-react';
import { SearchInput } from './SearchInput';
import { FilterDropdown } from './FilterDropdown';
import { SortSelector } from './SortSelector';
import { Button } from '@/components/ui/Button';
import { GENRES, FORMATS, SEASONS } from '@/data/constants';
import type { FilterState } from '@/hooks/useFilterState';

interface FilterBarProps {
  filters: FilterState;
  onFilterChange: (key: keyof FilterState, value: string) => void;
  onReset: () => void;
  resultCount: number;
}

const yearOptions = Array.from({ length: 30 }, (_, i) => {
  const year = new Date().getFullYear() - i;
  return { value: year.toString(), label: year.toString() };
});

export function FilterBar({ filters, onFilterChange, onReset, resultCount }: FilterBarProps) {
  const hasActiveFilters = filters.search || filters.genre || filters.format || filters.season || filters.year;

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
          options={GENRES.map(g => ({ value: g, label: g }))}
        />
        <FilterDropdown
          label="Format"
          value={filters.format}
          onChange={v => onFilterChange('format', v)}
          options={FORMATS.map(f => ({ value: f, label: f }))}
        />
        <FilterDropdown
          label="Season"
          value={filters.season}
          onChange={v => onFilterChange('season', v)}
          options={SEASONS}
        />
        <FilterDropdown
          label="Year"
          value={filters.year}
          onChange={v => onFilterChange('year', v)}
          options={yearOptions}
        />
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
