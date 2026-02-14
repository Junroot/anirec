import { BarChart3, Star, Heart, Film } from 'lucide-react';
import { StatSummaryCard } from '@/components/stats/StatSummaryCard';
import { GenreBarChart } from '@/components/stats/GenreBarChart';
import { RatingHistogram } from '@/components/stats/RatingHistogram';
import { TopStudiosChart } from '@/components/stats/TopStudiosChart';
import { MonthlyHistoryChart } from '@/components/stats/MonthlyHistoryChart';
import { mockStats } from '@/data/mockStats';

export function StatsPage() {
  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold text-text-primary mb-8">Your Statistics</h1>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatSummaryCard icon={BarChart3} label="Total Rated" value={mockStats.totalRated} />
        <StatSummaryCard icon={Star} label="Average Score" value={mockStats.averageScore.toFixed(1)} />
        <StatSummaryCard icon={Heart} label="Favorite Genre" value={mockStats.favoriteGenre} />
        <StatSummaryCard icon={Film} label="Episodes Watched" value={mockStats.totalEpisodes.toLocaleString()} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <GenreBarChart data={mockStats.genreStats} />
        <RatingHistogram data={mockStats.ratingDistribution} />
        <TopStudiosChart data={mockStats.topStudios} />
        <MonthlyHistoryChart data={mockStats.monthlyHistory} />
      </div>
    </div>
  );
}
