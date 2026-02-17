import { useState } from 'react';
import { AlertTriangle } from 'lucide-react';
import { FilterBar } from '@/components/filters/FilterBar';
import { AnimeGrid } from '@/components/anime/AnimeGrid';
import { RatingModal } from '@/components/rating/RatingModal';
import { Spinner } from '@/components/ui/Spinner';
import { EmptyState } from '@/components/ui/EmptyState';
import { Button } from '@/components/ui/Button';
import { Pagination } from '@/components/ui/Pagination';
import { useFilterState } from '@/hooks/useFilterState';
import { useAnimeSearch } from '@/hooks/useAnimeSearch';
import type { Anime } from '@/types/anime';

export function SearchAnimePage() {
  const { filters, updateFilter, resetFilters, buildSearchParams } = useFilterState();
  const [page, setPage] = useState(1);
  const [retryKey, setRetryKey] = useState(0);
  const [ratingAnime, setRatingAnime] = useState<Anime | null>(null);

  const searchParams = buildSearchParams(page);
  const { data, pagination, isLoading, error } = useAnimeSearch(searchParams, retryKey);

  const resultCount = pagination?.items?.total ?? data.length;

  const handleFilterChange = (key: Parameters<typeof updateFilter>[0], value: string) => {
    updateFilter(key, value);
    setPage(1);
  };

  const handleReset = () => {
    resetFilters();
    setPage(1);
  };

  const handleRate = (anime: Anime) => setRatingAnime(anime);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold text-on-surface mb-8">Search Anime</h1>
      <div className="space-y-6">
        <FilterBar
          filters={filters}
          onFilterChange={handleFilterChange}
          onReset={handleReset}
          resultCount={resultCount}
        />

        {error ? (
          <EmptyState
            icon={AlertTriangle}
            title="Failed to load anime"
            description={error.message}
            action={
              <Button variant="secondary" onClick={() => setRetryKey(k => k + 1)}>
                Retry
              </Button>
            }
          />
        ) : isLoading && data.length === 0 ? (
          <Spinner size="lg" className="py-20" />
        ) : (
          <div className={isLoading ? 'opacity-50 pointer-events-none' : undefined}>
            <AnimeGrid anime={data} onRate={handleRate} />
          </div>
        )}

        {!error && data.length > 0 && pagination && (
          <Pagination
            currentPage={pagination.current_page}
            lastPage={pagination.last_visible_page}
            hasNextPage={pagination.has_next_page}
            onPageChange={setPage}
          />
        )}
      </div>

      <RatingModal
        anime={ratingAnime}
        isOpen={!!ratingAnime}
        onClose={() => setRatingAnime(null)}
        onSubmit={() => {/* Mock: would save */}}
      />
    </div>
  );
}
