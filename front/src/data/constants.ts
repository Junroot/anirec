export const GENRES: { value: string; label: string }[] = [
  { value: '1', label: 'Action' },
  { value: '2', label: 'Adventure' },
  { value: '4', label: 'Comedy' },
  { value: '8', label: 'Drama' },
  { value: '10', label: 'Fantasy' },
  { value: '14', label: 'Horror' },
  { value: '18', label: 'Mecha' },
  { value: '19', label: 'Music' },
  { value: '7', label: 'Mystery' },
  { value: '22', label: 'Romance' },
  { value: '24', label: 'Sci-Fi' },
  { value: '36', label: 'Slice of Life' },
  { value: '30', label: 'Sports' },
  { value: '37', label: 'Supernatural' },
  { value: '41', label: 'Suspense' },
];

export const FORMATS: { value: string; label: string }[] = [
  { value: 'tv', label: 'TV' },
  { value: 'movie', label: 'Movie' },
  { value: 'ova', label: 'OVA' },
  { value: 'ona', label: 'ONA' },
  { value: 'special', label: 'Special' },
  { value: 'music', label: 'Music' },
];

export const SORT_OPTIONS: { value: string; label: string }[] = [
  { value: 'score-desc', label: 'Score (High to Low)' },
  { value: 'score-asc', label: 'Score (Low to High)' },
  { value: 'title-asc', label: 'Title (A-Z)' },
  { value: 'title-desc', label: 'Title (Z-A)' },
  { value: 'popularity-asc', label: 'Most Popular' },
  { value: 'newest', label: 'Newest First' },
];

export const SORT_PARAM_MAP: Record<string, { orderBy: string; sort: string }> = {
  'score-desc': { orderBy: 'score', sort: 'desc' },
  'score-asc': { orderBy: 'score', sort: 'asc' },
  'title-asc': { orderBy: 'title', sort: 'asc' },
  'title-desc': { orderBy: 'title', sort: 'desc' },
  'popularity-asc': { orderBy: 'popularity', sort: 'asc' },
  'newest': { orderBy: 'start_date', sort: 'desc' },
};

export const WATCH_STATUSES = [
  'Watching', 'Completed', 'Plan to Watch', 'Dropped', 'On Hold',
] as const;

export const PLACEHOLDER_IMAGE = 'https://placehold.co/225x320/1e1b2e/6366f1?text=No+Image';
