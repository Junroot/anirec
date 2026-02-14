export interface GenreStat {
  genre: string;
  count: number;
  avgScore: number;
}

export interface RatingDistribution {
  score: number;
  count: number;
}

export interface StudioStat {
  studio: string;
  count: number;
  avgScore: number;
}

export interface MonthlyHistory {
  month: string;
  count: number;
}

export interface UserStats {
  totalRated: number;
  averageScore: number;
  favoriteGenre: string;
  totalEpisodes: number;
  genreStats: GenreStat[];
  ratingDistribution: RatingDistribution[];
  topStudios: StudioStat[];
  monthlyHistory: MonthlyHistory[];
}
