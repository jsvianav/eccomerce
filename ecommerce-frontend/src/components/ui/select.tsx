import * as React from "react"
import { createPortal } from "react-dom"
import { cn } from "@/lib/utils"
import { Check, ChevronDown } from "lucide-react"

interface SelectContextValue {
  value: string
  onValueChange: (val: string) => void
  open: boolean
  setOpen: (v: boolean) => void
  triggerRef: React.RefObject<HTMLButtonElement | null>
}

const SelectContext = React.createContext<SelectContextValue>({
  value: "",
  onValueChange: () => undefined,
  open: false,
  setOpen: () => undefined,
  triggerRef: { current: null },
})

interface SelectProps {
  value?: string
  onValueChange?: (val: string) => void
  children: React.ReactNode
  defaultValue?: string
}

export function Select({ value, onValueChange, children, defaultValue = "" }: SelectProps) {
  const [open, setOpen] = React.useState(false)
  const [internalValue, setInternalValue] = React.useState(defaultValue)
  const controlled = value !== undefined
  const triggerRef = React.useRef<HTMLButtonElement>(null)

  const currentValue = controlled ? (value ?? "") : internalValue
  const handleChange = (val: string) => {
    if (!controlled) setInternalValue(val)
    onValueChange?.(val)
    setOpen(false)
  }

  return (
    <SelectContext.Provider value={{ value: currentValue, onValueChange: handleChange, open, setOpen, triggerRef }}>
      <div className="relative">{children}</div>
    </SelectContext.Provider>
  )
}

export function SelectTrigger({
  className,
  children,
  id,
  ...props
}: React.HTMLAttributes<HTMLButtonElement> & { id?: string }) {
  const { open, setOpen, triggerRef } = React.useContext(SelectContext)
  return (
    <button
      ref={triggerRef}
      id={id}
      type="button"
      onClick={() => setOpen(!open)}
      className={cn(
        "flex h-9 w-full items-center justify-between rounded-lg",
        "border border-input bg-card px-3 py-2 text-sm",
        "transition-colors duration-150",
        "hover:border-ring/40",
        "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
        "aria-[invalid=true]:border-destructive/60",
        "disabled:cursor-not-allowed disabled:opacity-50",
        className
      )}
      {...props}
    >
      {children}
      <ChevronDown
        className={cn(
          "ml-2 h-3.5 w-3.5 text-muted-foreground shrink-0",
          "transition-transform duration-150 ease-out",
          open && "rotate-180"
        )}
        strokeWidth={2}
      />
    </button>
  )
}

export function SelectValue({
  placeholder,
  displayMap,
}: {
  placeholder?: string
  displayMap?: Record<string, string>
}) {
  const { value } = React.useContext(SelectContext)
  const display = displayMap ? (displayMap[value] ?? value) : value
  return (
    <span className={cn("truncate", !value && "text-muted-foreground/60")}>
      {display || placeholder}
    </span>
  )
}

export function SelectContent({
  className,
  children,
  ...props
}: React.HTMLAttributes<HTMLDivElement>) {
  const { open, setOpen, triggerRef } = React.useContext(SelectContext)
  const ref = React.useRef<HTMLDivElement>(null)
  const [coords, setCoords] = React.useState({ top: 0, left: 0, width: 0 })

  // Measure trigger position synchronously before paint
  React.useLayoutEffect(() => {
    if (open && triggerRef.current) {
      const rect = triggerRef.current.getBoundingClientRect()
      setCoords({ top: rect.bottom + 6, left: rect.left, width: rect.width })
    }
  }, [open, triggerRef])

  // Close on outside click (exclude trigger — trigger's own onClick handles toggle)
  React.useEffect(() => {
    const handleMouseDown = (e: MouseEvent) => {
      const target = e.target as Node
      const insideDropdown = ref.current?.contains(target)
      const insideTrigger = triggerRef.current?.contains(target)
      if (!insideDropdown && !insideTrigger) setOpen(false)
    }
    if (open) document.addEventListener("mousedown", handleMouseDown)
    return () => document.removeEventListener("mousedown", handleMouseDown)
  }, [open, setOpen, triggerRef])

  // Close on scroll outside the dropdown (so position doesn't desync)
  // but NOT when the user is scrolling the dropdown list itself
  React.useEffect(() => {
    if (!open) return
    const handleScroll = (e: Event) => {
      if (ref.current?.contains(e.target as Node)) return
      setOpen(false)
    }
    window.addEventListener("scroll", handleScroll, true)
    return () => window.removeEventListener("scroll", handleScroll, true)
  }, [open, setOpen])

  if (!open) return null

  return createPortal(
    <div
      ref={ref}
      style={{ top: coords.top, left: coords.left, width: coords.width }}
      className={cn(
        /* Portal — fixed so it escapes any overflow:auto parent (dialogs, cards) */
        "fixed z-[200]",
        "rounded-xl ring-1 ring-black/[0.06] dark:ring-white/[0.06]",
        "bg-card shadow-[0_8px_30px_-4px_rgb(0_0_0_/_0.12)] dark:shadow-[0_8px_30px_-4px_rgb(0_0_0_/_0.4)]",
        "animate-dropdown-in overflow-hidden",
        className
      )}
      {...props}
    >
      <div className="p-1 max-h-56 overflow-y-auto">{children}</div>
    </div>,
    document.body
  )
}

interface SelectItemProps extends React.HTMLAttributes<HTMLDivElement> {
  value: string
}

export function SelectItem({ className, value, children, ...props }: SelectItemProps) {
  const { value: selectedValue, onValueChange } = React.useContext(SelectContext)
  const isSelected = selectedValue === value

  return (
    <div
      role="option"
      aria-selected={isSelected}
      onClick={() => onValueChange(value)}
      className={cn(
        "relative flex cursor-pointer select-none items-center justify-between",
        "rounded-lg px-2.5 py-2 text-sm outline-none",
        "transition-colors duration-100",
        "hover:bg-accent hover:text-accent-foreground",
        isSelected && "bg-primary/8 text-primary font-medium",
        className
      )}
      {...props}
    >
      <span>{children}</span>
      {isSelected && <Check className="h-3.5 w-3.5 shrink-0" strokeWidth={2.5} />}
    </div>
  )
}
