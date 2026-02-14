import { Trophy } from 'lucide-react';
import { AnimeListItem } from '@/components/anime/AnimeListItem';
import type { Anime } from '@/types/anime';

interface TopAnimeListProps {
  anime: Anime[];
}

export function TopAnimeList({ anime }: TopAnimeListProps) {
  return (
    <section className="py-10">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center gap-2 mb-6">
          <Trophy size={24} className="text-warning" />
          <h2 className="text-2xl font-bold text-text-primary">Top Anime</h2>
        </div>
        <div className="space-y-2 max-w-3xl">
          {anime.slice(0, 10).map((a, i) => (
            <AnimeListItem key={a.mal_id} anime={a} rank={i + 1} />
          ))}
        </div>
      </div>
    </section>
  );
}
