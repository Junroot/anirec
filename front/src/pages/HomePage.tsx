import { useState } from 'react';
import { HeroSection } from '@/components/home/HeroSection';
import { TrendingSection } from '@/components/home/TrendingSection';
import { SeasonalSection } from '@/components/home/SeasonalSection';
import { TopAnimeList } from '@/components/home/TopAnimeList';
import { RecommendationSection } from '@/components/recommendation/RecommendationSection';
import { RatingModal, animeToRatingTarget } from '@/components/rating/RatingModal';
import type { RatingTarget } from '@/components/rating/RatingModal';
import { useAuth } from '@/hooks/useAuth';
import { useSeasonalAnime } from '@/hooks/useSeasonalAnime';
import { useTopAnime } from '@/hooks/useTopAnime';
import { mockAnime } from '@/data/mockAnime';
import { mockRecommendations, mockTasteGroup } from '@/data/mockRecommendations';
import { upsertRating } from '@/api/ratingApi';
import type { Anime } from '@/types/anime';
import type { WatchStatus } from '@/types/rating';

const SEASONS = ['winter', 'spring', 'summer', 'fall'] as const;

export function HomePage() {
  const { isAuthenticated } = useAuth();
  const [ratingTarget, setRatingTarget] = useState<RatingTarget | null>(null);

  const currentYear = new Date().getFullYear();
  const currentSeason = SEASONS[Math.floor(new Date().getMonth() / 3)];

  const { data: trendingAnime, isLoading: trendingLoading, error: trendingError } =
    useSeasonalAnime(undefined, undefined, 1, 6);
  const { data: seasonalAnime, isLoading: seasonalLoading, error: seasonalError } =
    useSeasonalAnime(currentYear, currentSeason, 1, 6);
  const { data: topAnime, isLoading: topLoading, error: topError } =
    useTopAnime(1, 10);

  // mockAnime is still needed for RecommendationSection until recommendation API is implemented
  const animeMap = new Map(mockAnime.map(a => [a.mal_id, a]));

  const [submitting, setSubmitting] = useState(false);

  const handleRate = (anime: Anime) => setRatingTarget(animeToRatingTarget(anime));
  const handleRatingSubmit = async (animeId: number, score: number, status: WatchStatus) => {
    setSubmitting(true);
    try {
      await upsertRating(animeId, score, status);
      setRatingTarget(null);
    } catch {
      // API error — modal stays open so user can retry
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <HeroSection />
      <TrendingSection anime={trendingAnime} onRate={handleRate} isLoading={trendingLoading} error={trendingError} />
      <SeasonalSection anime={seasonalAnime} onRate={handleRate} isLoading={seasonalLoading} error={seasonalError} />

      {isAuthenticated && (
        <section className="py-10">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <RecommendationSection
              recommendations={mockRecommendations}
              animeMap={animeMap}
              tasteGroup={mockTasteGroup}
              onFeedback={() => {}}
              onRate={handleRate}
            />
          </div>
        </section>
      )}

      <TopAnimeList anime={topAnime} isLoading={topLoading} error={topError} />

      <RatingModal
        target={ratingTarget}
        isOpen={!!ratingTarget}
        onClose={() => setRatingTarget(null)}
        onSubmit={handleRatingSubmit}
        submitting={submitting}
      />
    </div>
  );
}
