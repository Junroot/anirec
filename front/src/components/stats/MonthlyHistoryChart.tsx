import { XAxis, YAxis, Tooltip, ResponsiveContainer, Area, AreaChart } from 'recharts';
import type { MonthlyHistory } from '@/types/stats';
import { CHART_PRIMARY, CHART_AXIS, CHART_TOOLTIP_CONTENT_STYLE, CHART_TOOLTIP_LABEL_STYLE } from '@/data/chartColors';

interface MonthlyHistoryChartProps {
  data: MonthlyHistory[];
}

export function MonthlyHistoryChart({ data }: MonthlyHistoryChartProps) {
  return (
    <div className="bg-surface-container rounded-xl border border-outline-variant p-5">
      <h3 className="text-lg font-semibold text-on-surface mb-4">Monthly Activity</h3>
      <ResponsiveContainer width="100%" height={300}>
        <AreaChart data={data}>
          <defs>
            <linearGradient id="colorCount" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor={CHART_PRIMARY} stopOpacity={0.3} />
              <stop offset="95%" stopColor={CHART_PRIMARY} stopOpacity={0} />
            </linearGradient>
          </defs>
          <XAxis dataKey="month" stroke={CHART_AXIS} fontSize={12} />
          <YAxis stroke={CHART_AXIS} fontSize={12} />
          <Tooltip
            contentStyle={CHART_TOOLTIP_CONTENT_STYLE}
            labelStyle={CHART_TOOLTIP_LABEL_STYLE}
          />
          <Area type="monotone" dataKey="count" stroke={CHART_PRIMARY} fill="url(#colorCount)" strokeWidth={2} />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
}
