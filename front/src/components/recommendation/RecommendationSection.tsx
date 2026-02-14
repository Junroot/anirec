import { Sparkles } from 'lucide-react';
import { RecommendationCard } from './RecommendationCard';
import type { Anime } from '@/types/anime';
import type { Recommendation, TasteGroup } from '@/types/recommendation';
import { Badge } from '@/components/ui/Badge';

interface RecommendationSectionProps {
  recommendations: Recommendation[];
  animeMap: Map<number, Anime>;
  tasteGroup: TasteGroup;
  onFeedback: (id: string, type: 'like' | 'dislike') => void;
  onRate: (anime: Anime) => void;
}

export function RecommendationSection({ recommendations, animeMap, tasteGroup, onFeedback, onRate }: RecommendationSectionProps) {
  return (
    <div className="space-y-8">
      <div className="bg-surface rounded-xl border border-surface-lighter p-6">
        <div className="flex items-center gap-2 mb-3">
          <Sparkles size={20} className="text-accent" />
          <h3 className="font-semibold text-text-primary">Your Taste Group</h3>
        </div>
        <p className="text-lg font-medium text-primary-light mb-2">{tasteGroup.name}</p>
        <p className="text-sm text-text-secondary mb-4">{tasteGroup.description}</p>
        <div className="flex flex-wrap gap-2">
          {tasteGroup.topGenres.map(genre => (
            <Badge key={genre} variant="primary">{genre}</Badge>
          ))}
        </div>
        <p className="text-xs text-text-muted mt-3">{tasteGroup.memberCount.toLocaleString()} members in this group</p>
      </div>

      <div className="space-y-4">
        <h3 className="text-xl font-bold text-text-primary flex items-center gap-2">
          <Sparkles size={20} className="text-primary" />
          Recommended for You
        </h3>
        <div className="space-y-4">
          {recommendations.map(rec => {
            const anime = animeMap.get(rec.animeId);
            if (!anime) return null;
            return (
              <RecommendationCard
                key={rec.id}
                recommendation={rec}
                anime={anime}
                onFeedback={onFeedback}
                onRate={onRate}
              />
            );
          })}
        </div>
      </div>
    </div>
  );
}
