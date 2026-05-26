import { Badge } from "@/components/ui/badge"

type BadgeVariant = "success" | "destructive" | "warning" | "default" | "secondary" | "outline" | "blue"

const statusConfig: Record<string, { variant: BadgeVariant; label: string }> = {
  // Cart
  ACTIVE:       { variant: "success",     label: "Activo" },
  CHECKED_OUT:  { variant: "blue",        label: "Procesado" },
  ABANDONED:    { variant: "secondary",   label: "Abandonado" },
  // Order
  CREATED:      { variant: "blue",        label: "Creado" },
  PAID:         { variant: "success",     label: "Pagado" },
  CANCELLED:    { variant: "destructive", label: "Cancelado" },
  // Payment
  SUCCEEDED:    { variant: "success",     label: "Exitoso" },
  FAILED:       { variant: "destructive", label: "Fallido" },
  // Inventory movement
  CREDIT:       { variant: "success",     label: "Crédito" },
  DEBIT:        { variant: "destructive", label: "Débito" },
  RESERVE:      { variant: "warning",     label: "Reserva" },
  RELEASE:      { variant: "secondary",   label: "Liberado" },
}

interface StatusBadgeProps {
  status: string
}

export function StatusBadge({ status }: StatusBadgeProps) {
  const config = statusConfig[status] ?? { variant: "outline" as BadgeVariant, label: status }
  return <Badge variant={config.variant}>{config.label}</Badge>
}
