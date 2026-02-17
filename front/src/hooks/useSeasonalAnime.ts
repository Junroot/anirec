import { useState, useEffect, useRef } from 'react';
import type { Anime } from '@/types/anime';
import type { Pagination } from '@/types/api';
import { getSeasonalAnime, getCurrentSeasonAnime } from '@/api/animeApi';

export function useSeasonalAnime(
  year?: number,
  season?: string,
  page?: number,
  limit?: number,
) {
  const [data, setData] = useState<Anime[]>([]);
  const [pagination, setPagination] = useState<Pagination | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const abortControllerRef = useRef<AbortController | null>(null);

  useEffect(() => {
    abortControllerRef.current?.abort();
    const controller = new AbortController();
    abortControllerRef.current = controller;

    const request =
      year != null && season != null
        ? getSeasonalAnime(year, season, page, limit, controller.signal)
        : getCurrentSeasonAnime(page, limit, controller.signal);

    request
      .then((res) => {
        setData(res.data);
        setPagination(res.pagination);
        setIsLoading(false);
      })
      .catch((err: unknown) => {
        if (err instanceof DOMException && err.name === 'AbortError') return;
        setError(err instanceof Error ? err : new Error(String(err)));
        setIsLoading(false);
      });

    return () => {
      controller.abort();
      setIsLoading(true);
      setError(null);
    };
  }, [year, season, page, limit]);

  return { data, pagination, isLoading, error };
}
