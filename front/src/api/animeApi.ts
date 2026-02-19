import type { Anime } from '@/types/anime';
import type { PaginatedResponse } from '@/types/api';
import { apiClient } from './client';

export interface SearchParams {
  type?: string;
  status?: string;
  genres?: string;
  producers?: string;
  orderBy?: string;
  sort?: string;
  year?: number;
  season?: string;
  page?: number;
  limit?: number;
}

export function searchAnime(
  params: SearchParams = {},
  signal?: AbortSignal,
): Promise<PaginatedResponse<Anime>> {
  return apiClient.get('/api/anime', { ...params }, signal);
}

export function getTopAnime(
  page?: number,
  limit?: number,
  signal?: AbortSignal,
): Promise<PaginatedResponse<Anime>> {
  return apiClient.get('/api/anime/top', { page, limit }, signal);
}

export function getSeasonalAnime(
  year: number,
  season: string,
  page?: number,
  limit?: number,
  signal?: AbortSignal,
): Promise<PaginatedResponse<Anime>> {
  return apiClient.get('/api/anime/season', { year, season, page, limit }, signal);
}

export function getCurrentSeasonAnime(
  page?: number,
  limit?: number,
  signal?: AbortSignal,
): Promise<PaginatedResponse<Anime>> {
  return apiClient.get('/api/anime/season/now', { page, limit }, signal);
}

export interface FilterOption {
  id: number;
  name: string;
}

export function searchGenres(
  query: string,
  signal?: AbortSignal,
): Promise<FilterOption[]> {
  return apiClient.get('/api/anime/genres', { q: query }, signal);
}

export function searchProducers(
  query: string,
  signal?: AbortSignal,
): Promise<FilterOption[]> {
  return apiClient.get('/api/anime/producers', { q: query, limit: 10 }, signal);
}
