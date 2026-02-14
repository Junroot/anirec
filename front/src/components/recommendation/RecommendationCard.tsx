import { Sparkles, Star, ExternalLink } from 'lucide-react';
import { Badge } from '@/components/ui/Badge';
import { FeedbackButtons } from './FeedbackButtons';
import type { Anime } from '@/types/anime';
import type { Recommendation } from '@/types/recommendation';
import { PLACEHOLDER_IMAGE } from '@/data/constants';

interface RecommendationCardProps {
  recommendation: Recommendation;
  anime: Anime;
  onFeedback: (id: string, type: 'like' | 'dislike') => void;
  onRate: (anime: Anime) => void;
}

export function RecommendationCard({ recommendation, anime, onFeedback, onRate }: RecommendationCardProps) {
  return (
    <div className="bg-surface rounded-xl border border-surface-lighter overflow-hidden hover:border-primary/30 transition-colors">
      <div className="flex">
        <a href={anime.url} target="_blank" rel="noopener noreferrer" className="shrink-0">
          <img
            src={anime.images.jpg.image_url || PLACEHOLDER_IMAGE}
            alt={anime.title}
            className="w-24 sm:w-32 h-full object-cover"
            loading="lazy"
          />
        </a>
        <div className="flex-1 p-4 space-y-3">
          <div className="flex items-start justify-between gap-2">
            <div>
              <a href={anime.url} target="_blank" rel="noopener noreferrer" className="group">
                <h3 className="font-medium text-text-primary group-hover:text-primary-light transition-colors flex items-center gap-1">
                  {anime.title}
                  <ExternalLink size={12} className="opacity-0 group-hover:opacity-100" />
                </h3>
              </a>
              <div className="flex items-center gap-2 mt-1">
                <div className="flex items-center gap-1">
                  <Star size={12} className="text-warning fill-warning" />
                  <span className="text-xs text-text-secondary">{anime.score.toFixed(1)}</span>
                </div>
                <span className="text-xs text-text-muted">{anime.type} · {anime.episodes ?? '?'} eps</span>
              </div>
            </div>
            <Badge variant="primary" className="shrink-0">
              <Sparkles size={10} className="mr-1" />
              {Math.round(recommendation.matchScore * 100)}%
            </Badge>
          </div>

          <p className="text-sm text-text-secondary">{recommendation.reason}</p>

          <div className="flex flex-wrap gap-1">
            {anime.genres.slice(0, 3).map(g => (
              <Badge key={g.mal_id} className="!text-[10px]">{g.name}</Badge>
            ))}
          </div>

          <div className="flex items-center justify-between pt-1">
            <button
              onClick={() => onRate(anime)}
              className="text-xs text-primary-light hover:text-primary transition-colors font-medium"
            >
              Rate this anime
            </button>
            <FeedbackButtons
              feedback={recommendation.feedback}
              onFeedback={type => onFeedback(recommendation.id, type)}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
