import { Link } from 'react-router';
import { BarChart3, Star, Heart, AlertTriangle, Search } from 'lucide-react';
import { Spinner } from '@/components/ui/Spinner';
import { EmptyState } from '@/components/ui/EmptyState';
import { Button } from '@/components/ui/Button';
import { StatSummaryCard } from '@/components/stats/StatSummaryCard';
import { GenreBarChart } from '@/components/stats/GenreBarChart';
import { RatingHistogram } from '@/components/stats/RatingHistogram';
import { TopStudiosChart } from '@/components/stats/TopStudiosChart';
import { MonthlyHistoryChart } from '@/components/stats/MonthlyHistoryChart';
import { useStats } from '@/hooks/useStats';

export function StatsPage() {
  const { stats, isLoading, error } = useStats();

  if (isLoading) {
    return <Spinner size="lg" className="py-20" />;
  }

  if (error) {
    return (
      <EmptyState
        icon={AlertTriangle}
        title="Failed to load statistics"
        description={error}
      />
    );
  }

  if (!stats || stats.totalRated === 0) {
    return (
      <EmptyState
        icon={BarChart3}
        title="아직 평가한 애니메이션이 없습니다"
        description="애니메이션을 평가하면 통계를 확인할 수 있습니다."
        action={
          <Link to="/search/anime">
            <Button className="gap-2">
              <Search size={16} /> 애니메이션 탐색하기
            </Button>
          </Link>
        }
      />
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold text-text-primary mb-8">Your Statistics</h1>

      <div className="grid grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
        <StatSummaryCard icon={BarChart3} label="Total Rated" value={stats.totalRated} />
        <StatSummaryCard icon={Star} label="Average Score" value={stats.averageScore.toFixed(1)} />
        <StatSummaryCard icon={Heart} label="Favorite Genre" value={stats.favoriteGenre} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <GenreBarChart data={stats.genreStats.slice(0, 7)} />
        <RatingHistogram data={stats.ratingDistribution} />
        <TopStudiosChart data={stats.topStudios} />
        <MonthlyHistoryChart data={stats.monthlyHistory} />
      </div>
    </div>
  );
}
