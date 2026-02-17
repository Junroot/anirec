import { useState } from 'react';
import { SORT_PARAM_MAP } from '@/data/constants';
import type { SearchParams } from '@/api/animeApi';

export interface FilterState {
  search: string;
  genre: string;
  format: string;
  sort: string;
}

const defaultFilters: FilterState = {
  search: '',
  genre: '',
  format: '',
  sort: 'score-desc',
};

export function useFilterState() {
  const [filters, setFilters] = useState<FilterState>(defaultFilters);

  const updateFilter = (key: keyof FilterState, value: string) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  };

  const resetFilters = () => setFilters(defaultFilters);

  const buildSearchParams = (page: number): SearchParams => {
    const sortMapping = SORT_PARAM_MAP[filters.sort];
    return {
      q: filters.search || undefined,
      genres: filters.genre || undefined,
      type: filters.format || undefined,
      orderBy: sortMapping?.orderBy,
      sort: sortMapping?.sort,
      page,
      limit: 24,
    };
  };

  return { filters, updateFilter, resetFilters, buildSearchParams };
}
