import { useState } from 'react';
import { AlertTriangle } from 'lucide-react';
import { FilterBar } from '@/components/filters/FilterBar';
import { AnimeGrid } from '@/components/anime/AnimeGrid';
import { RatingModal, animeToRatingTarget } from '@/components/rating/RatingModal';
import type { RatingTarget } from '@/components/rating/RatingModal';
import { Spinner } from '@/components/ui/Spinner';
import { EmptyState } from '@/components/ui/EmptyState';
import { Button } from '@/components/ui/Button';
import { Pagination } from '@/components/ui/Pagination';
import { useFilterState } from '@/hooks/useFilterState';
import { useAnimeSearch } from '@/hooks/useAnimeSearch';
import { useAuth } from '@/hooks/useAuth';
import { upsertRating } from '@/api/ratingApi';
import type { Anime } from '@/types/anime';
import type { WatchStatus } from '@/types/rating';

export function SearchAnimePage() {
  const { filters, updateFilter, resetFilters, buildSearchParams } = useFilterState();
  const { isAuthenticated } = useAuth();
  const [page, setPage] = useState(1);
  const [retryKey, setRetryKey] = useState(0);
  const [ratingTarget, setRatingTarget] = useState<RatingTarget | null>(null);
  const [submitting, setSubmitting] = useState(false);

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

  const handleRate = (anime: Anime) => setRatingTarget(animeToRatingTarget(anime));

  const handleSubmit = async (animeId: number, score: number, watchStatus: WatchStatus) => {
    setSubmitting(true);
    try {
      await upsertRating(animeId, score, watchStatus);
      setRatingTarget(null);
    } catch {
      // API error — modal stays open so user can retry
    } finally {
      setSubmitting(false);
    }
  };

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
            <AnimeGrid anime={data} onRate={isAuthenticated ? handleRate : undefined} />
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
        target={ratingTarget}
        isOpen={!!ratingTarget}
        onClose={() => setRatingTarget(null)}
        onSubmit={handleSubmit}
        submitting={submitting}
      />
    </div>
  );
}
