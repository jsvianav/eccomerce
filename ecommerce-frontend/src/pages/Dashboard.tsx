import { useQuery } from "@tanstack/react-query"
import { getProducts } from "@/api/products"
import { getOrders } from "@/api/orders"
import { getInventory } from "@/api/inventory"
import { getUsers } from "@/api/users"
import { Skeleton } from "@/components/ui/skeleton"
import { StatusBadge } from "@/components/StatusBadge"
import { formatCurrency } from "@/lib/utils"
import {
  TrendingUp, Package, Users, TriangleAlert, ShoppingBag, Clock,
} from "lucide-react"
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, Cell,
} from "recharts"
import type { ComponentType } from "react"

/* ── Chart config ── */
const STATUS_BAR: Record<string, string> = {
  CREATED:   "hsl(220 72% 60%)",
  PAID:      "hsl(145 55% 44%)",
  CANCELLED: "hsl(0 65% 57%)",
}
const STATUS_LABEL: Record<string, string> = {
  CREATED: "Creados", PAID: "Pagados", CANCELLED: "Cancelados",
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function ChartTooltip({ active, payload, label }: any) {
  if (!active || !payload?.length) return null
  return (
    <div className="rounded-xl bg-card ring-1 ring-border shadow-[0_4px_20px_-2px_rgb(0_0_0_/_0.12)] px-3 py-2.5">
      <p className="text-[11px] font-semibold text-muted-foreground uppercase tracking-wide mb-0.5">
        {STATUS_LABEL[label] ?? label}
      </p>
      <p className="text-xl font-bold tabular-nums leading-none text-foreground">
        {payload[0].value}
      </p>
    </div>
  )
}

/* ── Formatted date ── */
function todayLabel() {
  return new Date().toLocaleDateString("es-CO", {
    weekday: "long", day: "numeric", month: "long",
  })
}

export default function Dashboard() {
  const products  = useQuery({ queryKey: ["products"],  queryFn: getProducts  })
  const orders    = useQuery({ queryKey: ["orders"],    queryFn: getOrders    })
  const inventory = useQuery({ queryKey: ["inventory"], queryFn: getInventory })
  const users     = useQuery({ queryKey: ["users"],     queryFn: getUsers     })

  const loading = products.isLoading || orders.isLoading || inventory.isLoading || users.isLoading

  const paidTotal = (orders.data ?? []).filter(o => o.status === "PAID").reduce((s, o) => s + o.totalAmount, 0)
  const paidCount = (orders.data ?? []).filter(o => o.status === "PAID").length
  const lowStock  = (inventory.data ?? []).filter(i => i.stock < 10).length
  const available = (products.data ?? []).filter(p => p.available).length

  const ordersByStatus = ["CREATED", "PAID", "CANCELLED"].map(status => ({
    status,
    count: (orders.data ?? []).filter(o => o.status === status).length,
  }))

  const recent = [...(orders.data ?? [])].reverse().slice(0, 6)
  const totalOrders = orders.data?.length ?? 0

  return (
    <div className="space-y-5">

      {/* ── Editorial header ── */}
      <div className="flex items-end justify-between pb-1">
        <div>
          <h1 className="text-[22px] font-bold tracking-tight text-foreground leading-none">
            Vista general
          </h1>
          <p className="text-[12px] text-muted-foreground mt-1.5 capitalize">
            {todayLabel()}
          </p>
        </div>
      </div>

      {/* ─── Bento row 1: 4-col KPI grid ─── */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 grid-flow-dense">

        {/* Hero KPI — Revenue (spans 2 cols) */}
        <div
          className="lg:col-span-2 rounded-2xl ring-1 ring-primary/25 shadow-card p-5 flex flex-col justify-between min-h-[140px] cursor-default
                     bg-gradient-to-br from-primary/[0.08] via-primary/[0.04] to-card
                     hover:shadow-card-hover hover:-translate-y-px
                     transition-[box-shadow,transform] duration-200"
          style={{ animationDelay: "0ms" }}
        >
          <div className="flex items-start justify-between">
            <p className="text-[11px] font-semibold uppercase tracking-widest text-primary/70">
              Ingresos pagados
            </p>
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary/10 ring-1 ring-primary/20">
              <TrendingUp className="h-4 w-4 text-primary" strokeWidth={2.25} />
            </div>
          </div>
          {loading ? (
            <div className="space-y-2 mt-3">
              <Skeleton className="h-10 w-44" />
              <Skeleton className="h-3.5 w-28" />
            </div>
          ) : (
            <div className="mt-auto pt-3">
              <p className="text-4xl font-bold tracking-tight tabular-nums text-foreground leading-none">
                {formatCurrency(paidTotal)}
              </p>
              <p className="text-[12px] text-muted-foreground mt-1.5">
                {paidCount} {paidCount === 1 ? "pedido pagado" : "pedidos pagados"}
              </p>
            </div>
          )}
        </div>

        {/* Orders */}
        <KpiCard
          label="Pedidos"
          icon={ShoppingBag}
          loading={loading}
          value={totalOrders}
          sub={`${(orders.data ?? []).filter(o => o.status === "CREATED").length} pendientes`}
          delay={50}
        />

        {/* Products */}
        <KpiCard
          label="Productos"
          icon={Package}
          loading={loading}
          value={products.data?.length ?? 0}
          sub={`${available} disponibles`}
          delay={100}
        />
      </div>

      {/* ─── Bento row 2: secondary KPIs + chart + recent orders ─── */}
      <div className="grid grid-cols-2 lg:grid-cols-5 gap-4 grid-flow-dense">

        {/* Users */}
        <div
          className="col-span-1 rounded-2xl bg-card ring-1 ring-border shadow-card p-4 flex flex-col cursor-default
                     hover:shadow-card-hover hover:-translate-y-px
                     transition-[box-shadow,transform] duration-200"
          style={{ animationDelay: "150ms" }}
        >
          <div className="flex items-center justify-between mb-3">
            <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">
              Usuarios
            </p>
            <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-muted ring-1 ring-border/60">
              <Users className="h-3.5 w-3.5 text-muted-foreground" strokeWidth={1.75} />
            </div>
          </div>
          {loading
            ? <Skeleton className="h-8 w-16 mt-auto" />
            : <p className="text-2xl font-bold tabular-nums text-foreground mt-auto">{users.data?.length ?? 0}</p>
          }
          <p className="text-[11px] text-muted-foreground mt-1">registrados</p>
        </div>

        {/* Low stock alert */}
        <div
          className={`col-span-1 rounded-2xl ring-1 shadow-card p-4 flex flex-col cursor-default
                      hover:shadow-card-hover hover:-translate-y-px
                      transition-[box-shadow,transform] duration-200 ${
            lowStock > 0
              ? "bg-amber-50 dark:bg-amber-900/15 ring-amber-200 dark:ring-amber-700/40"
              : "bg-card ring-border"
          }`}
          style={{ animationDelay: "175ms" }}
        >
          <div className="flex items-center justify-between mb-3">
            <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">
              Stock bajo
            </p>
            <div className={`flex h-7 w-7 items-center justify-center rounded-lg ring-1 ${
              lowStock > 0
                ? "bg-amber-100 dark:bg-amber-900/40 ring-amber-300/60 dark:ring-amber-600/50"
                : "bg-muted ring-border/60"
            }`}>
              <TriangleAlert className={`h-3.5 w-3.5 ${
                lowStock > 0 ? "text-amber-600 dark:text-amber-400" : "text-muted-foreground"
              }`} strokeWidth={1.75} />
            </div>
          </div>
          {loading
            ? <Skeleton className="h-8 w-12 mt-auto" />
            : <p className={`text-2xl font-bold tabular-nums mt-auto ${
                lowStock > 0 ? "text-amber-600 dark:text-amber-400" : "text-foreground"
              }`}>
                {lowStock}
              </p>
          }
          <p className="text-[11px] text-muted-foreground mt-1">items con &lt; 10 uds.</p>
        </div>

        {/* Chart — orders by status */}
        <div
          className="col-span-2 lg:col-span-2 rounded-2xl bg-card ring-1 ring-border shadow-card p-4 cursor-default
                     hover:shadow-card-hover hover:-translate-y-px
                     transition-[box-shadow,transform] duration-200"
          style={{ animationDelay: "200ms" }}
        >
          <div className="flex items-center justify-between mb-3">
            <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">
              Pedidos por estado
            </p>
            {!loading && totalOrders > 0 && (
              <span className="tabular-nums text-[11px] font-semibold text-muted-foreground">
                {totalOrders} total
              </span>
            )}
          </div>
          {loading ? (
            <Skeleton className="h-[120px] w-full rounded-xl" />
          ) : (
            <ResponsiveContainer width="100%" height={120}>
              <BarChart
                data={ordersByStatus}
                barSize={28}
                margin={{ top: 0, right: 0, left: -28, bottom: 0 }}
              >
                <CartesianGrid
                  strokeDasharray="3 3"
                  vertical={false}
                  stroke="hsl(var(--border))"
                />
                <XAxis
                  dataKey="status"
                  tick={{ fontSize: 10, fill: "hsl(var(--muted-foreground))" }}
                  axisLine={false}
                  tickLine={false}
                  tickFormatter={v => STATUS_LABEL[v] ?? v}
                />
                <YAxis
                  tick={{ fontSize: 10, fill: "hsl(var(--muted-foreground))" }}
                  axisLine={false}
                  tickLine={false}
                  allowDecimals={false}
                />
                <Tooltip
                  content={<ChartTooltip />}
                  cursor={{ fill: "hsl(var(--muted) / 0.5)" }}
                />
                <Bar dataKey="count" radius={[5, 5, 0, 0]}>
                  {ordersByStatus.map(e => (
                    <Cell key={e.status} fill={STATUS_BAR[e.status] ?? "#94a3b8"} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          )}
        </div>

        {/* Recent orders */}
        <div
          className="col-span-2 lg:col-span-1 rounded-2xl bg-card ring-1 ring-border shadow-card p-4 cursor-default
                     hover:shadow-card-hover hover:-translate-y-px
                     transition-[box-shadow,transform] duration-200"
          style={{ animationDelay: "250ms" }}
        >
          <div className="flex items-center justify-between mb-3">
            <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">
              Recientes
            </p>
            <div className="flex h-5 w-5 items-center justify-center rounded-md bg-muted/60">
              <Clock className="h-3 w-3 text-muted-foreground/70" strokeWidth={2} />
            </div>
          </div>
          {loading ? (
            <div className="space-y-2.5">
              {Array.from({ length: 4 }).map((_, i) => (
                <Skeleton key={i} className="h-10 w-full rounded-lg" />
              ))}
            </div>
          ) : recent.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-8 text-center">
              <ShoppingBag className="h-7 w-7 text-muted-foreground/20 mb-2" />
              <p className="text-[12px] text-muted-foreground">Sin pedidos aún</p>
            </div>
          ) : (
            <div className="space-y-1">
              {recent.map((order, i) => (
                <div
                  key={order.id}
                  className="flex items-center justify-between rounded-lg px-2.5 py-2 hover:bg-muted/50 transition-colors duration-100"
                  style={{ animationDelay: `${250 + i * 30}ms` }}
                >
                  <div className="min-w-0">
                    <p className="text-[12px] font-semibold tabular-nums text-foreground leading-none">
                      #{order.id}
                    </p>
                    <p className="text-[10px] text-muted-foreground mt-0.5">
                      Usuario #{order.userId}
                    </p>
                  </div>
                  <div className="flex items-center gap-2 ml-2 shrink-0">
                    <span className="text-[12px] font-semibold tabular-nums text-foreground">
                      {formatCurrency(order.totalAmount, order.currency)}
                    </span>
                    <StatusBadge status={order.status} />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

/* ── Small KPI card ── */
function KpiCard({
  label,
  icon: Icon,
  loading,
  value,
  sub,
  delay = 0,
}: {
  label: string
  icon: ComponentType<{ className?: string; strokeWidth?: number }>
  loading: boolean
  value: number
  sub: string
  delay?: number
}) {
  return (
    <div
      className="rounded-2xl bg-card ring-1 ring-border shadow-card p-4 flex flex-col cursor-default
                 hover:shadow-card-hover hover:-translate-y-px
                 transition-[box-shadow,transform] duration-200"
      style={{ animationDelay: `${delay}ms` }}
    >
      <div className="flex items-center justify-between mb-3">
        <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">
          {label}
        </p>
        <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-muted ring-1 ring-border/60">
          <Icon className="h-3.5 w-3.5 text-muted-foreground" strokeWidth={1.75} />
        </div>
      </div>
      {loading
        ? <Skeleton className="h-8 w-14 mt-auto" />
        : <p className="text-2xl font-bold tabular-nums text-foreground mt-auto">{value}</p>
      }
      <p className="text-[11px] text-muted-foreground mt-1">{sub}</p>
    </div>
  )
}
