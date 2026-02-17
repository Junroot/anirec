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

export const STATUSES: { value: string; label: string }[] = [
  { value: 'airing', label: 'Airing' },
  { value: 'complete', label: 'Complete' },
  { value: 'upcoming', label: 'Upcoming' },
];

export const WATCH_STATUSES = [
  'Watching', 'Completed', 'Plan to Watch', 'Dropped', 'On Hold',
] as const;

export const PLACEHOLDER_IMAGE = 'https://placehold.co/225x320/1e1b2e/6366f1?text=No+Image';
