import { useState } from 'react';
import { HeroSection } from '@/components/home/HeroSection';
import { TrendingSection } from '@/components/home/TrendingSection';
import { SeasonalSection } from '@/components/home/SeasonalSection';
import { TopAnimeList } from '@/components/home/TopAnimeList';
import { RecommendationSection } from '@/components/recommendation/RecommendationSection';
import { RatingModal } from '@/components/rating/RatingModal';
import { useAuth } from '@/hooks/useAuth';
import { mockAnime } from '@/data/mockAnime';
import { mockRecommendations, mockTasteGroup } from '@/data/mockRecommendations';
import type { Anime } from '@/types/anime';
import type { WatchStatus } from '@/types/rating';

export function HomePage() {
  const { isAuthenticated } = useAuth();
  const [ratingAnime, setRatingAnime] = useState<Anime | null>(null);

  const trendingAnime = mockAnime.slice(0, 6);
  const seasonalAnime = mockAnime.slice(6, 12);
  const topAnime = [...mockAnime].sort((a, b) => b.score - a.score);

  const animeMap = new Map(mockAnime.map(a => [a.mal_id, a]));

  const handleRate = (anime: Anime) => setRatingAnime(anime);
  const handleRatingSubmit = (_animeId: number, _score: number, _status: WatchStatus) => {
    // Mock: would save to backend
  };

  return (
    <div>
      <HeroSection />
      <TrendingSection anime={trendingAnime} onRate={handleRate} />
      <SeasonalSection anime={seasonalAnime} onRate={handleRate} />

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

      <TopAnimeList anime={topAnime} />

      <RatingModal
        anime={ratingAnime}
        isOpen={!!ratingAnime}
        onClose={() => setRatingAnime(null)}
        onSubmit={handleRatingSubmit}
      />
    </div>
  );
}
