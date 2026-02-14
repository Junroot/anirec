import { useState } from 'react';
import { FilterBar } from '@/components/filters/FilterBar';
import { AnimeGrid } from '@/components/anime/AnimeGrid';
import { RatingModal } from '@/components/rating/RatingModal';
import { useFilterState } from '@/hooks/useFilterState';
import { mockAnime } from '@/data/mockAnime';
import type { Anime } from '@/types/anime';
import type { WatchStatus } from '@/types/rating';

export function SearchAnimePage() {
  const { filters, updateFilter, resetFilters, filteredAnime } = useFilterState(mockAnime);
  const [ratingAnime, setRatingAnime] = useState<Anime | null>(null);

  const handleRate = (anime: Anime) => setRatingAnime(anime);
  const handleRatingSubmit = (_animeId: number, _score: number, _status: WatchStatus) => {
    // Mock: would save
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold text-text-primary mb-8">Search Anime</h1>
      <div className="space-y-6">
        <FilterBar
          filters={filters}
          onFilterChange={updateFilter}
          onReset={resetFilters}
          resultCount={filteredAnime.length}
        />
        <AnimeGrid anime={filteredAnime} onRate={handleRate} />
      </div>

      <RatingModal
        anime={ratingAnime}
        isOpen={!!ratingAnime}
        onClose={() => setRatingAnime(null)}
        onSubmit={handleRatingSubmit}
      />
    </div>
  );
}
