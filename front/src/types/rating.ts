export type WatchStatus = 'Watching' | 'Completed' | 'Plan to Watch' | 'Dropped' | 'On Hold';

export interface Rating {
  id: string;
  animeId: number;
  userId: string;
  score: number;
  watchStatus: WatchStatus;
  createdAt: string;
  updatedAt: string;
}
