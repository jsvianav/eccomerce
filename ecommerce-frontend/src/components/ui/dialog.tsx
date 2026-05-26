import * as React from "react"
import { cn } from "@/lib/utils"

interface DialogProps {
  open?: boolean
  onOpenChange?: (open: boolean) => void
  children: React.ReactNode
}

export function Dialog({ open, onOpenChange, children }: DialogProps) {
  React.useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") onOpenChange?.(false)
    }
    if (open) {
      document.addEventListener("keydown", handleKeyDown)
      document.body.style.overflow = "hidden"
    }
    return () => {
      document.removeEventListener("keydown", handleKeyDown)
      document.body.style.overflow = ""
    }
  }, [open, onOpenChange])

  if (!open) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Backdrop — subtle blur, not heavy black (high-end-visual-design) */}
      <div
        className="fixed inset-0 bg-black/30 backdrop-blur-[3px]"
        onClick={() => onOpenChange?.(false)}
        aria-hidden
      />
      {/* Panel — animate-dialog-in (Emil: nothing appears from nothing) */}
      <div className="relative z-50 w-full max-w-md animate-dialog-in">
        {children}
      </div>
    </div>
  )
}

export function DialogContent({
  className,
  children,
  ...props
}: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn(
        /* Double-bezel outer shell (high-end-visual-design §4.A) */
        "ring-1 ring-black/[0.06] dark:ring-white/[0.06] rounded-2xl p-[3px]",
        "bg-gradient-to-b from-white/70 to-white/40 dark:from-white/5 dark:to-white/[0.02]",
        className
      )}
      {...props}
    >
      {/* Inner core */}
      <div
        className={cn(
          "bg-card rounded-[calc(1rem-3px)] shadow-[0_20px_60px_-10px_rgb(0_0_0_/_0.18)]",
          "dark:shadow-[0_20px_60px_-10px_rgb(0_0_0_/_0.5)]",
          "px-6 py-5 max-h-[88vh] overflow-y-auto"
        )}
      >
        {children}
      </div>
    </div>
  )
}

export function DialogHeader({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return <div className={cn("mb-5", className)} {...props} />
}

export function DialogTitle({ className, ...props }: React.HTMLAttributes<HTMLHeadingElement>) {
  return (
    <h2
      className={cn(
        "text-[15px] font-semibold leading-tight tracking-tight text-foreground",
        className
      )}
      {...props}
    />
  )
}

export function DialogDescription({
  className,
  ...props
}: React.HTMLAttributes<HTMLParagraphElement>) {
  return (
    <p
      className={cn("text-[13px] text-muted-foreground mt-1 leading-relaxed", className)}
      {...props}
    />
  )
}

export function DialogFooter({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn(
        "flex justify-end gap-2 mt-5 pt-4 border-t border-border/60",
        className
      )}
      {...props}
    />
  )
}

export function DialogTrigger({
  children,
  onClick,
}: {
  children: React.ReactNode
  onClick?: () => void
}) {
  return <span onClick={onClick}>{children}</span>
}
