import * as React from "react"
import { cn } from "@/lib/utils"

interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?:
    | "default"
    | "blue"
    | "success"
    | "destructive"
    | "warning"
    | "secondary"
    | "outline"
}

/**
 * Semantic pastel badges (minimalist-ui §4 + §5)
 * Colors use CSS token vars so they adapt to dark mode.
 */
export function Badge({ className, variant = "default", ...props }: BadgeProps) {
  const base =
    "inline-flex items-center rounded-md px-2 py-0.5 text-[11px] font-semibold leading-none tracking-wide transition-colors"

  const variants: Record<string, string> = {
    default:
      "bg-[hsl(var(--badge-blue-bg))] text-[hsl(var(--badge-blue-text))]",
    blue:
      "bg-[hsl(var(--badge-blue-bg))] text-[hsl(var(--badge-blue-text))]",
    success:
      "bg-[hsl(var(--badge-green-bg))] text-[hsl(var(--badge-green-text))]",
    destructive:
      "bg-[hsl(var(--badge-red-bg))] text-[hsl(var(--badge-red-text))]",
    warning:
      "bg-[hsl(var(--badge-amber-bg))] text-[hsl(var(--badge-amber-text))]",
    secondary:
      "bg-[hsl(var(--badge-slate-bg))] text-[hsl(var(--badge-slate-text))]",
    outline:
      "bg-transparent ring-1 ring-border text-foreground",
  }

  return (
    <span className={cn(base, variants[variant], className)} {...props} />
  )
}
