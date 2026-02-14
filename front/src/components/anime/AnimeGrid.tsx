import { AnimeCard } from './AnimeCard';
import { EmptyState } from '@/components/ui/EmptyState';
import { Search } from 'lucide-react';
import type { Anime } from '@/types/anime';

interface AnimeGridProps {
  anime: Anime[];
  onRate?: (anime: Anime) => void;
}

export function AnimeGrid({ anime, onRate }: AnimeGridProps) {
  if (anime.length === 0) {
    return (
      <EmptyState
        icon={Search}
        title="No anime found"
        description="Try adjusting your filters or search query."
      />
    );
  }

  return (
    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4">
      {anime.map(a => (
        <AnimeCard key={a.mal_id} anime={a} onRate={onRate} />
      ))}
    </div>
  );
}
