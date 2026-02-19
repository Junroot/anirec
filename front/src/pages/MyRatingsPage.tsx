import { useState, useMemo, useEffect, useCallback } from 'react';
import { Star, Trash2, Pencil, AlertTriangle } from 'lucide-react';
import { RatingModal } from '@/components/rating/RatingModal';
import type { RatingTarget } from '@/components/rating/RatingModal';
import { Badge } from '@/components/ui/Badge';
import { Button } from '@/components/ui/Button';
import { Spinner } from '@/components/ui/Spinner';
import { EmptyState } from '@/components/ui/EmptyState';
import { getMyRatings, deleteRating, upsertRating } from '@/api/ratingApi';
import type { RatingWithAnimeResponse, WatchStatus } from '@/types/rating';
import { WATCH_STATUSES, PLACEHOLDER_IMAGE } from '@/data/constants';

type TabFilter = 'All' | WatchStatus;

function ratingToTarget(r: RatingWithAnimeResponse): RatingTarget {
  return {
    malId: r.anime_id,
    title: r.anime_title,
    imageUrl: r.anime_image_url,
    type: r.anime_type,
    episodes: r.anime_episodes,
  };
}

export function MyRatingsPage() {
  const [ratings, setRatings] = useState<RatingWithAnimeResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<TabFilter>('All');
  const [sortBy, setSortBy] = useState<'date' | 'score'>('date');
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [editTarget, setEditTarget] = useState<RatingTarget | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const fetchRatings = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getMyRatings();
      setRatings(data);
    } catch {
      setError('Failed to load ratings');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchRatings();
  }, [fetchRatings]);

  const filteredRatings = useMemo(() => {
    let result = [...ratings];
    if (activeTab !== 'All') {
      result = result.filter(r => r.watch_status === activeTab);
    }
    if (sortBy === 'score') {
      result.sort((a, b) => b.score - a.score);
    } else {
      result.sort((a, b) => new Date(b.updated_at).getTime() - new Date(a.updated_at).getTime());
    }
    return result;
  }, [ratings, activeTab, sortBy]);

  const handleEdit = (rating: RatingWithAnimeResponse) => {
    setEditTarget(ratingToTarget(rating));
  };

  const handleEditSubmit = async (animeId: number, score: number, watchStatus: WatchStatus) => {
    setSubmitting(true);
    try {
      await upsertRating(animeId, score, watchStatus);
      setEditTarget(null);
      await fetchRatings();
    } catch {
      // API error — modal stays open so user can retry
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (malId: number) => {
    setDeletingId(malId);
    try {
      await deleteRating(malId);
      setRatings(prev => prev.filter(r => r.anime_id !== malId));
    } catch {
      // Silently fail — row stays in place
    } finally {
      setDeletingId(null);
    }
  };

  const tabs: TabFilter[] = ['All', ...WATCH_STATUSES];

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl font-bold text-on-surface mb-8">My Ratings</h1>
        <Spinner size="lg" className="py-20" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl font-bold text-on-surface mb-8">My Ratings</h1>
        <EmptyState
          icon={AlertTriangle}
          title="Failed to load ratings"
          description={error}
          action={<Button variant="secondary" onClick={fetchRatings}>Retry</Button>}
        />
      </div>
    );
  }

  if (ratings.length === 0) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl font-bold text-on-surface mb-8">My Ratings</h1>
        <EmptyState
          icon={Star}
          title="No ratings yet"
          description="Start rating anime from the search page to build your collection."
        />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold text-on-surface mb-8">My Ratings</h1>

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
              <Badge className="ml-1">{ratings.filter(r => r.watch_status === tab).length}</Badge>
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
              <th className="text-center text-xs text-text-muted font-medium px-4 py-3 w-20"></th>
            </tr>
          </thead>
          <tbody>
            {filteredRatings.map(rating => (
              <tr key={rating.id} className="border-b border-surface-lighter/50 hover:bg-surface-light transition-colors">
                <td className="px-4 py-3">
                  <div className="flex items-center gap-3">
                    <img
                      src={rating.anime_image_url || PLACEHOLDER_IMAGE}
                      alt={rating.anime_title}
                      className="w-10 h-14 rounded object-cover shrink-0"
                    />
                    <div className="min-w-0">
                      <p className="text-sm font-medium text-text-primary truncate">{rating.anime_title}</p>
                      <p className="text-xs text-text-muted">{rating.anime_type ?? '?'} · {rating.anime_episodes ?? '?'} eps</p>
                    </div>
                  </div>
                </td>
                <td className="px-4 py-3 hidden sm:table-cell">
                  <Badge variant={
                    rating.watch_status === 'Completed' ? 'success' :
                    rating.watch_status === 'Watching' ? 'info' :
                    rating.watch_status === 'Dropped' ? 'error' :
                    rating.watch_status === 'On Hold' ? 'warning' : 'default'
                  }>
                    {rating.watch_status}
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
                    {new Date(rating.updated_at).toLocaleDateString()}
                  </span>
                </td>
                <td className="px-4 py-3">
                  <div className="flex items-center justify-center gap-2">
                    <button
                      onClick={() => handleEdit(rating)}
                      className="text-outline hover:text-primary transition-colors"
                      title="Edit rating"
                    >
                      <Pencil size={16} />
                    </button>
                    <button
                      onClick={() => handleDelete(rating.anime_id)}
                      disabled={deletingId === rating.anime_id}
                      className="text-outline hover:text-error transition-colors disabled:opacity-50"
                      title="Delete rating"
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <RatingModal
        target={editTarget}
        isOpen={!!editTarget}
        onClose={() => setEditTarget(null)}
        onSubmit={handleEditSubmit}
        submitting={submitting}
      />
    </div>
  );
}
