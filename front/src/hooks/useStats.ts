import { useState, useEffect } from 'react';
import type { UserStats } from '@/types/stats';
import { getMyStats } from '@/api/statsApi';

export function useStats() {
  const [stats, setStats] = useState<UserStats | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getMyStats()
      .then((data) => {
        setStats(data);
        setIsLoading(false);
      })
      .catch((err: unknown) => {
        setError(err instanceof Error ? err.message : String(err));
        setIsLoading(false);
      });
  }, []);

  return { stats, isLoading, error };
}
