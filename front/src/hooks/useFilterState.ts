import { useState, useMemo } from 'react';
import type { Anime } from '@/types/anime';

export interface FilterState {
  search: string;
  genre: string;
  format: string;
  season: string;
  year: string;
  sort: string;
}

const defaultFilters: FilterState = {
  search: '',
  genre: '',
  format: '',
  season: '',
  year: '',
  sort: 'score-desc',
};

export function useFilterState(animeList: Anime[]) {
  const [filters, setFilters] = useState<FilterState>(defaultFilters);

  const updateFilter = (key: keyof FilterState, value: string) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  };

  const resetFilters = () => setFilters(defaultFilters);

  const filteredAnime = useMemo(() => {
    let result = [...animeList];

    if (filters.search) {
      const q = filters.search.toLowerCase();
      result = result.filter(a =>
        a.title.toLowerCase().includes(q) ||
        a.title_japanese?.toLowerCase().includes(q)
      );
    }
    if (filters.genre) {
      result = result.filter(a => a.genres.some(g => g.name === filters.genre));
    }
    if (filters.format) {
      result = result.filter(a => a.type === filters.format);
    }
    if (filters.season) {
      result = result.filter(a => a.season?.toLowerCase() === filters.season);
    }
    if (filters.year) {
      result = result.filter(a => a.year?.toString() === filters.year);
    }

    switch (filters.sort) {
      case 'score-desc':
        result.sort((a, b) => b.score - a.score);
        break;
      case 'score-asc':
        result.sort((a, b) => a.score - b.score);
        break;
      case 'title-asc':
        result.sort((a, b) => a.title.localeCompare(b.title));
        break;
      case 'title-desc':
        result.sort((a, b) => b.title.localeCompare(a.title));
        break;
      case 'popularity-asc':
        result.sort((a, b) => a.popularity - b.popularity);
        break;
      case 'newest':
        result.sort((a, b) => (b.year ?? 0) - (a.year ?? 0));
        break;
    }

    return result;
  }, [animeList, filters]);

  return { filters, updateFilter, resetFilters, filteredAnime };
}
