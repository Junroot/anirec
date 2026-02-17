/**
 * MD3 design-token hex values for recharts components.
 *
 * recharts does not accept CSS custom properties (`var(--color-*)`) in
 * fill / stroke / style props, so we duplicate the resolved hex values here.
 * If the MD3 palette in `index.css` is updated, these constants must be
 * updated accordingly.
 */

import type { CSSProperties } from 'react';

/* ── single-series colour ── */
/** --color-primary */
export const CHART_PRIMARY = '#c0c1ff';

/* ── axis / grid colour ── */
/** --color-outline */
export const CHART_AXIS = '#918f9a';

/* ── tooltip shared styles ── */
export const CHART_TOOLTIP_CONTENT_STYLE: CSSProperties = {
  backgroundColor: '#1f1f25', // --color-surface-container
  border: '1px solid #46464f', // --color-outline-variant
  borderRadius: '8px',
  color: '#e4e1e9', // --color-on-surface
};

export const CHART_TOOLTIP_LABEL_STYLE: CSSProperties = {
  color: '#c8c5d0', // --color-on-surface-variant
};

/* ── multi-series palette (6 colours) ── */
export const CHART_PALETTE = [
  '#c0c1ff', // primary
  '#e9b9d3', // tertiary
  '#c6c4dd', // secondary
  '#e1e0ff', // on-primary-container
  '#ffd8ec', // on-tertiary-container
  '#e2e0f9', // on-secondary-container
] as const;
