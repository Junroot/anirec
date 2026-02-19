import { useState } from 'react';
import { SORT_PARAM_MAP } from '@/data/constants';
import type { SearchParams } from '@/api/animeApi';

export interface FilterState {
  genre: string;
  format: string;
  status: string;
  producer: string;
  year: string;
  season: string;
  sort: string;
}

const defaultFilters: FilterState = {
  genre: '',
  format: '',
  status: '',
  producer: '',
  year: '',
  season: '',
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
      genres: filters.genre || undefined,
      type: filters.format || undefined,
      status: filters.status || undefined,
      producers: filters.producer || undefined,
      year: filters.year ? Number(filters.year) : undefined,
      season: filters.season || undefined,
      orderBy: sortMapping?.orderBy,
      sort: sortMapping?.sort,
      page,
      limit: 24,
    };
  };

  return { filters, updateFilter, resetFilters, buildSearchParams };
}
