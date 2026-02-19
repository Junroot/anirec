import type { RatingResponse, RatingWithAnimeResponse, WatchStatus } from '@/types/rating';
import { ApiError, apiClient } from './client';

export function upsertRating(
  animeId: number,
  score: number,
  watchStatus: WatchStatus,
): Promise<RatingResponse> {
  return apiClient.put('/api/ratings', {
    anime_id: animeId,
    score,
    watch_status: watchStatus,
  });
}

export function getMyRatings(): Promise<RatingWithAnimeResponse[]> {
  return apiClient.get('/api/ratings/me');
}

export async function getRating(malId: number): Promise<RatingResponse | null> {
  try {
    return await apiClient.get(`/api/ratings/me/${malId}`);
  } catch (err) {
    if (err instanceof ApiError && err.status === 404) return null;
    throw err;
  }
}

export function deleteRating(malId: number): Promise<void> {
  return apiClient.del(`/api/ratings/${malId}`);
}
