import { useState, useEffect, useRef } from 'react';
import type { Anime } from '@/types/anime';
import type { Pagination } from '@/types/api';
import { searchAnime } from '@/api/animeApi';
import type { SearchParams } from '@/api/animeApi';

const DEBOUNCE_MS = 300;

export function useAnimeSearch(params: SearchParams, key?: number) {
  const { q, type, genres, orderBy, sort, page, limit } = params;
  const [data, setData] = useState<Anime[]>([]);
  const [pagination, setPagination] = useState<Pagination | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const abortControllerRef = useRef<AbortController | null>(null);

  useEffect(() => {
    const timer = setTimeout(() => {
      abortControllerRef.current?.abort();
      const controller = new AbortController();
      abortControllerRef.current = controller;

      setIsLoading(true);
      setError(null);

      searchAnime({ q, type, genres, orderBy, sort, page, limit }, controller.signal)
        .then((res) => {
          setData(res.data);
          setPagination(res.pagination);
        })
        .catch((err: unknown) => {
          if (err instanceof DOMException && err.name === 'AbortError') return;
          setError(err instanceof Error ? err : new Error(String(err)));
        })
        .finally(() => {
          if (!controller.signal.aborted) {
            setIsLoading(false);
          }
        });
    }, DEBOUNCE_MS);

    return () => {
      clearTimeout(timer);
    };
  }, [q, type, genres, orderBy, sort, page, limit, key]);

  useEffect(() => {
    return () => {
      abortControllerRef.current?.abort();
    };
  }, []);

  return { data, pagination, isLoading, error };
}
