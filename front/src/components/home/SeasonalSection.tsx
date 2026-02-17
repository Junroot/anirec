import { Calendar, AlertTriangle } from 'lucide-react';
import { AnimeCard } from '@/components/anime/AnimeCard';
import { Spinner } from '@/components/ui/Spinner';
import type { Anime } from '@/types/anime';

interface SeasonalSectionProps {
  anime: Anime[];
  onRate?: (anime: Anime) => void;
  isLoading?: boolean;
  error?: Error | null;
}

export function SeasonalSection({ anime, onRate, isLoading, error }: SeasonalSectionProps) {
  const currentSeason = (['Winter', 'Spring', 'Summer', 'Fall'] as const)[Math.floor(new Date().getMonth() / 3)];
  const currentYear = new Date().getFullYear();

  return (
    <section className="py-10 bg-surface/30">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center gap-2 mb-6">
          <Calendar size={24} className="text-info" />
          <h2 className="text-2xl font-bold text-text-primary">{currentSeason} {currentYear} Anime</h2>
        </div>
        {isLoading ? (
          <div className="py-12">
            <Spinner size="lg" />
          </div>
        ) : error ? (
          <div className="flex items-center gap-2 py-12 justify-center text-error">
            <AlertTriangle size={20} />
            <p>Failed to load seasonal anime.</p>
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
