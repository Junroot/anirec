export interface Recommendation {
  id: string;
  animeId: number;
  reason: string;
  matchScore: number;
  tasteGroup: string;
  basedOn: string[];
  feedback?: 'like' | 'dislike' | null;
}

export interface TasteGroup {
  id: string;
  name: string;
  description: string;
  topGenres: string[];
  memberCount: number;
}
