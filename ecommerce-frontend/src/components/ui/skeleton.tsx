import { cn } from "@/lib/utils"

/**
 * Shimmer skeleton (redesign-existing-projects: loading states that match layout shape)
 * Uses directional shimmer — Emil: unseen details compound
 */
export function Skeleton({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn("rounded-md skeleton-shimmer", className)}
      aria-hidden
      {...props}
    />
  )
}
