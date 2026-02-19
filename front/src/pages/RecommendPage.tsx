import { useState } from 'react';
import { RecommendationSection } from '@/components/recommendation/RecommendationSection';
import { RatingModal, animeToRatingTarget } from '@/components/rating/RatingModal';
import type { RatingTarget } from '@/components/rating/RatingModal';
import { mockAnime } from '@/data/mockAnime';
import { mockRecommendations, mockTasteGroup } from '@/data/mockRecommendations';
import { upsertRating } from '@/api/ratingApi';
import type { Anime } from '@/types/anime';
import type { Recommendation } from '@/types/recommendation';
import type { WatchStatus } from '@/types/rating';

export function RecommendPage() {
  const [ratingTarget, setRatingTarget] = useState<RatingTarget | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [recommendations, setRecommendations] = useState<Recommendation[]>(mockRecommendations);

  const animeMap = new Map(mockAnime.map(a => [a.mal_id, a]));

  const handleFeedback = (id: string, type: 'like' | 'dislike') => {
    setRecommendations(prev =>
      prev.map(r => r.id === id ? { ...r, feedback: r.feedback === type ? null : type } : r)
    );
  };

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
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold text-text-primary mb-8">Your Recommendations</h1>
      <RecommendationSection
        recommendations={recommendations}
        animeMap={animeMap}
        tasteGroup={mockTasteGroup}
        onFeedback={handleFeedback}
        onRate={handleRate}
      />
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
