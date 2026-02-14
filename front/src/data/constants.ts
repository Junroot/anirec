export const GENRES = [
  'Action', 'Adventure', 'Comedy', 'Drama', 'Fantasy',
  'Horror', 'Mystery', 'Romance', 'Sci-Fi', 'Slice of Life',
  'Sports', 'Supernatural', 'Thriller', 'Mecha', 'Music',
] as const;

export const SEASONS: { value: string; label: string }[] = [
  { value: 'winter', label: 'Winter' },
  { value: 'spring', label: 'Spring' },
  { value: 'summer', label: 'Summer' },
  { value: 'fall', label: 'Fall' },
];

export const FORMATS = ['TV', 'Movie', 'OVA', 'ONA', 'Special', 'Music'] as const;

export const SORT_OPTIONS = [
  { value: 'score-desc', label: 'Score (High to Low)' },
  { value: 'score-asc', label: 'Score (Low to High)' },
  { value: 'title-asc', label: 'Title (A-Z)' },
  { value: 'title-desc', label: 'Title (Z-A)' },
  { value: 'popularity-asc', label: 'Most Popular' },
  { value: 'newest', label: 'Newest First' },
] as const;

export const WATCH_STATUSES = [
  'Watching', 'Completed', 'Plan to Watch', 'Dropped', 'On Hold',
] as const;

export const PLACEHOLDER_IMAGE = 'https://placehold.co/225x320/1e1b2e/6366f1?text=No+Image';
