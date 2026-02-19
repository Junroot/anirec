import type { UserStats } from '@/types/stats';
import { apiClient } from './client';

interface StatsApiResponse {
  total_rated: number;
  average_score: number;
  favorite_genre: string;
  genre_stats: { genre: string; count: number; avg_score: number }[];
  rating_distribution: { score: number; count: number }[];
  top_studios: { studio: string; count: number; avg_score: number }[];
  monthly_history: { month: string; count: number }[];
}

export async function getMyStats(): Promise<UserStats> {
  const raw = await apiClient.get<StatsApiResponse>('/api/stats/me');
  return {
    totalRated: raw.total_rated,
    averageScore: raw.average_score,
    favoriteGenre: raw.favorite_genre,
    genreStats: raw.genre_stats.map((g) => ({ genre: g.genre, count: g.count, avgScore: g.avg_score })),
    ratingDistribution: raw.rating_distribution,
    topStudios: raw.top_studios.map((s) => ({ studio: s.studio, count: s.count, avgScore: s.avg_score })),
    monthlyHistory: raw.monthly_history,
  };
}
