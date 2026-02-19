export type WatchStatus = 'Watching' | 'Completed' | 'Plan to Watch' | 'Dropped' | 'On Hold';

export interface RatingResponse {
  id: number;
  anime_id: number;
  score: number;
  watch_status: string;
  created_at: string;
  updated_at: string;
}

export interface RatingWithAnimeResponse {
  id: number;
  anime_id: number;
  anime_title: string;
  anime_image_url: string | null;
  anime_type: string | null;
  anime_episodes: number | null;
  score: number;
  watch_status: string;
  created_at: string;
  updated_at: string;
}
