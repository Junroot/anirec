import { useState, useMemo } from 'react';
import { Star, Hash, BarChart3, Film } from 'lucide-react';
import { StatSummaryCard } from '@/components/stats/StatSummaryCard';
import { Badge } from '@/components/ui/Badge';
import { Button } from '@/components/ui/Button';
import { mockRatings } from '@/data/mockRatings';
import { mockAnime } from '@/data/mockAnime';
import type { WatchStatus } from '@/types/rating';
import { WATCH_STATUSES } from '@/data/constants';
type TabFilter = 'All' | WatchStatus;

export function MyRatingsPage() {
  const [activeTab, setActiveTab] = useState<TabFilter>('All');
  const [sortBy, setSortBy] = useState<'date' | 'score'>('date');

  const animeMap = new Map(mockAnime.map(a => [a.mal_id, a]));

  const filteredRatings = useMemo(() => {
    let ratings = [...mockRatings];
    if (activeTab !== 'All') {
      ratings = ratings.filter(r => r.watchStatus === activeTab);
    }
    if (sortBy === 'score') {
      ratings.sort((a, b) => b.score - a.score);
    } else {
      ratings.sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime());
    }
    return ratings;
  }, [activeTab, sortBy]);

  const avgScore = mockRatings.length
    ? (mockRatings.reduce((sum, r) => sum + r.score, 0) / mockRatings.length).toFixed(1)
    : '0';

  const totalEpisodes = mockRatings.reduce((sum, r) => {
    const anime = animeMap.get(r.animeId);
    return sum + (anime?.episodes ?? 0);
  }, 0);

  const tabs: TabFilter[] = ['All', ...WATCH_STATUSES];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold text-text-primary mb-8">My Ratings</h1>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatSummaryCard icon={Hash} label="Total Rated" value={mockRatings.length} />
        <StatSummaryCard icon={Star} label="Average Score" value={avgScore} />
        <StatSummaryCard icon={Film} label="Episodes Watched" value={totalEpisodes} />
        <StatSummaryCard icon={BarChart3} label="Completed" value={mockRatings.filter(r => r.watchStatus === 'Completed').length} />
      </div>

      <div className="flex flex-wrap gap-2 mb-6">
        {tabs.map(tab => (
          <Button
            key={tab}
            variant={activeTab === tab ? 'primary' : 'ghost'}
            size="sm"
            onClick={() => setActiveTab(tab)}
          >
            {tab}
            {tab !== 'All' && (
              <Badge className="ml-1">{mockRatings.filter(r => r.watchStatus === tab).length}</Badge>
            )}
          </Button>
        ))}
      </div>

      <div className="flex justify-end mb-4 gap-2">
        <Button variant={sortBy === 'date' ? 'secondary' : 'ghost'} size="sm" onClick={() => setSortBy('date')}>
          Recent
        </Button>
        <Button variant={sortBy === 'score' ? 'secondary' : 'ghost'} size="sm" onClick={() => setSortBy('score')}>
          By Score
        </Button>
      </div>

      <div className="bg-surface rounded-xl border border-surface-lighter overflow-hidden">
        <table className="w-full">
          <thead>
            <tr className="border-b border-surface-lighter">
              <th className="text-left text-xs text-text-muted font-medium px-4 py-3">Anime</th>
              <th className="text-left text-xs text-text-muted font-medium px-4 py-3 hidden sm:table-cell">Status</th>
              <th className="text-center text-xs text-text-muted font-medium px-4 py-3">Score</th>
              <th className="text-right text-xs text-text-muted font-medium px-4 py-3 hidden md:table-cell">Date</th>
            </tr>
          </thead>
          <tbody>
            {filteredRatings.map(rating => {
              const anime = animeMap.get(rating.animeId);
              if (!anime) return null;
              return (
                <tr key={rating.id} className="border-b border-surface-lighter/50 hover:bg-surface-light transition-colors">
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-3">
                      <img
                        src={anime.images.jpg.image_url}
                        alt={anime.title}
                        className="w-10 h-14 rounded object-cover shrink-0"
                      />
                      <div className="min-w-0">
                        <p className="text-sm font-medium text-text-primary truncate">{anime.title}</p>
                        <p className="text-xs text-text-muted">{anime.type} · {anime.episodes ?? '?'} eps</p>
                      </div>
                    </div>
                  </td>
                  <td className="px-4 py-3 hidden sm:table-cell">
                    <Badge variant={
                      rating.watchStatus === 'Completed' ? 'success' :
                      rating.watchStatus === 'Watching' ? 'info' :
                      rating.watchStatus === 'Dropped' ? 'error' :
                      rating.watchStatus === 'On Hold' ? 'warning' : 'default'
                    }>
                      {rating.watchStatus}
                    </Badge>
                  </td>
                  <td className="px-4 py-3 text-center">
                    <div className="flex items-center justify-center gap-1">
                      <Star size={14} className="text-warning fill-warning" />
                      <span className="font-semibold text-text-primary">{rating.score}</span>
                    </div>
                  </td>
                  <td className="px-4 py-3 text-right hidden md:table-cell">
                    <span className="text-xs text-text-muted">
                      {new Date(rating.updatedAt).toLocaleDateString()}
                    </span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
