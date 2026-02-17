export interface PaginationItems {
  count: number;
  total: number;
  per_page: number;
}

export interface Pagination {
  last_visible_page: number;
  has_next_page: boolean;
  current_page: number;
  items: PaginationItems | null;
}

export interface PaginatedResponse<T> {
  pagination: Pagination | null;
  data: T[];
}
