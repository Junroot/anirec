import { Star, Play } from 'lucide-react';
import { Badge } from '@/components/ui/Badge';
import type { Anime } from '@/types/anime';
import { PLACEHOLDER_IMAGE } from '@/data/constants';

interface AnimeCardProps {
  anime: Anime;
  onRate?: (anime: Anime) => void;
}

export function AnimeCard({ anime, onRate }: AnimeCardProps) {
  return (
    <div className="group relative rounded-xl overflow-hidden bg-surface-container shadow-lg transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-primary/5">
      <a href={anime.url} target="_blank" rel="noopener noreferrer" className="block">
        <div className="relative aspect-[225/320] overflow-hidden">
          <img
            src={anime.images.jpg.large_image_url || PLACEHOLDER_IMAGE}
            alt={anime.title}
            className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
            loading="lazy"
          />
          {anime.score != null && (
            <div className="absolute top-2 right-2">
              <Badge variant="warning" className="flex items-center gap-1 !bg-warning/90 !text-surface font-bold">
                <Star size={12} fill="currentColor" />
                {anime.score.toFixed(1)}
              </Badge>
            </div>
          )}
          <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-100 md:opacity-0 md:group-hover:opacity-100 transition-opacity duration-300">
            <div className="absolute bottom-0 left-0 right-0 p-3 space-y-2">
              <div className="text-xs text-on-surface-variant space-y-1">
                {anime.studios.length > 0 && (
                  <p>Studio: {anime.studios.map(s => s.name).join(', ')}</p>
                )}
                {anime.season && anime.year && (
                  <p>{anime.season} {anime.year}</p>
                )}
                <p>{anime.aired.from ? new Date(anime.aired.from).getFullYear() : 'TBA'}</p>
              </div>
              {onRate && (
                <button
                  onClick={(e) => { e.preventDefault(); e.stopPropagation(); onRate(anime); }}
                  className="w-full py-1.5 rounded-lg bg-primary hover:bg-primary/80 text-on-primary text-xs font-medium transition-colors flex items-center justify-center gap-1"
                >
                  <Star size={12} /> Rate
                </button>
              )}
            </div>
          </div>
        </div>
      </a>
      <div className="p-3">
        <h3 className="text-sm font-medium text-on-surface line-clamp-2 mb-2 leading-tight">
          {anime.title}
        </h3>
        <div className="flex items-center gap-2 text-xs text-outline mb-2">
          <span className="flex items-center gap-1">
            <Play size={10} />
            {anime.type}
          </span>
          {anime.episodes && <span>{anime.episodes} eps</span>}
        </div>
        <div className="flex flex-wrap gap-1">
          {anime.genres.slice(0, 3).map(genre => (
            <Badge key={genre.mal_id} className="!text-[10px] !px-1.5 !py-0">
              {genre.name}
            </Badge>
          ))}
        </div>
      </div>
    </div>
  );
}
