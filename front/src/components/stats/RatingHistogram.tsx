import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import type { RatingDistribution } from '@/types/stats';
import { CHART_PRIMARY, CHART_AXIS, CHART_TOOLTIP_CONTENT_STYLE, CHART_TOOLTIP_LABEL_STYLE } from '@/data/chartColors';

interface RatingHistogramProps {
  data: RatingDistribution[];
}

export function RatingHistogram({ data }: RatingHistogramProps) {
  return (
    <div className="bg-surface-container rounded-xl border border-outline-variant p-5">
      <h3 className="text-lg font-semibold text-on-surface mb-4">Rating Distribution</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data}>
          <XAxis dataKey="score" stroke={CHART_AXIS} fontSize={12} />
          <YAxis stroke={CHART_AXIS} fontSize={12} />
          <Tooltip
            contentStyle={CHART_TOOLTIP_CONTENT_STYLE}
            labelStyle={CHART_TOOLTIP_LABEL_STYLE}
            formatter={(value: number | undefined) => [value ?? 0, 'Count']}
            labelFormatter={(label: React.ReactNode) => `Score: ${label}`}
          />
          <Bar dataKey="count" fill={CHART_PRIMARY} radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
