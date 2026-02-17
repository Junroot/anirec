import { TrendingUp, AlertTriangle } from 'lucide-react';
import { AnimeCard } from '@/components/anime/AnimeCard';
import { Spinner } from '@/components/ui/Spinner';
import type { Anime } from '@/types/anime';

interface TrendingSectionProps {
  anime: Anime[];
  onRate?: (anime: Anime) => void;
  isLoading?: boolean;
  error?: Error | null;
}

export function TrendingSection({ anime, onRate, isLoading, error }: TrendingSectionProps) {
  return (
    <section className="py-10">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center gap-2 mb-6">
          <TrendingUp size={24} className="text-error" />
          <h2 className="text-2xl font-bold text-on-surface">Trending Now</h2>
        </div>
        {isLoading ? (
          <div className="py-12">
            <Spinner size="lg" />
          </div>
        ) : error ? (
          <div className="flex items-center gap-2 py-12 justify-center text-error">
            <AlertTriangle size={20} />
            <p>Failed to load trending anime.</p>
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4">
            {anime.slice(0, 6).map(a => (
              <AnimeCard key={a.mal_id} anime={a} onRate={onRate} />
            ))}
          </div>
        )}
      </div>
    </section>
  );
}
