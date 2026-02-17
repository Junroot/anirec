import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import type { GenreStat } from '@/types/stats';
import { CHART_PALETTE, CHART_AXIS, CHART_TOOLTIP_CONTENT_STYLE, CHART_TOOLTIP_LABEL_STYLE } from '@/data/chartColors';

interface GenreBarChartProps {
  data: GenreStat[];
}

export function GenreBarChart({ data }: GenreBarChartProps) {
  return (
    <div className="bg-surface-container rounded-xl border border-outline-variant p-5">
      <h3 className="text-lg font-semibold text-on-surface mb-4">Genres Watched</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data} layout="vertical" margin={{ left: 60 }}>
          <XAxis type="number" stroke={CHART_AXIS} fontSize={12} />
          <YAxis type="category" dataKey="genre" stroke={CHART_AXIS} fontSize={12} width={80} />
          <Tooltip
            contentStyle={CHART_TOOLTIP_CONTENT_STYLE}
            labelStyle={CHART_TOOLTIP_LABEL_STYLE}
          />
          <Bar dataKey="count" radius={[0, 4, 4, 0]}>
            {data.map((_, index) => (
              <Cell key={index} fill={CHART_PALETTE[index % CHART_PALETTE.length]} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
