import * as React from "react"

interface PageHeaderProps {
  title: string
  description?: string
  action?: React.ReactNode
}

export function PageHeader({ title, description, action }: PageHeaderProps) {
  return (
    <div className="flex items-start justify-between mb-6">
      <div>
        <h1 className="text-[20px] font-bold tracking-tight text-foreground leading-none">{title}</h1>
        {description && (
          <p className="text-[13px] text-muted-foreground mt-1.5 leading-snug max-w-[480px]">{description}</p>
        )}
      </div>
      {action && <div className="ml-4 shrink-0 mt-0.5">{action}</div>}
    </div>
  )
}
