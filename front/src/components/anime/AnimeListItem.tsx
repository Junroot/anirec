import { Star, ExternalLink } from 'lucide-react';
import { Badge } from '@/components/ui/Badge';
import type { Anime } from '@/types/anime';
import { PLACEHOLDER_IMAGE } from '@/data/constants';

interface AnimeListItemProps {
  anime: Anime;
  rank?: number;
}

export function AnimeListItem({ anime, rank }: AnimeListItemProps) {
  return (
    <a
      href={anime.url}
      target="_blank"
      rel="noopener noreferrer"
      className="flex items-center gap-4 p-3 rounded-xl bg-surface hover:bg-surface-light transition-colors group"
    >
      {rank !== undefined && (
        <span className="text-2xl font-bold text-text-muted w-8 text-center shrink-0">
          {rank}
        </span>
      )}
      <img
        src={anime.images.jpg.image_url || PLACEHOLDER_IMAGE}
        alt={anime.title}
        className="w-12 h-16 rounded-lg object-cover shrink-0"
        loading="lazy"
      />
      <div className="flex-1 min-w-0">
        <h4 className="text-sm font-medium text-text-primary truncate group-hover:text-primary-light transition-colors">
          {anime.title}
        </h4>
        <div className="flex items-center gap-2 mt-1">
          <span className="text-xs text-text-muted">{anime.type}</span>
          {anime.episodes && <span className="text-xs text-text-muted">{anime.episodes} eps</span>}
          <div className="flex gap-1">
            {anime.genres.slice(0, 2).map(g => (
              <Badge key={g.mal_id} className="!text-[10px] !px-1.5 !py-0">{g.name}</Badge>
            ))}
          </div>
        </div>
      </div>
      <div className="flex items-center gap-1 shrink-0">
        <Star size={14} className="text-warning fill-warning" />
        <span className="text-sm font-semibold text-text-primary">{anime.score.toFixed(1)}</span>
      </div>
      <ExternalLink size={14} className="text-text-muted opacity-0 group-hover:opacity-100 transition-opacity shrink-0" />
    </a>
  );
}
