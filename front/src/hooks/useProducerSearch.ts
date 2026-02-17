import { useState, useEffect, useRef } from 'react';
import { searchProducers } from '@/api/animeApi';
import type { Producer } from '@/api/animeApi';

const DEBOUNCE_MS = 300;
const MIN_QUERY_LENGTH = 2;

export function useProducerSearch(query: string) {
  const [results, setResults] = useState<Producer[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const abortControllerRef = useRef<AbortController | null>(null);

  useEffect(() => {
    if (query.length < MIN_QUERY_LENGTH) {
      setResults([]);
      return;
    }

    const timer = setTimeout(() => {
      abortControllerRef.current?.abort();
      const controller = new AbortController();
      abortControllerRef.current = controller;

      setIsLoading(true);

      searchProducers(query, controller.signal)
        .then((data) => {
          setResults(data);
        })
        .catch((err: unknown) => {
          if (err instanceof DOMException && err.name === 'AbortError') return;
          setResults([]);
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
  }, [query]);

  useEffect(() => {
    return () => {
      abortControllerRef.current?.abort();
    };
  }, []);

  return { results, isLoading };
}
