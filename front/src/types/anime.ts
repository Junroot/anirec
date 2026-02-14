export type AnimeFormat = 'TV' | 'Movie' | 'OVA' | 'ONA' | 'Special' | 'Music';
export type AnimeSeason = 'Winter' | 'Spring' | 'Summer' | 'Fall';
export type AnimeStatus = 'Airing' | 'Finished' | 'Upcoming';

export interface Anime {
  mal_id: number;
  title: string;
  title_japanese?: string;
  synopsis?: string;
  score: number;
  scored_by: number;
  rank: number;
  popularity: number;
  members: number;
  episodes: number | null;
  status: AnimeStatus;
  type: AnimeFormat;
  season?: AnimeSeason;
  year?: number;
  genres: Genre[];
  studios: Studio[];
  images: {
    jpg: {
      image_url: string;
      large_image_url: string;
    };
  };
  aired: {
    from: string;
    to: string | null;
  };
  url: string;
}

export interface Genre {
  mal_id: number;
  name: string;
}

export interface Studio {
  mal_id: number;
  name: string;
}
